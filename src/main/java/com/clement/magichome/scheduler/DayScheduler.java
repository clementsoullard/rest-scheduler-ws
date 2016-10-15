package com.clement.magichome.scheduler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.clement.magichome.object.DatePriveDeTele;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.CreditTask;
import com.clement.magichome.service.FileService;

@Configuration
@EnableScheduling
/**
 * This class credit some minutes for the TV every day.
 * 
 * @author Clément
 *
 */

public class DayScheduler {

	static final Logger LOG = LoggerFactory.getLogger(DayScheduler.class);

	public final static int TIME_IS_PASSED = -1;

	DateFormat df = new SimpleDateFormat("EEEEE d MMM", Locale.FRENCH);
	DateFormat dfLcd = new SimpleDateFormat("EEE d HH:mm", Locale.FRENCH);

	@Resource
	private BonPointDaoImpl bonPointDaoImpl;

	@Resource
	private FileService fileService;

	@Autowired
	private TaskScheduler taskScheduler;

	/**
	 * The future credit information that will be displayed, it probably make
	 * sense to merge it into a single class.
	 */

	/** Credit task */
	private CreditTask creditTask;

	/** */
	private ScheduledFuture<CreditTask> scheduledFuture;

	/** Every day we check at what time the time for tv is granted */
	@Scheduled(cron = "0 0 * * * *")
	public void computeNextOccurenceOfCredit() throws IOException {

		Calendar calendar = Calendar.getInstance();
		DatePriveDeTele priveDeTeleUntil = bonPointDaoImpl.maxDate();

		if (priveDeTeleUntil != null && priveDeTeleUntil.getMaxDate() != null
				&& priveDeTeleUntil.getMaxDate().after(calendar.getTime())) {
			calendar.setTime(priveDeTeleUntil.getMaxDate());
			calendar.add(Calendar.DATE, 1);
		}
		LOG.debug("Checking if task is here at  " + calendar.getTime());

		/* **/
		int minutesAllowed = -1;

		/**
		 * dans le cas ou nous sommes privé de tele alors le calendrier commence
		 * à la dernière fois (si quelque chose était déjà programmé
		 */
		if (bonPointDaoImpl.isPriveDeTele() && creditTask != null) {
			calendar.setTime(creditTask.getExecutionDate());
			LOG.debug("Une punition s'est produite dans l'entre deux. On recommence depuis "
					+ df.format(calendar.getTime()));
			scheduledFuture.cancel(false);
			creditTask = null;
		}
		/**
		 * First we check on the current day if there is a credit possible.
		 */
		minutesAllowed = checkTimeToGiveWithoutPunition(calendar);

		/**
		 * This is to avoid that the calendar loop eternally, if no day ever
		 * give some minutes to watch the TV.
		 */
		int numberOfDayIntheFuture = 0;

		while (minutesAllowed < 0 && numberOfDayIntheFuture < 20) {
			calendar.add(Calendar.DATE, 1);
			minutesAllowed = checkTimeToGiveWithoutPunition(calendar);
			numberOfDayIntheFuture++;
		}

		/**
		 * The future date is determined here.
		 */
		Date futureDate = calendar.getTime();

		/**
		 * Here the minutes modifier for the punishement computed are
		 */
		Integer minuteModifierForBonPoint = bonPointDaoImpl.pointToDistribute(-minutesAllowed, minutesAllowed / 2);
		LOG.debug("There would be a modifier of " + minuteModifierForBonPoint + " on " + futureDate);

		int minutesGranted = minutesAllowed + minuteModifierForBonPoint;
		/** We will schedule something only if something has not been started */
		if (creditTask == null) {
			LOG.info("Programmation de " + minutesGranted + " m  pour le " + futureDate);
			creditTask = new CreditTask(fileService, bonPointDaoImpl, this);
			creditTask.setMinutes(minutesGranted);
			creditTask.setMinutesModifier(minuteModifierForBonPoint);
			creditTask.setExecutionDate(futureDate);

			scheduledFuture = (ScheduledFuture<CreditTask>) taskScheduler.schedule(creditTask, futureDate);
			fileService.writeSecondLine("" + minutesGranted + "m " + dfLcd.format(futureDate));
		} else {
			creditTask.setMinutes(minutesGranted);
			creditTask.setMinutesModifier(minuteModifierForBonPoint);
			fileService.writeSecondLine("" + minutesGranted + "m " + dfLcd.format(creditTask.getExecutionDate()));
			LOG.debug("Already scheduled task, no need to schedule another one just updating the minutes");
		}

	}

	/**
	 * Every night at 2 o'clock the switch goes to Off, no matter what happened
	 * before
	 */
	@Scheduled(cron = "0 0 2 * * *")
	public void switchOffInNight() throws IOException {
		fileService.writeCountDown(-1);
	}

	public CreditTask getCreditTask() {
		return creditTask;
	}

	public void setCreditTask(CreditTask creditTask) {
		this.creditTask = creditTask;
	}

	/**
	 * Depending on the day of the week, some minutes would be granted it is
	 * returned here not considering the potential punishment.
	 * 
	 * @param dayOfWeek
	 * @param calendarDateToGrantMinutes
	 * @return the number of minutes to be granted. If the time where it should
	 *         be granted is passed, then -1 is returned.
	 */
	private int checkTimeToGiveWithoutPunition(Calendar calendarDateToGrantMinutes) {

		int dayOfWeek = calendarDateToGrantMinutes.get(Calendar.DAY_OF_WEEK);
		int minutesAllowed = 0;

		/** FIXME, test only */
		if (false) {
			calendarDateToGrantMinutes.add(Calendar.MINUTE, 2);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			LOG.debug("Checking for date " + df.format(calendarDateToGrantMinutes.getTime()) + " minutes allowed "
					+ minutesAllowed);
			return 1;
		}

		//
		if (dayOfWeek == Calendar.WEDNESDAY) {
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 16);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		} else if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
			// During day of the week, this should change during holiday
			// #see
			// https://tvscheduler.atlassian.net/secure/RapidBoard.jspa?rapidView=1&view=detail&selectedIssue=TS-30
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 11);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			// minutesAllowed = 60;
			// During school, there are no minute allowed.
			minutesAllowed = -1;
		} else if (dayOfWeek == Calendar.SATURDAY) {
			//
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 11);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		} else if (dayOfWeek >= Calendar.SUNDAY) {
			//
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 11);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		}
		/**
		 * In the case we are after the time when the minutes have been granted,
		 * we should return -1;
		 */
		if (new Date().after(calendarDateToGrantMinutes.getTime())) {
			LOG.debug("The current date is after " + calendarDateToGrantMinutes.getTime()
					+ ", we must check the next day.");
			minutesAllowed = -1;
		}

		else if (bonPointDaoImpl.isPriveDeTele() && minutesAllowed > 0) {
			LOG.debug("The guy is deprived , we must check the next day.");
			bonPointDaoImpl.remove1DayPriveDeTele(calendarDateToGrantMinutes.getTime());
			minutesAllowed = -1;
		}

		LOG.debug("Checking for date " + df.format(calendarDateToGrantMinutes.getTime()) + " minutes allowed "
				+ minutesAllowed);
		return minutesAllowed;
	}

}

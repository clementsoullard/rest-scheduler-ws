package com.clement.magichome.scheduler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.DatePriveDeTele;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.FileService;
import com.clement.magichome.service.VacancesService;
import com.clement.magichome.service.VacancesService.Profile;
import com.clement.magichome.task.AllowDenyUserTask;
import com.clement.magichome.task.CreditTimeForScreenTask;

@Configuration
@EnableScheduling
/**
 * This class credit some minutes for the TV every day.
 * 
 * @author Clément
 *
 */

public class DayScheduler {

	public static final int SCHEDULER_OFF = -1;

	private static final int SCHEDULER_ON = -2;

	static final Logger LOG = LoggerFactory.getLogger(DayScheduler.class);

	public final static int TIME_IS_PASSED = SCHEDULER_OFF;

	private DateFormat df = new SimpleDateFormat("EEEEE d MMM", Locale.FRENCH);

	private DateFormat dfLcd = new SimpleDateFormat("EEE d HH:mm", Locale.FRENCH);

	@Resource
	private PropertyManager propertyManager;

	@Resource
	private BonPointDaoImpl bonPointDaoImpl;

	@Resource
	private VacancesService vacancesService;

	@Resource
	private FileService fileService;

	@Autowired
	private TaskScheduler taskScheduler;

	/**
	 * The future credit information that will be displayed, it probably make
	 * sense to merge it into a single class.
	 */

	/** Credit task */
	private CreditTimeForScreenTask creditTask;

	/** */
	private ScheduledFuture<CreditTimeForScreenTask> scheduledFuture;
	/** */
	private ScheduledFuture<AllowDenyUserTask> scheduledFutureAllowCesar;

	/** Every day we check at what time the time for tv is granted */
	@Scheduled(cron = "0 0 * * * *")
	public void computeNextOccurenceOfCredit() throws IOException {
		if (!propertyManager.getMonitorTVAndPC()) {
			return;
		}
		Calendar calendar = Calendar.getInstance();
		DatePriveDeTele priveDeTeleUntil = bonPointDaoImpl.maxDate();

		if (priveDeTeleUntil != null && priveDeTeleUntil.getMaxDate() != null
				&& priveDeTeleUntil.getMaxDate().after(calendar.getTime())) {
			calendar.setTime(priveDeTeleUntil.getMaxDate());
			calendar.add(Calendar.DATE, 1);
		}
		LOG.debug("Checking if task is here at  " + calendar.getTime());

		/* **/
		int minutesAllowed = SCHEDULER_OFF;

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
			creditTask = new CreditTimeForScreenTask(fileService, bonPointDaoImpl, this);
			creditTask.setMinutes(minutesGranted);
			creditTask.setMinutesModifier(minuteModifierForBonPoint);
			creditTask.setExecutionDate(futureDate);

			scheduledFuture = (ScheduledFuture<CreditTimeForScreenTask>) taskScheduler.schedule(creditTask, futureDate);
			fileService.writeSecondLine("Prochain crédit:" + minutesGranted + "mn " + dfLcd.format(futureDate));
		} else {
			creditTask.setMinutes(minutesGranted);
			creditTask.setMinutesModifier(minuteModifierForBonPoint);
			fileService.writeSecondLine("" + minutesGranted + "mn " + dfLcd.format(creditTask.getExecutionDate()));
			LOG.debug("Already scheduled task, no need to schedule another one just updating the minutes");
		}

	}

	/**
	 * Every night at 2 o'clock the switch goes to Off, no matter what happened
	 * before
	 */
	@Scheduled(cron = "0 0 2 * * *")
	public void switchOffInNight() throws IOException {
		if (!propertyManager.getMonitorTVAndPC()) {
			return;
		}

		fileService.writeCredit(SCHEDULER_OFF);
	}

	/**
	 * 10 the switch goes On, if Cesar is in school.
	 */
	@Scheduled(cron = "0 0 10 * * *")
	public void switchOnForCris() throws IOException {
	
		if (isWorkingDay()) {
			fileService.writeCredit(SCHEDULER_ON);
			LOG.info("Allumage de la TV pour Cris");
		}
	}

	/**
	 * Allow Cesar only on weekend
	 */
	@Scheduled(cron = "0 0 14 * * SAT")
	private void allowCesar() {
		if (scheduledFutureAllowCesar != null) {
			scheduledFutureAllowCesar.cancel(false);
		}
		LOG.debug("Autorisation de Cesar");
		AllowDenyUserTask allowDenyUserTask = new AllowDenyUserTask(this, propertyManager, true, "cesar");
		allowDenyUserTask.run();
	}

	/**
	 * Allow Cesar only on weekend
	 */
	@Scheduled(cron = "0 0 17 * * SUN")
	private void denyPCCesar() {
		if (scheduledFutureAllowCesar != null) {
			scheduledFutureAllowCesar.cancel(false);
		}
		LOG.debug("Fin de l'autorisation d'utilisation  de Cesar");
		AllowDenyUserTask allowDenyUserTask = new AllowDenyUserTask(this, propertyManager, false, "cesar");
		allowDenyUserTask.run();

	}

	/**
	 * 21 PM TV goes on if it is a working day
	 */
	@Scheduled(cron = "0 0 21 * * *")
	public void switchOnInNight() throws IOException {
		if (isWorkingDay()) {
			fileService.writeCredit(SCHEDULER_ON);
			LOG.info("Allumage de la TV pour Cris");
		}
	}

	/**
	 * Every working day tv goes off at 5PM, no matter what happened before
	 */
	@Scheduled(cron = "0 0 17 * * *")
	public void switchOffForCris() throws IOException {
		if (isWorkingDay()) {
			LOG.info("Extinction de la TV pour le retour de César");
			fileService.writeCredit(SCHEDULER_OFF);
		}
	}

	public CreditTimeForScreenTask getCreditTask() {
		return creditTask;
	}

	public void setCreditTask(CreditTimeForScreenTask creditTask) {
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

		// int dayOfWeek = calendarDateToGrantMinutes.get(Calendar.DAY_OF_WEEK);
		Date dateToCheck = calendarDateToGrantMinutes.getTime();

		VacancesService.Profile profile = vacancesService.getProfile(dateToCheck);
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

		if (profile == Profile.HOLIDAY) {
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 14);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		}

		else if (profile == Profile.WEDNESDAY) {
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 16);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		} else if (profile == Profile.WORKDAY) {
			// During day of the week, this should change during holiday
			// #see
			// https://tvscheduler.atlassian.net/secure/RapidBoard.jspa?rapidView=1&view=detail&selectedIssue=TS-30
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 11);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			// minutesAllowed = 60;
			// During school, there are no minute allowed.
			minutesAllowed = SCHEDULER_OFF;
		} else if (profile == Profile.SATURDAY) {
			//
			calendarDateToGrantMinutes.set(Calendar.HOUR_OF_DAY, 14);
			calendarDateToGrantMinutes.set(Calendar.MINUTE, 00);
			calendarDateToGrantMinutes.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		}
		if (profile == Profile.SUNDAY) {
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
		if (new Date().after(calendarDateToGrantMinutes.getTime()))

		{
			LOG.debug("The current date is after " + calendarDateToGrantMinutes.getTime()
					+ ", we must check the next day.");
			minutesAllowed = SCHEDULER_OFF;
		}

		else if (bonPointDaoImpl.isPriveDeTele() && minutesAllowed > 0) {
			LOG.debug("The guy is deprived , we must check the next day.");
			bonPointDaoImpl.remove1DayPriveDeTele(calendarDateToGrantMinutes.getTime());
			minutesAllowed = SCHEDULER_OFF;
		}

		LOG.debug("Checking for date " + df.format(calendarDateToGrantMinutes.getTime()) + " minutes allowed "
				+ minutesAllowed);
		return minutesAllowed;
	}

	/**
	 * 
	 * @return true if we are a working day and cris can watch TV during the
	 *         day.
	 */
	boolean isWorkingDay() {
		Profile profile = vacancesService.getProfile(new Date());
		return profile == Profile.WORKDAY;
	}

	/**
	 * 
	 * @param originalCreditTask
	 */
	public void retryIn10Minutes(CreditTimeForScreenTask originalCreditTask) {
		Calendar calendar = Calendar.getInstance();
		/*
		 * If we are after 20h00 then nothing is done
		 */
		if (calendar.get(Calendar.HOUR_OF_DAY) >= 20) {
			LOG.debug(
					"No sufficient action were performed canceling right to watch the TV for that day cancelling retrying");
			scheduledFuture.cancel(false);
			creditTask = null;
			return;
		}

		LOG.debug("Not sufficient actions to grant rights on tv, retrying in 10 minutes");
		creditTask = new CreditTimeForScreenTask(fileService, bonPointDaoImpl, this);
		creditTask.setMinutes(originalCreditTask.getMinutes());
		creditTask.setMinutesModifier(originalCreditTask.getMinutesModifier());
		calendar.add(Calendar.MINUTE, 10);
		Date futureDate = calendar.getTime();
		creditTask.setExecutionDate(futureDate);
		scheduledFuture = (ScheduledFuture<CreditTimeForScreenTask>) taskScheduler.schedule(creditTask, futureDate);
	}

	/**
	 * 
	 * @param originalCreditTask
	 */
	public void retryIn10Minutes(AllowDenyUserTask allowTask) {
		Calendar calendar = Calendar.getInstance();
		LOG.debug("PC was not available to trigger allowing user");
		calendar.add(Calendar.MINUTE, 10);
		Date futureDate = calendar.getTime();
		allowTask.setExecutionDate(futureDate);
		scheduledFutureAllowCesar = (ScheduledFuture<AllowDenyUserTask>) taskScheduler.schedule(allowTask, futureDate);
	}
}

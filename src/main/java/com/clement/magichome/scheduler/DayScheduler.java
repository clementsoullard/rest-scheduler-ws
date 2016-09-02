package com.clement.magichome.scheduler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.clement.magichome.object.FutureCredit;
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

	DateFormat df = new SimpleDateFormat("EEEEE d MMM");

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
	private FutureCredit futureCredit;

	/** Credit task */
	private CreditTask creditTask;
	/** */
	private ScheduledFuture<CreditTask> scheduledFuture;

	/** Every day we check at what time the time for tv is granted */
	@Scheduled(cron = "0 0 * * * *")
	public void scheduleForTheDay() throws IOException {

		LOG.debug("Checking if task is here at  " + new Date());

		Calendar calendar = Calendar.getInstance();

		/* **/
		int minutesAllowed = -1;

		/**
		 * dans le cas ou nous sommes privé de tele alors le calendrier commence
		 * à la dernière fois (si quelque chose était déjà programmé
		 */
		if (bonPointDaoImpl.isPriveDeTele() && futureCredit != null) {
			calendar.setTime(futureCredit.getDateOfCredit());
			LOG.debug("Une punition s'est produite dans l'entre deux. On recommence depuis "
					+ df.format(calendar.getTime()));
			scheduledFuture.cancel(false);
			creditTask = null;
			futureCredit = null;
		}

		minutesAllowed = checkTimeToGive(calendar);

		/**
		 * This is to avoid that the calendar loop eternally, if no day ever
		 * give some minutes to watch the TV.
		 */
		int numberOfDayIntheFuture = 0;

		while (minutesAllowed < 0 && numberOfDayIntheFuture < 20) {
			calendar.add(Calendar.DATE, 1);
			minutesAllowed = checkTimeToGive(calendar);
			numberOfDayIntheFuture++;
		}

		/**
		 * The future date is determined here.
		 */
		Date futureDate = calendar.getTime();

		Integer minuteModifierForBonPoint = bonPointDaoImpl.pointToDistribute(-minutesAllowed, minutesAllowed / 2);
		LOG.debug("There would be a modifier of " + minuteModifierForBonPoint + " on " + futureDate);

		int minutesGranted = minutesAllowed + minuteModifierForBonPoint;
		/** Create the future task */
		if (futureCredit == null) {
			futureCredit = new FutureCredit();
			futureCredit.setAmountOfCreditInMinutes(minutesGranted);
			futureCredit.setDateOfCredit(futureDate);
		} else {
			futureCredit.setAmountOfCreditInMinutes(minutesGranted);
			LOG.debug("Already future schedule, no need to schedule another one just updating the minutes");
		}
		/** We will schedule something only if something has not been started */
		if (creditTask == null) {

			LOG.debug("Schedule " + minutesGranted + " mn  on " + futureDate);
			creditTask = new CreditTask(fileService, bonPointDaoImpl, this);
			creditTask.setMinutes(minutesGranted);
			/** The switch off date is the day after */
			Calendar switchOffDate = Calendar.getInstance();
			switchOffDate.add(Calendar.DATE, 1);
			switchOffDate.set(Calendar.HOUR_OF_DAY, 1);
			switchOffDate.set(Calendar.MINUTE, 0);

			CreditTask creditTaskSwitchOff = new CreditTask(fileService, bonPointDaoImpl, this);
			creditTaskSwitchOff.setMinutes(-1);
			scheduledFuture = (ScheduledFuture<CreditTask>) taskScheduler.schedule(creditTask, futureDate);
			taskScheduler.schedule(creditTaskSwitchOff, switchOffDate.getTime());
		} else {
			creditTask.setMinutes(minutesGranted);
			LOG.debug("Already scheduled task, no need to schedule another one just updating the minutes");
		}

	}

	public CreditTask getCreditTask() {
		return creditTask;
	}

	public void setCreditTask(CreditTask creditTask) {
		this.creditTask = creditTask;
	}

	/**
	 * Depending on the day of the week, some minutes would be granted.
	 * 
	 * @param dayOfWeek
	 * @param calendarDateToGrantMinutes
	 * @return the number of minutes to be granted. If the time where it should
	 *         be granted is passed, then -1 is returned.
	 */
	private int checkTimeToGive(Calendar calendarDateToGrantMinutes) {

		int dayOfWeek = calendarDateToGrantMinutes.get(Calendar.DAY_OF_WEEK);
		int minutesAllowed = 0;

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
			bonPointDaoImpl.remove1DayPriveDeTele();
			minutesAllowed = -1;
		}
		LOG.debug("Checking for date " + df.format(calendarDateToGrantMinutes.getTime()) + " minutes allowed "
				+ minutesAllowed);
		return minutesAllowed;
	}

	public FutureCredit getFutureCredit() {
		return futureCredit;
	}

	public void setFutureCredit(FutureCredit futureCredit) {
		this.futureCredit = futureCredit;
	}

}

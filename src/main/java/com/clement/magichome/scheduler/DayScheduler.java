package com.clement.magichome.scheduler;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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

	@Resource
	private BonPointDaoImpl bonPointDaoImpl;

	@Resource
	private FileService fileService;

	@Autowired
	private TaskScheduler taskScheduler;

	private FutureCredit futureCredit;
	private CreditTask creditTask;

	/** Every day we check at what time the time for tv is granted */
	@Scheduled(cron = "0 * * * * *")
	public void scheduleForTheDay() throws IOException {

		LOG.debug("Checking if task is here at  " + new Date());

		Calendar calendar = Calendar.getInstance();
		//
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int minutesAllowed = 0;

		minutesAllowed = checkTimeToGive(dayOfWeek, calendar);

		/** If date is passed, we should check the other day */
		if (minutesAllowed == TIME_IS_PASSED) {
			LOG.debug("Time is passed checking next day");
			calendar.add(Calendar.DATE, 1);
			dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			minutesAllowed = checkTimeToGive(dayOfWeek, calendar);
		}

		Date futureDate = calendar.getTime();

		LOG.debug("scheduling change at  " + futureDate);
		Integer minuteModifierForBonPoint = bonPointDaoImpl.pointToDistribute(-minutesAllowed, minutesAllowed / 2);

		int minutesGranted = minutesAllowed + minuteModifierForBonPoint;
		/** */
		if (futureCredit == null) {
			futureCredit = new FutureCredit();
			futureCredit.setAmountOfCreditInMinutes(minutesGranted);
			futureCredit.setDateOfCredit(futureDate);
		} else {
			futureCredit.setAmountOfCreditInMinutes(minutesGranted);
			LOG.debug("Already prvisionned future, no need to schedule another one just updating the minutes");
		}

		if (creditTask == null) {
			LOG.debug("Scheddule another " + minutesGranted + " on " + futureDate);
			creditTask = new CreditTask(fileService, bonPointDaoImpl, this);
			creditTask.setMinutes(minutesGranted);
			taskScheduler.schedule(creditTask, futureDate);
		} else {
			creditTask.setMinutes(minutesGranted);
			LOG.debug("Already schedulled task, no need to schedule another one just updating the minutes");
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
	private int checkTimeToGive(int dayOfWeek, Calendar calendarDateToGrantMinutes) {
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
			minutesAllowed = 60;
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
			return -1;
		}

		return minutesAllowed;
	}

	// /**
	// * Every day the TV stops at midnight.
	// */
	// @Scheduled(cron = "0 1 1 * * MON-FRI")
	// public void closeTv() throws IOException {
	// fileService.writeCountDown(-1);
	// }
	//
	// /**
	// * Every day the TV stops at midnight.
	// */
	// @Scheduled(cron = "1 0 11 * * MON-TUE", zone = "Europe/Paris")
	// public void creditTvVacances() throws IOException {
	// Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
	// fileService.writeCountDown(60 * (60 + minutes));
	// bonPointDaoImpl.removePunition(minutes);
	// }
	//
	// /**
	// * Every day the TV stops at midnight.
	// */
	// @Scheduled(cron = "1 0 11 * * THU-FRI", zone = "Europe/Paris")
	// public void creditTvVacances2() throws IOException {
	// Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
	// fileService.writeCountDown(60 * (60 + minutes));
	// bonPointDaoImpl.removePunition(minutes);
	// }
	//
	// /**
	// * Credited on Wednesday.
	// */
	// @Scheduled(cron = "0 1 14 * * WED", zone = "Europe/Paris")
	// public void giveCreditForWednesday() throws IOException {
	// Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
	// fileService.writeCountDown(60 * (60 + minutes));
	// bonPointDaoImpl.removePunition(minutes);
	// }
	//
	// /**
	// * Credited on Saturday.
	// */
	// @Scheduled(cron = "0 1 11 * * SAT", zone = "Europe/Paris")
	// public void giveCreditForWeekEnd() throws IOException {
	// Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
	// fileService.writeCountDown(60 * (60 + minutes));
	// bonPointDaoImpl.removePunition(minutes);
	// }
	//
	// /**
	// * Credited on Saturday.
	// */
	// @Scheduled(cron = "0 1 11 * * SUN", zone = "Europe/Paris")
	// public void giveCreditForWeekEndSunday() throws IOException {
	// Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
	// fileService.writeCountDown(60 * (60 + minutes));
	// bonPointDaoImpl.removePunition(minutes);
	// }
	public FutureCredit getFutureCredit() {
		return futureCredit;
	}

	public void setFutureCredit(FutureCredit futureCredit) {
		this.futureCredit = futureCredit;
	}

}

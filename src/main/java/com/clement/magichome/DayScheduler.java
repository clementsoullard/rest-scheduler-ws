package com.clement.magichome;

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
import org.springframework.stereotype.Repository;

import com.clement.magichome.object.FutureCredit;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.CreditTask;

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

	@Resource
	private BonPointDaoImpl bonPointDaoImpl;

	@Resource
	private FileService fileService;

	@Autowired
	private TaskScheduler taskScheduler;

	private static FutureCredit futureCredit;

	@Scheduled(cron = "0 * * * * *")
	public void scheduleForTheDay() throws IOException {

		LOG.debug("Checking if task is here at  " + new Date());

		CreditTask creditTask = new CreditTask(60, fileService, bonPointDaoImpl);

		futureCredit = new FutureCredit();

		Calendar calendar = Calendar.getInstance();
		//
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int minutesAllowed = 0;
		//
		if (dayOfWeek == Calendar.WEDNESDAY) {
			calendar.set(Calendar.HOUR_OF_DAY, 16);
			calendar.set(Calendar.MINUTE, 00);
			calendar.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		} else if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
			// During day of the week, this should change during holiday
			// #see
			// https://tvscheduler.atlassian.net/secure/RapidBoard.jspa?rapidView=1&view=detail&selectedIssue=TS-30
			calendar.set(Calendar.HOUR_OF_DAY, 11);
			calendar.set(Calendar.MINUTE, 00);
			calendar.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		} else if (dayOfWeek == Calendar.SATURDAY) {
			//
			calendar.set(Calendar.HOUR_OF_DAY, 11);
			calendar.set(Calendar.MINUTE, 00);
			calendar.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		} else if (dayOfWeek >= Calendar.SUNDAY) {
			//
			calendar.set(Calendar.HOUR_OF_DAY, 11);
			calendar.set(Calendar.MINUTE, 00);
			calendar.set(Calendar.SECOND, 00);
			minutesAllowed = 60;
		}

		Date futureDate = calendar.getTime();
		LOG.debug("scheduling change at  " + futureDate);
		Integer minuteModifierForBonPoint = bonPointDaoImpl.pointToDistribute(-minutesAllowed, minutesAllowed / 2);
		futureCredit.setAmountOfCreditInMinutes(minutesAllowed + minuteModifierForBonPoint);
		futureCredit.setDateOfCredit(futureDate);
		taskScheduler.schedule(creditTask, futureDate);
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
	public static FutureCredit getFutureCredit() {
		return futureCredit;
	}
}

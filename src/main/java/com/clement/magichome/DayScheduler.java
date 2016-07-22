package com.clement.magichome;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Configuration
@EnableScheduling
/**
 * This class credit some minutes for the TV.
 * 
 * @author Clément
 *
 */
@Repository
public class DayScheduler {

	static final Logger LOG = LoggerFactory.getLogger(DayScheduler.class);

	@Resource
	FileService fileService;

	/**
	 * Every day the TV stops at midnight.
	 */
	@Scheduled(cron = "0 1 1 * * MON-FRI")
	public void closeTv() throws IOException {
		fileService.writeCountDown(-1);
	}

	/**
	 * Every day the TV stops at midnight.
	 */
	@Scheduled(cron = "1 0 6 * * MON-FRI", zone = "Europe/Paris")
	public void creditTvTest() throws IOException {
		LOG.info("Test credit 5mn sec");
		fileService.writeCountDown(60 * 60);
	}

	/**
	 * Credited on Wednesday.
	 */
	@Scheduled(cron = "0 1 1 * * WED")
	public void giveCreditForWednesday() throws IOException {
		fileService.writeCountDown(30 * 60);
	}

	/**
	 * Credited on Saturday.
	 */
	@Scheduled(cron = "0 1 1 * * SAT")
	public void giveCreditForWeekEnd() throws IOException {
		fileService.writeCountDown(60 * 60);
	}

	/**
	 * Credited on Saturday.
	 */
	@Scheduled(cron = "0 1 1 * * SUN")
	public void giveCreditForWeekEndSunday() throws IOException {
		fileService.writeCountDown(60 * 60);
	}

}

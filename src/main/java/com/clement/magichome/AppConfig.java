package com.clement.magichome;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AppConfig {

	/**
	 * Every day the TV stops at midnight.
	 */
	@Scheduled(cron = "* * 0 * * MON-FRI")
	public void closeTv() throws IOException {
		File file = new File(Constant.path);
		PrintStream ps = new PrintStream(file);
		ps.print(-1);

	}

	/**
	 * Credited with .
	 */
	@Scheduled(cron = "* * 1 * * WED")
	public void giveCreditForWednesday() throws IOException {
		System.out.println("Credit Mercredi");
		File file = new File(Constant.path);
		PrintStream ps = new PrintStream(file);
		ps.print(30 * 60);
	}
	/**
	 * Credited with .
	 */
	@Scheduled(cron = "* * 1 * * SAT-SUN")
	public void giveCreditForWeekEnd() throws IOException {
		System.out.println("Credit samedi dimanche");
		File file = new File(Constant.path);
		PrintStream ps = new PrintStream(file);
		ps.print(60 * 60);
	}

}
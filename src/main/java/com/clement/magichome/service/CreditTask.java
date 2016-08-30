package com.clement.magichome.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.scheduler.DayScheduler;

/**
 * This will credit 
 * @author Clement_Soullard
 *
 */
public class CreditTask implements Runnable {

	static final Logger LOG = LoggerFactory.getLogger(CreditTask.class);

	/** The number of minutes that will be granted */

	private Integer minutes;

	private FileService fileService;

	private BonPointDaoImpl bonPointDaoImpl;

	private DayScheduler dayScheduler;

	public CreditTask(FileService fileService, BonPointDaoImpl bonPointDaoImpl, DayScheduler dayScheduler) {
		this.fileService = fileService;
		this.bonPointDaoImpl = bonPointDaoImpl;
	}

	@Override
	public void run() {
		LOG.debug("Credit " + minutes);
		fileService.writeCountDown(minutes);
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		bonPointDaoImpl.removePunition(minutes);
		dayScheduler.setFutureCredit(null);
		dayScheduler.setCreditTask(null);
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

}

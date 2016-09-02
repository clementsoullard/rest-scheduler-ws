package com.clement.magichome.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.scheduler.DayScheduler;

/**
 * This will credit
 * 
 * @author Clement_Soullard
 *
 */
public class CreditTask implements Runnable {

	static final Logger LOG = LoggerFactory.getLogger(CreditTask.class);

	/** The number of minutes that will be granted */

	private Integer minutes;

	private Date executionDate;

	private FileService fileService;

	private BonPointDaoImpl bonPointDaoImpl;

	private DayScheduler dayScheduler;

	public CreditTask(FileService fileService, BonPointDaoImpl bonPointDaoImpl, DayScheduler dayScheduler) {
		this.fileService = fileService;
		this.bonPointDaoImpl = bonPointDaoImpl;
		this.dayScheduler = dayScheduler;
	}

	@Override
	public void run() {
		LOG.debug("Credit " + minutes);
		fileService.writeCountDown(minutes);
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		bonPointDaoImpl.removePunition(minutes);
		dayScheduler.setCreditTask(null);
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public Integer getMinutes() {
		return minutes;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

}

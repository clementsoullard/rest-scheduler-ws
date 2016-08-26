package com.clement.magichome.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.FileService;

/**
 * 
 * @author Clement_Soullard
 *
 */
public class CreditTask implements Runnable {

	static final Logger LOG = LoggerFactory.getLogger(CreditTask.class);

	/** The number of minutes that will be granted */

	private Integer minutes;

	private FileService fileService;

	private BonPointDaoImpl bonPointDaoImpl;

	public CreditTask(Integer minutes, FileService fileService, BonPointDaoImpl bonPointDaoImpl) {
		this.minutes = minutes;
		this.fileService = fileService;
		this.bonPointDaoImpl = bonPointDaoImpl;
	}

	@Override
	public void run() {
		LOG.debug("Credit " + minutes);
		fileService.writeCountDown(minutes);
		bonPointDaoImpl.removePunition(minutes);
	}

}

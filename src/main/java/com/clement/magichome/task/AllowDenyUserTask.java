package com.clement.magichome.task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.scheduler.DayScheduler;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.FileService;

/**
 * This will credit
 * 
 * @author Clement_Soullard
 *
 */
public class AllowDenyUserTask implements Runnable {

	static final Logger LOG = LoggerFactory.getLogger(AllowDenyUserTask.class);

	/** The number of minutes that will be granted */

	private Date executionDate;

	private DayScheduler dayScheduler;

	private PropertyManager propertyManager;

	private boolean success = false;

	private boolean enable = false;

	private String user;

	public AllowDenyUserTask(DayScheduler dayScheduler, PropertyManager propertyManager, boolean enable, String user) {
		this.dayScheduler = dayScheduler;
		this.propertyManager = propertyManager;
		this.enable = enable;
		this.user = user;
	}

	@Override
	public void run() {
		try {
			String uri = propertyManager.getPcUrlPrefix() + "/api/User/" + user + "/" + enable;
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			connection.getInputStream().read();
			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				LOG.info("Succès de l'activation de César");
				success = true;
			} else {
				LOG.info("Echec1  de l'activation de César");
			}
		} catch (IOException e) {
			LOG.info("Echec2  de l'activation de César");
			LOG.error(e.getMessage(), e);
		}
		if (!success) {
			dayScheduler.retryIn10Minutes(this);
		}

	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}
}
package com.clement.magichome.task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.scheduler.DayScheduler;

/**
 * This will credit
 * 
 * @author Clement_Soullard
 *
 */
public class KickOffConnectedUserTask implements Runnable {

	static final Logger LOG = LoggerFactory.getLogger(KickOffConnectedUserTask.class);

	private PropertyManager propertyManager;

	public KickOffConnectedUserTask(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	@Override
	public void run() {
		try {
			String uri = propertyManager.getPcUrlPrefix() + "/api/User/disconnect";
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			connection.getInputStream().read();
			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				LOG.info("Succès de la deconnexion");
			} else {
				LOG.info("Impossible de déconnecter");
			}
		} catch (IOException e) {
			LOG.info("Impossible de déconnecter");
			LOG.error(e.getMessage(), e);
		}

	}

}
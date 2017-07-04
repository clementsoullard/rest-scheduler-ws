package com.clement.magichome.service;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 
 * This class is used in test mode to simulate the TV
 * 
 * @author cleme
 *
 */
@Repository
public class TelevisionSimulator {
	static final Logger LOG = LoggerFactory.getLogger(TelevisionSimulator.class);

	Boolean on = true;

	public InputStream getJSON() {
		if (on) {
			return getClass().getClassLoader().getResourceAsStream("/data/livebox-sample-actif.json");
		} else {
			return getClass().getClassLoader().getResourceAsStream("/data/livebox-sample-inactif.json");
		}
	}

	public Boolean getOn() {
		return on;
	}

	public void pressOnButton() {
		LOG.info("Appui sur le boutton on/off");
		this.on = !on;
	}

}

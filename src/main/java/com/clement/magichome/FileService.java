package com.clement.magichome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class FileService {

	static final Logger LOG = LoggerFactory.getLogger(TvCheckScheduler.class);

	@Resource
	PropertyManager propertyManager;

	/**
	 * 
	 * @param value
	 * @return true
	 */
	public boolean writeCountDown(int value) {
		File file = new File(propertyManager.getPathCountDown());
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		PrintStream ps;
		try {
			ps = new PrintStream(file);
			ps.print(value);
			ps.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Create a file if the Livebox is in Standby mode.
	 * 
	 * @param value
	 */
	public void writeStandby(boolean value) {
		File file = new File(propertyManager.getPathStandby());
		if (value) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			try {
				PrintStream ps;
				ps = new PrintStream(file);
				ps.print("1");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			file.delete();
		}
	}

	/**
	 * Return the TV status stored in the file /tmp/scheduler/ST
	 * 
	 * @return true is the TV is on at the scheduler level.
	 */
	boolean getTvStatusRelay() {
		File file = new File(propertyManager.getPathStatus());
		try {
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				byte buffer[] = new byte[5];
				IOUtils.read(fis, buffer);
				String valueRead = new String(buffer).trim();
				// If 1 is read from the livebox then it is in standby
				// (therefore not consuming seconds)
				LOG.debug("Value read at relay level " + valueRead);
				if (valueRead != null && valueRead.equals("1")) {
					LOG.debug("Open state detected at relay level");
					return true;
				}
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		LOG.debug("Close state detected at relay level");
		return false;
	}

	/**
	 * Return the number of seconds remaning /tmp/scheduler/REM
	 * 
	 * @return true is the TV is on at the scheduler level.
	 */
	Integer getSecondRemaining() {
		File file = new File(propertyManager.getPathRemaining());
		try {
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				byte buffer[] = new byte[5];
				IOUtils.read(fis, buffer);
				String valueRead = new String(buffer).trim();
				// If 1 is read from the livebox then it is in standby
				// (therefore not consuming seconds)
				LOG.debug("Value read at relay level " + valueRead);
				if (valueRead != null) {
					return Integer.parseInt(valueRead);
				}
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}

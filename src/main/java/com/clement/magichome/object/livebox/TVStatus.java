package com.clement.magichome.object.livebox;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.scheduler.DayScheduler;

/**
 * This is return by the web service to give the TV status.
 * 
 * @author cleme
 *
 */
public class TVStatus {


	/** The data from the TV */
	private TVData data;


	public TVData getData() {
		return data;
	}

	public void setData(TVData data) {
		this.data = data;
	}


}

package com.clement.magichome.object;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "log")
public class LogEntry {

	@Id
	private String id;

	private String metricName;

	private Integer channel;

	private Float minutes;

	private Date fromDate;

	private Date toDate;

	public LogEntry() {
	}

	public LogEntry(String metricName, Integer channel, Float minutes, Date fromDate, Date toDate) {
		this.metricName = metricName;
		this.channel = channel;
		this.minutes = minutes;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Float getMinutes() {
		return minutes;
	}

	public void setMinutes(Float minutes) {
		this.minutes = minutes;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

//	public DBObject getDocument() {
//		DBObject dbObject = new BasicDBObject();
//		dbObject.put("metricName", metricName);
//		dbObject.put("channel", channel);
//		dbObject.put("minutes", minutes);
//		dbObject.put("from", fromDate);
//		dbObject.put("to", toDate);
//		return dbObject;
//	}
}

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

	private String channelName;

	private String user;

	private Integer seconds;

	private Date fromDate;

	private Date toDate;

	public LogEntry() {
	}

	public LogEntry(String metricName, Integer channel, String channelName, String userName, Integer seconds,
			Date fromDate, Date toDate) {
		this.metricName = metricName;
		this.channel = channel;
		this.seconds = seconds;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.channelName = channelName;
		this.user = userName;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
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

	public Integer getSeconds() {
		return seconds;
	}

	public void setSeconds(Integer minutes) {
		this.seconds = minutes;
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

	public String getChannelName() {
		return channelName;
	}

}

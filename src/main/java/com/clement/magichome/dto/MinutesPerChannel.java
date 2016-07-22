package com.clement.magichome.dto;

import org.springframework.data.annotation.Id;

public class MinutesPerChannel {
	Float totalMinutes;
	
	@Id private String channel;


	public Float getTotalMinutes() {
		return totalMinutes;
	}

	public void setTotalMinutes(Float totalMinutes) {
		this.totalMinutes = totalMinutes;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}

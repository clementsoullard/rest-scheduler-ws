package com.clement.magichome.dto;

import org.springframework.data.annotation.Id;

public class SecondPerChannel {
	Float totalSeconds;

	@Id
	private String channelName;

	public Float getTotalSeconds() {
		return totalSeconds;
	}

	public Float getTotalhours() {
		return totalSeconds / 3600;
	}

	public void setTotalSeconds(Float totalMinutes) {
		this.totalSeconds = totalMinutes;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channel) {
		this.channelName = channel;
	}
}

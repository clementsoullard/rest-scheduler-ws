package com.clement.magichome.dto;

import org.springframework.data.annotation.Id;

public class ConsumptionPerHourOfDay {
	Float totalSeconds;

	@Id
	private Integer hour;

	public Float getTotalSeconds() {
		return totalSeconds;
	}

	public Float getTotalhours() {
		return totalSeconds / 3600;
	}

	public void setTotalSeconds(Float totalMinutes) {
		this.totalSeconds = totalMinutes;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}
}

package com.clement.magichome.dto;

import org.springframework.data.annotation.Id;

public class SecondPerUserOrdi {
	Float totalSeconds;

	@Id
	private String user;

	public Float getTotalSeconds() {
		return totalSeconds;
	}

	public Float getTotalhours() {
		return totalSeconds / 3600;
	}

	public void setTotalSeconds(Float totalMinutes) {
		this.totalSeconds = totalMinutes;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}

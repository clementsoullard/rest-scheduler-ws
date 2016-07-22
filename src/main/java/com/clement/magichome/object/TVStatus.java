package com.clement.magichome.object;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TVStatus {

	NumberFormat nf = new DecimalFormat("00");

	private Boolean relayStatus;

	private Integer remainingSecond;

	private TVData data;

	public Integer getRemainingSecond() {
		return remainingSecond;
	}

	public String getRemainingTime() {
		Integer second = remainingSecond % 60;
		Integer minutes = remainingSecond / 60;
		Integer hours = minutes / 60;
		minutes = minutes % 60;
		return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(second);
	}

	public void setRemainingSecond(Integer remaininingSecond) {
		this.remainingSecond = remaininingSecond;
	}

	public TVData getData() {
		return data;
	}

	public void setData(TVData data) {
		this.data = data;
	}

	public Boolean getRelayStatus() {
		return relayStatus;
	}

	public void setRelayStatus(Boolean relayStatus) {
		this.relayStatus = relayStatus;
	}

}

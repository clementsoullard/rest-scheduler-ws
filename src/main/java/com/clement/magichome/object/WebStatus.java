package com.clement.magichome.object;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clement.magichome.scheduler.DayScheduler;

public class WebStatus {
	public final static DateFormat df = new SimpleDateFormat("EEEEE dd, HH:mm", Locale.FRANCE);
	/** The date of the future credit. */
	private Date dateOfCredit;

	/** The amount of future credited minutes */
	private Integer amountOfCreditInMinutes;

	/** The number of bon points */
	private Integer bonPoints;

	/** Status of the TV read from the livebox */
	private Integer activeStandbyState;

	/** The channel that is being watched */
	private Integer playedMediaId;

	/** The channel that is being watched */
	private String channelName;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/** The number of bon points */
	private Integer bonPointsWeek;

	private final static NumberFormat nf = new DecimalFormat("00");

	/** Gives the status of the relay */
	private Boolean relayStatus;

	/** Number of seconds remaining */
	private Integer remainingSecond;

	/** Number of seconds remaining */
	private Integer minutesToday;

	public Integer getActiveStandbyState() {
		return activeStandbyState;
	}

	public void setActiveStandbyState(Integer activeStandbyState) {
		this.activeStandbyState = activeStandbyState;
	}

	public Integer getPlayedMediaId() {
		return playedMediaId;
	}

	public void setPlayedMediaId(Integer playedMediaId) {
		this.playedMediaId = playedMediaId;
	}

	public Integer getMinutesToday() {
		return minutesToday;
	}

	public void setMinutesToday(Integer minutesToday) {
		this.minutesToday = minutesToday;
	}

	public Integer getRemainingSecond() {
		return remainingSecond;
	}

	public String getRemainingTime() {
		if (remainingSecond == null) {
			return "Temps restant non disponible";
		} else if (remainingSecond == -2) {
			return "Télé activée sans limite de temps";
		} else if (remainingSecond <= 0) {
			return "Plus de temps restant";
		}
		Integer second = remainingSecond % 60;
		Integer minutes = remainingSecond / 60;
		Integer hours = minutes / 60;
		minutes = minutes % 60;
		return "Il reste " + nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(second);
	}

	public void setRemainingSecond(Integer remaininingSecond) {
		this.remainingSecond = remaininingSecond;
	}

	public Boolean getRelayStatus() {
		return relayStatus;
	}

	public void setRelayStatus(Boolean relayStatus) {
		this.relayStatus = relayStatus;
	}

	public Date getDateOfCredit() {
		return dateOfCredit;
	}

	static final Logger LOG = LoggerFactory.getLogger(DayScheduler.class);

	public void setDateOfCredit(Date dateOfCredit) {
		LOG.debug("Appel de setDateOfCredit" + dateOfCredit);
		this.dateOfCredit = dateOfCredit;
	}

	public Integer getAmountOfCreditInMinutes() {
		return amountOfCreditInMinutes;
	}

	public void setAmountOfCreditInMinutes(Integer amountOfCreditInMinutes) {
		this.amountOfCreditInMinutes = amountOfCreditInMinutes;
	}

	public String getDateStr() {
		if (dateOfCredit != null) {
			return df.format(dateOfCredit);
		} else {
			return null;
		}
	}

	public Integer getBonPoints() {
		return bonPoints;
	}

	public void setBonPoints(Integer bonPoints) {
		this.bonPoints = bonPoints;
	}

	public Integer getBonPointsWeek() {
		return bonPointsWeek;
	}

	public void setBonPointsWeek(Integer bonPointsWeek) {
		this.bonPointsWeek = bonPointsWeek;
	}

}

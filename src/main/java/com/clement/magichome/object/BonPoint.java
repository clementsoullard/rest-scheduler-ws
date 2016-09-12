package com.clement.magichome.object;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class BonPoint {
	@Id
	private String id;

	/** The number of point */
	private Integer point;

	private Integer pointConsumed;

	/** The date when the bon point is given */
	private Date date;

	/** The date when any minute cannot be given */
	private Date noTimeGivenBefore;

	/** The rationale why the bon point is given */
	private String rationale;

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public Integer getPointConsumed() {
		return pointConsumed;
	}

	public void setPointConsumed(Integer pointConsumed) {
		this.pointConsumed = pointConsumed;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRationale() {
		return rationale;
	}

	public void setRationale(String rationale) {
		this.rationale = rationale;
	}

	public BonPoint(Integer point, Integer pointConsumed, Date date, String rationale) {
		super();
		this.point = point;
		this.pointConsumed = pointConsumed;
		this.date = date;
		this.rationale = rationale;
	}

	public Date getNoTimeGivenBefore() {
		return noTimeGivenBefore;
	}

	public void setNoTimeGivenBefore(Date noTimeGivenBefore) {
		this.noTimeGivenBefore = noTimeGivenBefore;
	}

	public BonPoint() {
	}

}

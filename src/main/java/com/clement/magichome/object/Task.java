package com.clement.magichome.object;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Task {

	@Id
	private String id;

	public Task(String taskName, Boolean done, Date date, String owner, Boolean expireAtTheEndOfTheDay) {
		super();
		this.taskName = taskName;
		this.done = done;
		this.date = date;
		this.expireAtTheEndOfTheDay = expireAtTheEndOfTheDay;
		this.owner = owner;
	}

	public Task() {
	}

	private String taskName;

	private String owner;

	private Boolean done;

	/** True if a task is cancelled once the day is over */
	private Boolean expireAtTheEndOfTheDay;

	private Boolean open;

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	private Date date;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Date getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public String getIdr() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdr(String id) {
		this.id = id;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Boolean getExpireAtTheEndOfTheDay() {
		return expireAtTheEndOfTheDay;
	}

	public void setExpireAtTheEndOfTheDay(Boolean expireAtTheEndOfTheDay) {
		this.expireAtTheEndOfTheDay = expireAtTheEndOfTheDay;
	}

}

package com.clement.magichome.object;

import java.util.Date;

public class Task {

	public Task(String taskName, Boolean done, Date date) {
		super();
		this.taskName = taskName;
		this.done = done;
		this.date = date;
	}

	private String taskName;

	private Boolean done;

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

	public void setDate(Date date) {
		this.date = date;
	}

}

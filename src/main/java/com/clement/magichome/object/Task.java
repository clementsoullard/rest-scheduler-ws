package com.clement.magichome.object;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Task {

	@Id
	private String id;

	public Task(String taskName, Boolean done, Date date) {
		super();
		this.taskName = taskName;
		this.done = done;
		this.date = date;
	}

	public Task() {
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

}

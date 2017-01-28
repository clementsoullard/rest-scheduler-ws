package com.clement.magichome.object;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Achat {

	@Id
	private String id;

	public Achat() {
	}

	/** The name of the achat */
	private String name;

	/** The date when the achat was submitted; */
	private Date dateSubmit;
	/** The date when the active list has been closed */
	private Date dateListClosure;

	/** If it is bouht ore not */
	private Boolean done;
	/** When it has been done */
	private Date dateDone;

	/** If it is active (In the active list) ore not */
	private Boolean active;
	/** The identifier of the list */
	private String identifierList;

	public String getIdr() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setIdr(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateSubmit() {
		return dateSubmit;
	}

	public void setDateSubmit(Date dateSubmit) {
		this.dateSubmit = dateSubmit;
	}

	public Date getDateListClosure() {
		return dateListClosure;
	}

	public void setDateListClosure(Date dateListClosure) {
		this.dateListClosure = dateListClosure;
	}

	public Date getDateDone() {
		return dateDone;
	}

	public void setDateDone(Date dateDone) {
		this.dateDone = dateDone;
	}

	public String getIdentifierList() {
		return identifierList;
	}

	public void setIdentifierList(String identifierList) {
		this.identifierList = identifierList;
	}

	public Achat(String name) {
		super();
		this.name = name;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}

package com.clement.magichome.object;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Vacances {
	@Id
	private String id;

	private Date dateDebut;

	private Date dateFin;

	public Vacances() {
	}

	public Vacances(Date dateDebut, Date dateFin) {
		super();
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
	}

	public String getIdr() {
		return id;
	}

	public void setIdr(String id) {
		this.id = id;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	
	
	

}

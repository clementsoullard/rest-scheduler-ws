package com.clement.magichome.object;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The credit is to be granted and is computed every day.
 * 
 * @author Clement_Soullard
 *
 */
public class FutureCredit {

	public final static DateFormat df=new SimpleDateFormat("EEEEE dd, HH:mm");
	/** */
	private Date dateOfCredit;
	
	/** */
	private Integer amountOfCreditInMinutes;

	public Date getDateOfCredit() {
		return dateOfCredit;
	}

	public void setDateOfCredit(Date dateOfCredit) {
		this.dateOfCredit = dateOfCredit;
	}

	public Integer getAmountOfCreditInMinutes() {
		return amountOfCreditInMinutes;
	}

	public void setAmountOfCreditInMinutes(Integer amountOfCreditInMinutes) {
		this.amountOfCreditInMinutes = amountOfCreditInMinutes;
	}

	public String getDateStr() {
		return df.format(dateOfCredit);
	}

}

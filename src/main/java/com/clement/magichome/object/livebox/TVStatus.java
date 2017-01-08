package com.clement.magichome.object.livebox;

/**
 * This is return by the web service to give the TV status.
 * 
 * @author cleme
 *
 */
public class TVStatus {


	/** The data from the TV */
	private TVData data;


	public TVData getData() {
		return data;
	}

	public void setData(TVData data) {
		this.data = data;
	}


}

package com.clement.magichome.dto.graph;

/**
 * Represent a chart for TV consumption
 * 
 * @author cleme
 *
 */
public class JSChart {

	private String caption = "Consommation Télé par chaine (h)";

	private String subCaption = "Depuis le début";

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getSubCaption() {
		return subCaption;
	}

	public void setSubCaption(String subCaption) {
		this.subCaption = subCaption;
	}

}

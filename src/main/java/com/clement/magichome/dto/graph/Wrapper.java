package com.clement.magichome.dto.graph;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wrapper {
	JSChart JSChart;

	@JsonProperty("JSChart")
	public JSChart getJSChart() {
		return JSChart;
	}

	public void setJSChart(JSChart jSChart) {
		JSChart = jSChart;
	}

}

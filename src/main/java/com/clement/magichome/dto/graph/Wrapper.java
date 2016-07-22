package com.clement.magichome.dto.graph;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wrapper {
	JSChart JSChart = new JSChart();

	@JsonProperty("chart")
	public JSChart getJSChart() {
		return JSChart;
	}

	public void setJSChart(JSChart jSChart) {
		JSChart = jSChart;
	}

	List<Data> data = new ArrayList<Data>();

	public List<Data> getData() {
		return data;
	}

}

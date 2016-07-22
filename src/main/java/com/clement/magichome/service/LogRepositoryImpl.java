package com.clement.magichome.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.clement.magichome.dto.MinutesPerChannel;
import com.clement.magichome.dto.graph.Data;
import com.clement.magichome.dto.graph.Dataset;
import com.clement.magichome.dto.graph.JSChart;
import com.clement.magichome.object.Channel;

@Repository
public class LogRepositoryImpl {

	static final Logger LOG = LoggerFactory.getLogger(LogRepositoryImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;

	public JSChart getMinutesPerChannel() {
		try {
			JSChart jsChart = new JSChart();
			jsChart.getDatasets().add(new Dataset());
			Aggregation aggregation = newAggregation(match(Criteria.where("metricName").in("TV")),
					project("channel", "minutes"), group("channel").sum("minutes").as("totalMinutes"));
			LOG.debug("Construction de la requete effectuée");
			AggregationResults<MinutesPerChannel> minutesPerChannelAgg = mongoTemplate.aggregate(aggregation, "log",
					MinutesPerChannel.class);
			LOG.debug("Requete effectue");
			List<Data> datas = jsChart.getDatasets().get(0).getData();
			for (MinutesPerChannel minutesPerChannel : minutesPerChannelAgg) {
				Data data = new Data();
				data.setUnit(minutesPerChannel.getChannel());
				data.setValue(minutesPerChannel.getTotalMinutes());
				datas.add(data);
				LOG.debug(minutesPerChannel.getChannel().toString() + " " + minutesPerChannel.getTotalMinutes());
			}
			return jsChart;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}
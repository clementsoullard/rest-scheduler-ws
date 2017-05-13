package com.clement.magichome.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.clement.magichome.dto.SecondPerChannel;
import com.clement.magichome.dto.ConsumptionPerHourOfDay;
import com.clement.magichome.dto.SecondPerUserOrdi;
import com.clement.magichome.dto.graph.Data;
import com.clement.magichome.dto.graph.Wrapper;
import com.clement.magichome.object.MinutesToday;

@Repository
public class LogRepositoryImpl {

	static final Logger LOG = LoggerFactory.getLogger(LogRepositoryImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;

	NumberFormat nfm = new DecimalFormat("00");

	/**
	 * The number of hours per channel.
	 * 
	 * @return
	 */
	public Wrapper getHoursPerChannel() {
		try {
			Wrapper jsChart = new Wrapper();
			Aggregation aggregation = newAggregation(match(Criteria.where("metricName").in("TV")),
					project("channelName", "seconds"), group("channelName").sum("seconds").as("totalSeconds"));
			LOG.debug("Construction de la requete effectuée");
			AggregationResults<SecondPerChannel> minutesPerChannelAgg = mongoTemplate.aggregate(aggregation, "log",
					SecondPerChannel.class);
			LOG.debug("Requete effectue");
			List<Data> datas = jsChart.getData();
			for (SecondPerChannel minutesPerChannel : minutesPerChannelAgg) {
				if (minutesPerChannel.getChannelName() != null) {
					Data data = new Data();
					data.setLabel(minutesPerChannel.getChannelName());
					data.setValue(minutesPerChannel.getTotalhours());
					datas.add(data);
					LOG.debug(
							minutesPerChannel.getChannelName().toString() + " " + minutesPerChannel.getTotalSeconds());
				}
			}
			return jsChart;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * The consumption per hour.
	 * 
	 * @return
	 */
	public Wrapper getConsumptionPerHours() {
		try {
			Wrapper jsChart = new Wrapper();
			Aggregation aggregation = newAggregation(match(Criteria.where("metricName").is("TV")),
					project("seconds").and("fromDate").project("hour").as("hourInday"),
					group("hourInday").sum("seconds").as("totalSeconds"));
			LOG.debug("Construction de la requete effectuée");
			AggregationResults<ConsumptionPerHourOfDay> consumptionPerHourInDayAgg = mongoTemplate
					.aggregate(aggregation, "log", ConsumptionPerHourOfDay.class);
			LOG.debug("Requete effectue");
			List<Data> datas = jsChart.getData();
			Map<Integer, Float> mapHours = new HashMap<Integer, Float>();

			for (ConsumptionPerHourOfDay consumptionPerHour : consumptionPerHourInDayAgg) {
				mapHours.put(consumptionPerHour.getHour(), consumptionPerHour.getTotalhours());
			}

			for (Integer hour = 0; hour < 24; hour++) {
				Float qty = mapHours.get(hour);
				if (qty == null) {
					qty = 0F;
				}
				Data data = new Data();
				data.setLabel(hour.toString());
				data.setValue(qty);
				datas.add(data);
				LOG.debug(hour.toString() + " " + qty);
			}

			return jsChart;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Wrapper getHoursPerUserComputer() {
		try {
			Wrapper jsChart = new Wrapper();
			Aggregation aggregation = newAggregation(match(Criteria.where("metricName").in("PC")),
					project("user", "seconds"), group("user").sum("seconds").as("totalSeconds"));
			LOG.debug("Construction de la requete effectuée");
			AggregationResults<SecondPerUserOrdi> secondsPerUserOrdiAgg = mongoTemplate.aggregate(aggregation, "log",
					SecondPerUserOrdi.class);
			LOG.debug("Requete effectue");
			List<Data> datas = jsChart.getData();
			for (SecondPerUserOrdi secondsPerUserOrdi : secondsPerUserOrdiAgg) {
				if (secondsPerUserOrdi.getUser() != null) {
					String user = secondsPerUserOrdi.getUser();
					Data data = new Data();
					data.setLabel(user);
					data.setValue(secondsPerUserOrdi.getTotalhours());
					datas.add(data);
					LOG.debug(secondsPerUserOrdi.getUser().toString() + " " + secondsPerUserOrdi.getTotalSeconds());
				}
			}
			return jsChart;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * The number of minut watched today
	 * 
	 * @return
	 */
	public Integer getTimeTVToday() {
		try {
			Date todayMidnight = DateUtils.truncate(new Date(), Calendar.DATE);
			Aggregation aggregation = newAggregation(match(Criteria.where("metricName").in("TV")),
					match(Criteria.where("fromDate").gt(todayMidnight)), group().sum("seconds").as("seconds"));
			MinutesToday secondsToday = mongoTemplate.aggregate(aggregation, "log", MinutesToday.class)
					.getUniqueMappedResult();
			if (secondsToday == null || secondsToday.getSeconds() == null) {
				return 0;
			}
			return secondsToday.getSeconds();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public String secondsToString(int secondsTodayI) {
		int seconds = (int) secondsTodayI % 60;
		int minutes = (int) secondsTodayI / 60 % 60;
		int hour = (int) secondsTodayI / 3600;

		return "" + hour + ":" + nfm.format(minutes) + ":" + nfm.format(seconds);

	}

	/**
	 * The number of minut watched today
	 * 
	 * @return
	 */
	public Integer getTimePcToday() {
		try {
			Date todayMidnight = DateUtils.truncate(new Date(), Calendar.DATE);
			Aggregation aggregation = newAggregation(
					match(Criteria.where("metricName").in("PC").and("user").is("DESKTOP-BUREAU\\cesar")),
					match(Criteria.where("fromDate").gt(todayMidnight)), group().sum("seconds").as("seconds"));
			MinutesToday secondsToday = mongoTemplate.aggregate(aggregation, "log", MinutesToday.class)
					.getUniqueMappedResult();
			if (secondsToday == null || secondsToday.getSeconds() == null) {
				return 0;
			}

			return secondsToday.getSeconds();

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}
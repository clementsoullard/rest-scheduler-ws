package com.clement.magichome.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.validator.cfg.defs.MaxDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.BonPointSum;
import com.clement.magichome.object.DatePriveDeTele;
import com.clement.magichome.object.Vacances;
import com.mongodb.BasicDBObject;

/**
 * This class help to track the bon points.
 * 
 * @author Clement_Soullard
 *
 */
@Repository
public class VacancesService {

	public enum Profile {
		HOLIDAY, WEDNESDAY, SUNDAY, WORKDAY, SATURDAY
	}

	final static float DISTRIBUTION_FACTOR = 2F;

	static final Logger LOG = LoggerFactory.getLogger(VacancesService.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

	@Autowired
	private VacancesRepository vacancesRepository;

	/**
	 * Pass this test to avoid t oadd a vacance overlapping with anothoer
	 * 
	 * @param vacances
	 * @return true if some Vacances are already existing.
	 */
	public Boolean checkVacanceNotExisting(Vacances vacances) {
		try {
			String queryStr = "{ $or : [ { $and : [ { dateFin : " + new BasicDBObject("$gt", vacances.getDateDebut())
					+ "} , { dateFin : " + new BasicDBObject("$lte", vacances.getDateFin())
					+ "}]} , { $and : [ { dateDebut : " + new BasicDBObject("$gte", vacances.getDateDebut())
					+ "} , { dateDebut :" + new BasicDBObject("$lt", vacances.getDateFin()) + "}]}]}";
			BasicQuery query = new BasicQuery(queryStr);
			List<Vacances> vacancess = mongoTemplate.find(query, Vacances.class);

			if (vacancess.size() == 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 * @return true if the date passed in argument is in vacances
	 */
	public Profile getProfile(Date date) {
		try {
			String queryStr = " { $and : [ { dateFin : " + new BasicDBObject("$gte", date) + "} , { dateDebut :  "
					+ new BasicDBObject("$lte", date) + "}]} ";
			BasicQuery query = new BasicQuery(queryStr);
			List<Vacances> vacancess = mongoTemplate.find(query, Vacances.class);
			if (vacancess.size() == 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek == Calendar.WEDNESDAY) {
					return Profile.WEDNESDAY;
				} else if (dayOfWeek == Calendar.SATURDAY) {
					return Profile.WEDNESDAY;
				} else if (dayOfWeek == Calendar.SUNDAY) {
					return Profile.SUNDAY;
				} else {
					return Profile.WORKDAY;
				}
			}
			return Profile.HOLIDAY;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}
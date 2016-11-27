package com.clement.magichome.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

/**
 * This class help to track the bon points.
 * 
 * @author Clement_Soullard
 *
 */
@Repository
public class VacancesService {

	final static float DISTRIBUTION_FACTOR = 2F;

	static final Logger LOG = LoggerFactory.getLogger(VacancesService.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

	@Autowired
	private VacancesRepository vacancesRepository;

/**
 * 
 * @param vacances
 * @return true if some Vacances are already existing.
 */
	public Boolean checkVacanceNotExisting(Vacances vacances) {
		try {
			String queryStr = "{ $or : [ { $and : [ { dateFin : { $gte : { $date :'" + df.format(vacances.getDateDebut())
					+ "'}}} , { dateFin : { $lte : { $date :'" + df.format(vacances.getDateFin())
					+ "'}}}]} , { $and : [ { dateDebut : { $gte : { $date :'" + df.format(vacances.getDateDebut())
					+ "'}}} , { dateDebut : { $lte :	{ $date :'" + df.format(vacances.getDateFin()) + "'}}}]}]}";
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

}
package com.clement.magichome.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.BonPointSum;
import com.clement.magichome.object.DatePriveDeTele;

/**
 * This class help to track the bon points.
 * 
 * @author Clement_Soullard
 *
 */
@Repository
public class BonPointDaoImpl {

	final static float DISTRIBUTION_FACTOR = 2F;

	static final Logger LOG = LoggerFactory.getLogger(BonPointDaoImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private BonPointRepository bonPointRepository;

	public List<BonPoint> findPointsAvailable() {
		try {
			BasicQuery query = new BasicQuery("{$and: [ {pointConsumed: {$ne: 0}},{pointConsumed:{$gt: -1000}}]}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			return bonPoints;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * This compensate the good and the bad points.
	 */
	public void compensateBonEtMauvaisPoint() {
		List<BonPoint> bonPoints = findBonPointsAvailable();
		List<BonPoint> mauvaisPoints = findMauvaisPointsAvailable();

		Integer sumBonPoint = 0;

		for (BonPoint bonPoint : bonPoints) {
			sumBonPoint += bonPoint.getPointConsumed();
		}
		Integer sumMauvaisPoint = 0;
		for (BonPoint mauvaisPoint : mauvaisPoints) {
			sumMauvaisPoint += mauvaisPoint.getPointConsumed();
		}
		int difference = sumBonPoint + sumMauvaisPoint;
		Integer bonPointToRemove;
		if (difference > 0) {
			bonPointToRemove = sumBonPoint - difference;
		} else {
			bonPointToRemove = sumMauvaisPoint - difference;
		}
		removePunition(bonPointToRemove);
		removePunition(-bonPointToRemove);
	}

	/**
	 * 
	 * @return
	 * 
	 */
	public List<BonPoint> findBonPointsAvailable() {
		try {
			BasicQuery query = new BasicQuery("{pointConsumed: {$gt: 0}}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			return bonPoints;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public long countEntityConsumed() {
		BasicQuery query = new BasicQuery("{pointConsumed: 0}");
		return mongoTemplate.count(query, BonPoint.class);
	}

	/**
	 * 
	 * @return the list of mauvais point available
	 */
	public List<BonPoint> findMauvaisPointsAvailable() {
		try {
			BasicQuery query = new BasicQuery("{$and: [ {pointConsumed: {$lt: 0}},{pointConsumed:{$gt: -1000}}]}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			return bonPoints;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * This compute the sum of the good and bad point starting for the beginning
	 * of the data collection.
	 * 
	 * @return
	 */
	public BonPointSum sumBonPointV2() {
		BonPointSum bonPointSum = null;
		try {
			Aggregation agg = Aggregation.newAggregation(
					match(org.springframework.data.mongodb.core.query.Criteria.where("point").ne(-1000)),
					group().sum("pointConsumed").as("total"));
			/*
			 * Here is the raw query used in mongo db shell [{$match: {point:
			 * {$ne: -1000}}},{$group:{ _id: { },totalAmount: { $sum:
			 * '$pointConsumed' },count: { $sum: 1 }}}]
			 */
			AggregationResults<BonPointSum> aggregatedResult = mongoTemplate.aggregate(agg, "bonPoint",
					BonPointSum.class);
			bonPointSum = aggregatedResult.getUniqueMappedResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		if (bonPointSum == null) {
			bonPointSum = new BonPointSum();
			bonPointSum.setTotal(0L);
		}
		return bonPointSum;
	}

	/**
	 * This compute the sum of the good and bad point starting for the beginning
	 * of the data collection.
	 * 
	 * @return
	 */
	public DatePriveDeTele maxDate() {
		try {
			Aggregation agg = Aggregation.newAggregation(match(Criteria.where("point").is(-1000)),

					group().max("noTimeGivenBefore").as("maxDate"));
			/*
			 * Here is the raw query used in mongo db shell [{$match: {point:
			 * {$ne: -1000}}},{$group:{ _id: { },totalAmount: { $sum:
			 * '$pointConsumed' },count: { $sum: 1 }}}]
			 */
			AggregationResults<DatePriveDeTele> bonPointSum = mongoTemplate.aggregate(agg, "bonPoint",
					DatePriveDeTele.class);
			return bonPointSum.getUniqueMappedResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * This compute the sume of the good and bad point starting for the
	 * beginning of the data collection.
	 * 
	 * @return
	 */
	public BonPointSum sumBonPointBeginningOfWeek() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_WEEK, 2);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date dateFirstDayOfWeek = cal.getTime();
			LOG.debug("First Day of week is " + dateFirstDayOfWeek);
			Aggregation agg = Aggregation
					.newAggregation(
							match(new Criteria().andOperator(Criteria.where("point").ne(-1000),
									Criteria.where("date").gt(dateFirstDayOfWeek))),
							group().sum("pointConsumed").as("total"));
			/*
			 * Here is the raw query used in mongo db shell [{$match: {point:
			 * {$ne: -1000}}},{$group:{ _id: { },totalAmount: { $sum:
			 * '$pointConsumed' },count: { $sum: 1 }}}]
			 */
			AggregationResults<BonPointSum> bonPointSum = mongoTemplate.aggregate(agg, "bonPoint", BonPointSum.class);
			BonPointSum bps = bonPointSum.getUniqueMappedResult();
			if (bps != null) {
				return bps;
			} else {
				bps = new BonPointSum();
				bps.setTotal(0L);
				return bps;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * The day privé de Télé are marked with -1000
	 * 
	 * @return
	 */
	public boolean isPriveDeTele() {
		try {
			BasicQuery query = new BasicQuery("{pointConsumed: -1000}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			return bonPoints.size() > 0;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * The day privé de Télé are marked with -1000
	 * 
	 * @return
	 */
	public void remove1DayPriveDeTele(Date noMinutesGivenBefore) {
		try {
			BasicQuery query = new BasicQuery("{pointConsumed: -1000}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			if (bonPoints.size() > 0) {
				BonPoint bonPoint = bonPoints.get(0);
				bonPoint.setPointConsumed(0);
				bonPoint.setNoTimeGivenBefore(noMinutesGivenBefore);
				mongoTemplate.save(bonPoint);

			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * This is called by the scheduler to compute the number of point to
	 * distribute and propagate into the timer computation. Note: This does not
	 * take the point off the Bon Point, in rder to do this, you have to call
	 * the method
	 * 
	 * @see removePunition()
	 * @param min
	 * @param max
	 * @return
	 */
	public Integer pointToDistribute(Integer min, Integer max) {
		BonPointSum bonPointSum = sumBonPointV2();

		Integer sum = 0;
		if (bonPointSum != null) {
			Long result = sumBonPointV2().getTotal();
			LOG.debug("No bon point was distributed so far");
			sum = result.intValue();
		}
		Integer pointToDistribute = Math.round(sum.floatValue() / DISTRIBUTION_FACTOR + (1 * Math.signum(sum)));
		if (pointToDistribute < min) {
			pointToDistribute = min;
		} else if (pointToDistribute > max) {
			pointToDistribute = max;
		}
		return pointToDistribute;
	}

	/**
	 * Once the points have been selected and applied to a new count down timer.
	 * They must be ignored for the further count down.
	 * 
	 * @param pointToRemove
	 */
	public void removePunition(Integer pointToRemove) {
		LOG.info("Décompte des point de punition " + pointToRemove);
		Integer takenOutPoint = pointToRemove;
		if (pointToRemove > 0) {
			List<BonPoint> bonPointsPositifs = findBonPointsAvailable();
			for (BonPoint bonPoint : bonPointsPositifs) {
				if (takenOutPoint > 0) {
					Integer reste = bonPoint.getPointConsumed();
					if (takenOutPoint >= reste) {
						takenOutPoint -= reste;
						bonPoint.setPointConsumed(0);
					} else {
						reste -= takenOutPoint;
						bonPoint.setPointConsumed(reste);
						takenOutPoint = 0;
					}
					bonPointRepository.save(bonPoint);
				}
			}
		} else {

			List<BonPoint> bonPointsnegatives = findMauvaisPointsAvailable();
			for (BonPoint bonPoint : bonPointsnegatives) {
				if (takenOutPoint < 0) {
					Integer reste = bonPoint.getPointConsumed();
					if (takenOutPoint <= reste) {
						takenOutPoint -= reste;
						bonPoint.setPointConsumed(0);
					} else {
						reste -= takenOutPoint;
						bonPoint.setPointConsumed(reste);
						takenOutPoint = 0;
					}
					bonPointRepository.save(bonPoint);
				}
			}

		}
	}

}
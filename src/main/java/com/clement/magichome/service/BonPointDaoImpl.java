package com.clement.magichome.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.BonPointSum;

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

	public BonPointSum sumBonPointInOneLine() {
		try {

			Aggregation agg = Aggregation.newAggregation(
					match(org.springframework.data.mongodb.core.query.Criteria.where("point").ne(-1000)),
					group().sum("pointConsumed").as("total"));
			/*
			 * Here is the raw query used in mongo db shell [{$match: {point:
			 * {$ne: -1000}}},{$group:{ _id: { },totalAmount: { $sum:
			 * '$pointConsumed' },count: { $sum: 1 }}}]
			 */
			AggregationResults<BonPointSum> bonPointSum = mongoTemplate.aggregate(agg, "bonPoint", BonPointSum.class);
			return bonPointSum.getUniqueMappedResult();
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
	public void remove1DayPriveDeTele() {
		try {
			BasicQuery query = new BasicQuery("{pointConsumed: -1000}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			if (bonPoints.size() > 0) {
				BonPoint bonPoint = bonPoints.get(0);
				bonPoint.setPointConsumed(0);
				mongoTemplate.save(bonPoint);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @return the sum of bon points available (Not counting the privé de télé.)
	 */
	public Integer sumBonPoint() {
		Integer sum = 0;
		List<BonPoint> bonPoints = findPointsAvailable();
		for (BonPoint bonPoint : bonPoints) {
			sum += bonPoint.getPointConsumed();
		}
		return sum;
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
		Integer sum = sumBonPoint();
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
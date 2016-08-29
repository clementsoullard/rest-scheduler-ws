package com.clement.magichome.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import com.clement.magichome.object.BonPoint;

@Repository
public class BonPointDaoImpl {

	final static float DISTRIBUTION_FACTOR = 2F;

	static final Logger LOG = LoggerFactory.getLogger(BonPointDaoImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private BonPointRepository bonPointRepository;

	public List<BonPoint> findBonPointsAvailable() {
		try {
			BasicQuery query = new BasicQuery("{$and: [ {pointConsumed: {$ne: 0}},{pointConsumed:{$gt: -1000}}]}");
					List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			return bonPoints;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public List<BonPoint> findPostiveBonPointsAvailable() {
		try {
			BasicQuery query = new BasicQuery("{pointConsumed: {$gt: 0}}");
			List<BonPoint> bonPoints = mongoTemplate.find(query, BonPoint.class);
			return bonPoints;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public List<BonPoint> findNegativeBonPointsAvailable() {
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
				mongoTemplate.remove(bonPoints.get(0));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public Integer sumBonPoint() {
		Integer sum = 0;
		List<BonPoint> bonPoints = findBonPointsAvailable();
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
			List<BonPoint> bonPointsPositifs = findPostiveBonPointsAvailable();
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

			List<BonPoint> bonPointsnegatives = findNegativeBonPointsAvailable();
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
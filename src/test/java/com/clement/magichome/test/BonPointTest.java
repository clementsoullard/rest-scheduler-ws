package com.clement.magichome.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.BonPointSum;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.BonPointRepository;
import com.clement.magichome.task.CreditTimeForScreenTask;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BonPointTest {

	@Resource
	BonPointDaoImpl bonPointDaoImpl;
	@Resource
	BonPointRepository bonPointRepository;

	@Test
	public void testFindBonPoint() {
		insertBonPointBilanNegatif();
		List<BonPoint> bonPoints = bonPointDaoImpl.findPointsAvailable();
		for (BonPoint bonPoint : bonPoints) {
			bonPoint.setPointConsumed(bonPoint.getPointConsumed() + 1);
			bonPointRepository.save(bonPoint);
		}
	}

	private void insertBonPointBilanNegatif() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.MONTH, -1);
		Date oneMonthAgo = cal.getTime();
		bonPointRepository.deleteAll();
		bonPointRepository.save(new BonPoint(20, 20, today, "recompense"));
		bonPointRepository.save(new BonPoint(-20, -20, today, "desobeissance"));
		bonPointRepository.save(new BonPoint(-20, -20, oneMonthAgo, "desobeissance"));
		bonPointRepository.save(new BonPoint(-20, -10, today, "desobeissance"));
		bonPointRepository.save(new BonPoint(-1000, 0, today, "desobeissance"));
		bonPointRepository.save(new BonPoint(-1000, -1000, today, "desobeissance"));
	}

	/**
	 * 
	 */
	private void insertBonPointBilanPositif() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.MONTH, -1);
		Date oneMonthAgo = cal.getTime();
		bonPointRepository.deleteAll();
		bonPointRepository.save(new BonPoint(30, 0, oneMonthAgo, "recompense"));
		bonPointRepository.save(new BonPoint(20, 20, oneMonthAgo, "recompense"));
		bonPointRepository.save(new BonPoint(20, 20, oneMonthAgo, "recompense"));
		bonPointRepository.save(new BonPoint(-20, -20, oneMonthAgo, "desobeissance"));
		bonPointRepository.save(new BonPoint(-20, 0, oneMonthAgo, "desobeissance"));
		bonPointRepository.save(new BonPoint(-10, -5, oneMonthAgo, "desobeissance"));
		bonPointRepository.save(new BonPoint(-1000, 0, oneMonthAgo, "desobeissance"));
		bonPointRepository.save(new BonPoint(-1000, -1000, oneMonthAgo, "desobeissance"));
	}

	public void insertBonPointBilanNull() {
		bonPointRepository.deleteAll();
		bonPointRepository.save(new BonPoint(10, 10, new Date(), "recompense"));
		bonPointRepository.save(new BonPoint(10, 10, new Date(), "recompense"));
		bonPointRepository.save(new BonPoint(-10, -10, new Date(), "desobeissance"));
		bonPointRepository.save(new BonPoint(-10, -10, new Date(), "desobeissance"));
	}

	@Test
	public void testSumBonPoint() {
		insertBonPointBilanNegatif();
		Integer sumBonPoint = bonPointDaoImpl.sumBonPointV2().getTotal().intValue();
		org.junit.Assert.assertEquals(-30, sumBonPoint.intValue());
	}

	@Test
	public void testRetirePointPunition() {
		insertBonPointBilanNegatif();
		Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
		bonPointDaoImpl.removePunition(minutes);
	}

	@Test
	public void testRetirePointRecompense() {
		insertBonPointBilanPositif();
		Long sumBonpoint = bonPointDaoImpl.sumBonPointV2().getTotal();
		org.junit.Assert.assertEquals(sumBonpoint.intValue(), 15);
		Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
		bonPointDaoImpl.removePunition(minutes);
		org.junit.Assert.assertEquals(minutes.intValue(), 9);
		minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
		org.junit.Assert.assertEquals(minutes.intValue(), 0);
	}

	@Test
	public void testCompensatePointRecompenseBilanPositif() {
		insertBonPointBilanPositif();
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		org.junit.Assert.assertEquals(3L, bonPointDaoImpl.countEntityConsumed());
	}

	@Test
	public void testCompensatePointRecompenseBilanNegatif() {
		insertBonPointBilanNegatif();
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		org.junit.Assert.assertEquals(3L, bonPointDaoImpl.countEntityConsumed());
	}

	@Test
	public void testCompensatePointRecompenseBilanNul() {
		insertBonPointBilanNull();
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		org.junit.Assert.assertEquals(4L, bonPointDaoImpl.countEntityConsumed());
	}

	@Test
	public void testSumPointBilanNegatif() {
		insertBonPointBilanNegatif();
		BonPointSum bonPointSum = bonPointDaoImpl.sumBonPointV2();
		org.junit.Assert.assertEquals(-30L, bonPointSum.getTotal().longValue());
		BonPointSum bonPointSumBeginningOfWeek = bonPointDaoImpl.sumBonPointBeginningOfWeek();
		org.junit.Assert.assertEquals(-10L, bonPointSumBeginningOfWeek.getTotal().longValue());
	}

	@Test
	public void testSumPointBilanPositif() {
		insertBonPointBilanPositif();
		BonPointSum bonPointSum = bonPointDaoImpl.sumBonPointV2();
		org.junit.Assert.assertEquals(15L, bonPointSum.getTotal().longValue());
		BonPointSum bonPointSumBeginningOfWeek = bonPointDaoImpl.sumBonPointBeginningOfWeek();
		org.junit.Assert.assertEquals(0L, bonPointSumBeginningOfWeek.getTotal().longValue());
	}

	@Test
	public void testTaskDoneInDay() {
		Boolean sufficientActionToWatchTV = bonPointDaoImpl.sufficientActionToWatchTv();
		org.junit.Assert.assertEquals(false, sufficientActionToWatchTV);
	}

}

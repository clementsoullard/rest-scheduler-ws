package com.clement.magichome.test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.object.BonPoint;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.BonPointRepository;

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

	public void insertBonPointBilanNegatif() {
		bonPointRepository.deleteAll();
		bonPointRepository.save(new BonPoint(10, 10, new Date(), "recompense"));
		bonPointRepository.save(new BonPoint(-10, -10, new Date(), "desobeissance"));
		bonPointRepository.save(new BonPoint(-10, -10, new Date(), "desobeissance"));
	}

	public void insertBonPointBilanPositif() {
		bonPointRepository.deleteAll();
		bonPointRepository.save(new BonPoint(10, 10, new Date(), "recompense"));
		bonPointRepository.save(new BonPoint(10, 10, new Date(), "recompense"));
		bonPointRepository.save(new BonPoint(-10, -10, new Date(), "desobeissance"));
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
		Integer sumBonPoint = bonPointDaoImpl.sumBonPoint();
		org.junit.Assert.assertEquals(-10, sumBonPoint.intValue());
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
		Integer minutes = bonPointDaoImpl.pointToDistribute(-60, 30);
		bonPointDaoImpl.removePunition(minutes);
	}

	@Test
	public void testCompensatePointRecompenseBilanPositif() {
		insertBonPointBilanPositif();
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		org.junit.Assert.assertEquals(2L,bonPointDaoImpl.countEntityConsumed());
	}

	@Test
	public void testCompensatePointRecompenseBilanNegatif() {
		insertBonPointBilanNegatif();
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		org.junit.Assert.assertEquals(2L,bonPointDaoImpl.countEntityConsumed());
	}
	@Test
	public void testCompensatePointRecompenseBilanNul() {
		insertBonPointBilanNull();
		bonPointDaoImpl.compensateBonEtMauvaisPoint();
		org.junit.Assert.assertEquals(4L,bonPointDaoImpl.countEntityConsumed());
	}

}

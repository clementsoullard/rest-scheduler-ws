package com.clement.magichome.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.object.DatePriveDeTele;
import com.clement.magichome.scheduler.DayScheduler;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.CreditTask;
import com.clement.magichome.service.FileService;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NoUnitaryTest {

	static final Logger LOG = LoggerFactory.getLogger(NoUnitaryTest.class);

	@Autowired
	FileService fileService;
	@Autowired
	BonPointDaoImpl bonPointDaoImpl;

	@Autowired
	DayScheduler dayScheduler;

	@Test
	public void testLaunchTask() {
		int minutesAllowed = 60;
		CreditTask creditTask = new CreditTask(fileService, bonPointDaoImpl, dayScheduler);
		Integer minuteModifier = bonPointDaoImpl.pointToDistribute(-minutesAllowed, minutesAllowed / 2);
		int minutesGranted = minutesAllowed + minuteModifier;
		creditTask.setMinutes(minutesGranted);
		creditTask.setMinutesModifier(minuteModifier);
		LOG.debug("Credit Task repartition de " + minutesGranted);
		Long sumBonPoint = bonPointDaoImpl.sumBonPointV2().getTotal();
		LOG.debug("Sum Bon Point " + sumBonPoint);
		creditTask.run();
		sumBonPoint = bonPointDaoImpl.sumBonPointV2().getTotal();
		LOG.debug("Sum Bon Point après " + sumBonPoint);

	}

	@Test
	public void testMaxDate() {
		DatePriveDeTele dpt=bonPointDaoImpl.maxDate();
		org.junit.Assert.assertEquals(dpt.getMaxDate(),new Date());

	}

}

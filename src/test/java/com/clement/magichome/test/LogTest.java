package com.clement.magichome.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.TVSchedulerConstants;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.LogRepositoryImpl;
import com.clement.magichome.service.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LogTest {

	static final Logger LOG = LoggerFactory.getLogger(LogTest.class);

	@Resource
	LogRepositoryImpl logRepositoryImpl;

	@Resource
	TaskService taskService;

	@Test
	public void testTaskForToday() {
		logRepositoryImpl.getHoursPerUserComputer();
	}

	@Test
	public void testTimePCToday() {
		LOG.debug("Temps joué par césar "+logRepositoryImpl.getTimePcToday());
	}

}

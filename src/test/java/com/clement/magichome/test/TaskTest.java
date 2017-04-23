package com.clement.magichome.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.TVSchedulerConstants;
import com.clement.magichome.service.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TaskTest {

	@Resource
	TaskService taskService;

	@Test
	public void testTaskForToday() {
		taskService.getTaskForToday(TVSchedulerConstants.CESAR);
	}

}

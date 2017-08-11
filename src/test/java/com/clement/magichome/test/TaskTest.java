package com.clement.magichome.test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.TVSchedulerConstants;
import com.clement.magichome.object.Task;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.TaskRepository;
import com.clement.magichome.service.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TaskTest {

	Logger log = LoggerFactory.getLogger(TaskTest.class);

	@Resource
	BonPointDaoImpl bonPointDaoImpl;

	@Resource
	TaskService taskService;

	@Resource
	TaskRepository taskRepository;

	@Test
	public void testTaskForToday() {
		List<Task> tasks = taskService.getTaskForToday(TVSchedulerConstants.CESAR);
		for (Task task : tasks) {
			log.debug("Task " + task.getTaskName() + " date:" + task.getDate());
		}
	}


	@Test
	public void testTaskDoneInDay() {
		Boolean sufficientActionToWatchTV = bonPointDaoImpl.sufficientActionToWatchTv();
		org.junit.Assert.assertEquals(false, sufficientActionToWatchTV);
	}

}

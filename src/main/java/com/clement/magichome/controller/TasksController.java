package com.clement.magichome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.object.Task;
import com.clement.magichome.service.TaskService;

@RestController
public class TasksController {
	@Autowired
	TaskService tasksService;

	@RequestMapping("/today-tasks")
	public List<Task> getTasksToday() throws Exception {
		return tasksService.getTaskForToday();
	}

}

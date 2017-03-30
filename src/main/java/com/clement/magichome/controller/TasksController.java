package com.clement.magichome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.object.Task;
import com.clement.magichome.service.TaskService;

@RestController
public class TasksController {
	@Autowired
	TaskService tasksService;

	@RequestMapping(value="/today-tasks",method=RequestMethod.GET)
	
	public List<Task> getTasksToday() throws Exception {
		return tasksService.getTaskForToday();
	}

	@RequestMapping(value="/ws-create-todo",method=RequestMethod.POST)
	public void saveTasksToday(@RequestBody Task task) throws Exception {
		tasksService.saveTaskForToday(task);
	}

}

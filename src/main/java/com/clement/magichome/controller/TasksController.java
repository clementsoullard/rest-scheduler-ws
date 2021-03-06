package com.clement.magichome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

	@RequestMapping(value = "/today-tasks", method = RequestMethod.GET)
	public List<Task> getTasksToday() throws Exception {
//		return tasksService.getTaskForToday();
		return tasksService.listTaskForToday();
	}

	@RequestMapping(value = "/ws-create-todo", method = RequestMethod.POST)
	public void saveTask(@RequestBody Task task) throws Exception {
		tasksService.saveTaskForToday(task);
	}

	@RequestMapping(value = "/ws-update-todo/{id}", method = RequestMethod.POST)
	public void updateTasks(@RequestBody Task task, @PathVariable(value = "id") String id) throws Exception {
		tasksService.updateTask(task, id);
	}

}

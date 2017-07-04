package com.clement.magichome.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.TVSchedulerConstants;
import com.clement.magichome.object.Task;

@Repository
public class TaskService {

	@Resource
	private PropertyManager propertyManager;

	@Resource
	private TaskRepository taskRepository;

	/**
	 * List the taks predefined for the day.
	 * 
	 * @return
	 */
	public List<Task> getCesarTasksExpiringToday() {
		Calendar calendar = Calendar.getInstance();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		List<Task> tasks = taskRepository.getTaskByDateAndOwnerAndExpireAtTheEndOfTheDay(date,
				TVSchedulerConstants.CESAR, true);
		if (tasks.size() == 0) {
			Task task;
			switch (dayOfWeek) {
			case Calendar.SATURDAY:
				task = new Task("Mettre la table", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Faire le piano", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Sortir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("jeu de Société", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Lecture", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.SUNDAY:
				task = new Task("Faire du sport (piscine/footing)", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Solfège", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Piano", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Sortir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Jouer tout seul", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Lecture", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Aider à faire le ménage", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Jeu de société", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("S'habiller", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.MONDAY:
				task = new Task("Solfège", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.TUESDAY:
				task = new Task("Piano", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.WEDNESDAY:
				task = new Task("Solfège", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Aider à faire le ménage", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.THURSDAY:
				task = new Task("Piano", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.FRIDAY:
				task = new Task("Solfège", false, date, TVSchedulerConstants.CESAR, true);
				taskRepository.save(task);
				tasks.add(task);
				break;
			}

		}
		return tasks;
	}

	/**
	 * 
	 * @param task
	 */
	public void saveTaskForToday(Task task) {
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		task.setDate(date);
		task.setDone(false);
		taskRepository.save(task);
	}

	public List<Task> getHomeTasksExpiringToday() {
		Calendar calendar = Calendar.getInstance();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		List<Task> tasksEOD = taskRepository.getTaskByDateAndOwnerAndExpireAtTheEndOfTheDay(date,
				TVSchedulerConstants.HOME, true);
		if (tasksEOD.size() == 0) {
			Task task;
			switch (dayOfWeek) {
			case Calendar.SATURDAY:
				task = new Task("Descendre les poubelles", false, date, TVSchedulerConstants.HOME, true);
				taskRepository.save(task);
				tasksEOD.add(task);
				break;
			case Calendar.SUNDAY:
				task = new Task("Descendre les poubelles", false, date, TVSchedulerConstants.HOME, true);
				taskRepository.save(task);
				tasksEOD.add(task);
				break;
			case Calendar.MONDAY:
				break;
			case Calendar.TUESDAY:
				break;
			case Calendar.WEDNESDAY:
				break;
			case Calendar.THURSDAY:
				break;
			case Calendar.FRIDAY:
				break;
			}
		}
		return tasksEOD;
	}

	/**
	 * List the taks predefined for the day.
	 * 
	 * @return
	 */
	public List<Task> getTaskForToday(String owner) {

		List<Task> tasksEOD = null;
		if (owner.equals(TVSchedulerConstants.CESAR)) {
			tasksEOD = getCesarTasksExpiringToday();
		} else if (owner.equals(TVSchedulerConstants.HOME)) {
			tasksEOD = getHomeTasksExpiringToday();
		}
		List<Task> tasksPermanentTasks = taskRepository.getTaskByOwnerAndExpireAtTheEndOfTheDayAndDone(owner, false,
				false);
		List<Task> tasks = tasksEOD;
		tasks.addAll(tasksPermanentTasks);
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		tasksPermanentTasks = taskRepository.getTaskByDateAndOwnerAndExpireAtTheEndOfTheDayAndDone(date, owner, false,
				true);
		tasks.addAll(tasksPermanentTasks);
		return tasks;
	}

	/**
	 * List the taks predefined for the day.
	 * 
	 * @return
	 */
	public List<Task> getTaskForToday() {
		List<Task> tasksEOD = null;
		tasksEOD = getCesarTasksExpiringToday();
		tasksEOD.addAll(getHomeTasksExpiringToday());
		List<Task> tasksPermanentTasks = taskRepository.getTaskByExpireAtTheEndOfTheDayAndDone(false,
				false);
		List<Task> tasks = tasksEOD;
		tasks.addAll(tasksPermanentTasks);
		return tasks;
	}
}

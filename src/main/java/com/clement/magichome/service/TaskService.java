package com.clement.magichome.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.TVSchedulerConstants;
import com.clement.magichome.object.Achat;
import com.clement.magichome.object.Task;

@Repository
public class TaskService {

	@Autowired
	private MongoTemplate mongoTemplate;

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
	public List<Task> listTaskForToday() throws Exception {
		Query query = new Query();
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.HOUR, -2);
		Date toHoursAgo = calendar.getTime();
		query.addCriteria(new Criteria().andOperator(
				new Criteria().orOperator(Criteria.where("expirationDate").gt(today),
						Criteria.where("expirationDate").exists(false)),
				new Criteria().orOperator(Criteria.where("done").is(false),
						Criteria.where("completionDate").gt(toHoursAgo))));

		List<Task> tasks = mongoTemplate.find(query, Task.class);

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

	/**
	 * 
	 * @param task
	 */
	public Task updateTask(Task task, String id) {
		/**
		 * In case the id starts with a TMP, this mean that the application.
		 */
		if (id.startsWith("TMP")) {
			task.setId(null);
			return taskRepository.save(task);

		} else {
			Date date = DateUtils.truncate(new Date(), Calendar.DATE);
			Task taskToUpdate = taskRepository.findOne(id);
			task.setDate(date);
			taskToUpdate.setDone(task.getDone());
			if (task.getDone() && task.getCompletionDate() == null) {
				taskToUpdate.setCompletionDate(new Date());
			}
			return taskRepository.save(taskToUpdate);
		}
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
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -2);
		List<Task> tasksPermanentTasks = taskRepository
				.getTaskByOwnerAndExpireAtTheEndOfTheDayAndCompletionDateAfter(owner, false, calendar.getTime());
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
		Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);

		getCesarTasksExpiringToday();
		getHomeTasksExpiringToday();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -2);
		List<Task> tasksPermanentTasks = taskRepository
				.getTaskByExpireAtTheEndOfTheDayAndCompletionDateAfterOrCompletionDateIsNullOrderByDoneAsc(false,
						calendar.getTime());
		List<Task> tasksTemp = taskRepository
				.getTaskByExpireAtTheEndOfTheDayAndDateAndCompletionDateAfterOrderByDoneAsc(true, currentDate,
						calendar.getTime());
		tasksPermanentTasks.addAll(tasksTemp);
		return tasksPermanentTasks;
	}
}

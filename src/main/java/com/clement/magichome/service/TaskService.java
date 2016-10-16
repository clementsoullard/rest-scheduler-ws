package com.clement.magichome.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.Task;

@Repository
public class TaskService {

	@Resource
	private PropertyManager propertyManager;

	@Resource
	private TaskRepository taskRepository;

	public List<Task> getTaskForToday() {
		Calendar calendar = Calendar.getInstance();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		List<Task> tasks = taskRepository.getTaskByDate(date);
		if (tasks.size() == 0) {
			Task task;
			switch (dayOfWeek) {
			case Calendar.SATURDAY:
				task = new Task("Mettre la table", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Faire le piano", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Sortir", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("jeu de Société", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date);
				taskRepository.save(task);
				tasks.add(task);
				break;
			case Calendar.SUNDAY:
				task = new Task("Faire du sport (psicine/footing)", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Solfège", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Piano", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Sortir", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Jouer tout seul", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Devoir", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Aider à faire le ménage", false, date);
				taskRepository.save(task);
				tasks.add(task);
				task = new Task("Jeu de société", false, date);
				taskRepository.save(task);
				tasks.add(task);
				break;

			}

		}
		return tasks;

	}
}

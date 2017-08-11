
package com.clement.magichome.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.clement.magichome.object.Task;

@RepositoryRestResource(collectionResourceRel = "task", path = "task")
public interface TaskRepository extends MongoRepository<Task, String> {

	List<Task> getTaskByDateAndOwnerAndExpireAtTheEndOfTheDay(Date date, String owner, Boolean expiresAtTheEndOfTheDay);

	List<Task> getTaskByDateAndOwnerAndExpireAtTheEndOfTheDayAndDone(Date date, String owner,
			Boolean expiresAtTheEndOfTheDay, Boolean done);

	List<Task> getTaskByOwnerAndExpireAtTheEndOfTheDayAndDateCompletionAfter(String owner,
			Boolean expiresAtTheEndOfTheDay, Date dateCompletion);

	List<Task> getTaskByDateCompletionAfterOrDateCompletionIsNullOrderByDoneAsc(Date dateCompletion);

}

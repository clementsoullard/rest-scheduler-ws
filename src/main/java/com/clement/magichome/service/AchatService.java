package com.clement.magichome.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.Achat;

@Repository
public class AchatService {

	@Resource
	private PropertyManager propertyManager;

	@Resource
	private AchatRepository achatRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Achat> getActiveAchat() {
		return achatRepository.findByActive(true);
	}

	/**
	 * 
	 * @param achat
	 */
	public void update(Achat achat) {
		if (achat.getDone() == null || achat.getDone()) {
			Date date = DateUtils.truncate(new Date(), Calendar.DATE);
			achat.setDateDone(date);
		}
		achatRepository.save(achat);
	}

	/**
	 * 
	 * @param achat
	 */
	public void finish() {
		Query query = new BasicQuery("{active: true, done: true}");

		Update update = new Update();
		update.set("active", false);
		update.set("dateListClosure", new Date());
		update.set("identifierList", ObjectId.get());

		mongoTemplate.updateMulti(query, update, Achat.class);

	}

	/**
	 * 
	 * @param task
	 */
	public void createNew(Achat achat) {
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		achat.setDateSubmit(date);
		achat.setActive(true);
		achatRepository.save(achat);
	}
}

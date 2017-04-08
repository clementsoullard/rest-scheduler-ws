package com.clement.magichome.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.Achat;

@Repository
public class AchatService {

	static final Logger LOG = LoggerFactory.getLogger(AchatService.class);

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
	 * This mean that the purchase is done, we can close all the items.
	 * 
	 * @param achat
	 */
	public void endAchat() {
		LOG.info("Cloture de la liste de course");
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
	public void createNew(Achat achat) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(achat.getName()).and("active").is(true));
		Long count = mongoTemplate.count(query, Achat.class);
		/**
		 * We only insert if the achat does not exist
		 */
		if (count == 0 && achat.getName() != null && achat.getName().trim().length() != 0) {
			Date date = DateUtils.truncate(new Date(), Calendar.DATE);
			achat.setDateSubmit(date);
			achat.setActive(true);
			achatRepository.save(achat);
		} else {
			throw new Exception("Achat existe déjà ou vide");
		}
	}

	/**
	 * 
	 * @param task
	 */
	public List<String> distinct() {
		List<String> distinctName = mongoTemplate.getCollection("achat").distinct("name");
		return distinctName;
	}
}

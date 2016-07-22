package com.clement.magichome.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.clement.magichome.object.LogEntry;

@Repository
public class LogService {

	static final Logger LOG = LoggerFactory.getLogger(LogRepositoryImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;

	public void insertlogEntry(Date from, Date to, Integer channel, Float minutes) {
		try {
			LogEntry logEntry = new LogEntry();
			logEntry.setMinutes(minutes);
			logEntry.setMetricName("TV");
			logEntry.setChannel(channel);
			logEntry.setFromDate(from);
			logEntry.setToDate(to);
			LOG.debug("Insert");
//			mongoTemplate.getCollection("log").insert(logEntry.getDocument());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

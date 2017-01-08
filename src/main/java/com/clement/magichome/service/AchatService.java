package com.clement.magichome.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.Achat;

@Repository
public class AchatService {

	@Resource
	private PropertyManager propertyManager;

	@Resource
	private AchatRepository achatRepository;

	public List<Achat> getActiveAchat() {
		return achatRepository.findByActive(true);
	}

	/**
	 * 
	 * @param task
	 */
	public void update(Achat achat) {
		if (achat.getDone()==null||achat.getDone()) {
			Date date = DateUtils.truncate(new Date(), Calendar.DATE);
			achat.setDateDone(date);
		}
		achatRepository.save(achat);
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

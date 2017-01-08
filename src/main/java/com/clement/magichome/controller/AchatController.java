package com.clement.magichome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.object.Achat;
import com.clement.magichome.service.AchatService;

@RestController
public class AchatController {
	@Autowired
	AchatService achatService;

	@RequestMapping(value = "/ws-active-achat", method = RequestMethod.GET)

	public List<Achat> getTasksToday() throws Exception {
		return achatService.getActiveAchat();
	}

	@RequestMapping(value = "/ws-create-achat", method = RequestMethod.POST)
	public void createAchat(@RequestBody Achat achat) throws Exception {
		achatService.createNew(achat);
	}

	@RequestMapping(value = "/ws-update-achat", method = RequestMethod.POST)
	public void saveTasksToday(@RequestBody Achat achat) throws Exception {
		achatService.update(achat);
	}

}

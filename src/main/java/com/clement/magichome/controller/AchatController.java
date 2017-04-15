package com.clement.magichome.controller;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.object.Achat;
import com.clement.magichome.object.AchatDistinct;
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

	@GetMapping(value = "/ws-suggest-achat")
	public List<AchatDistinct> getSuggestAchat() throws Exception {
		List<String> achatDistinctString = achatService.distinctAchats();
		List<AchatDistinct> achatDistincts = new ArrayList<AchatDistinct>();
		int i = 0;
		for (String acString : achatDistinctString) {
			AchatDistinct achatDistinct = new AchatDistinct();
			achatDistinct.setId(i);
			achatDistinct.setName(acString);
			achatDistincts.add(achatDistinct);
			i++;
		}
		return achatDistincts;
	}

	@PostMapping(value = "/ws-update-achat")
	public void updateAchat(@RequestBody Achat achat) throws Exception {
		achatService.update(achat);
	}

	@PostMapping(value = "/ws-update-achat/{id}")
	public void updateAchat(@RequestBody Achat achat, @PathParam(value = "id") String id) throws Exception {
		achatService.update(achat);
	}

	@RequestMapping(value = "/ws-finish-achat")
	public void finishAchat() throws Exception {
		achatService.endAchat();
	}

}

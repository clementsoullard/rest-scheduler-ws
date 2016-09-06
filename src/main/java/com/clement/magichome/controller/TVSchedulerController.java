package com.clement.magichome.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.dto.CreditResult;
import com.clement.magichome.dto.Punition;
import com.clement.magichome.dto.PunitionResult;
import com.clement.magichome.dto.graph.Wrapper;
import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.LogEntry;
import com.clement.magichome.object.TVStatus;
import com.clement.magichome.scheduler.DayScheduler;
import com.clement.magichome.scheduler.TvCheckScheduler;
import com.clement.magichome.service.BonPointRepository;
import com.clement.magichome.service.FileService;
import com.clement.magichome.service.LogRepository;
import com.clement.magichome.service.LogRepositoryImpl;

@RestController
public class TVSchedulerController {

	@Resource
	TvCheckScheduler tvCheckScheduler;

	@Resource
	DayScheduler dayScheduler;

	@Resource
	FileService fileService;

	@Resource
	private LogRepository logRepository;

	@Resource
	private BonPointRepository bonPointRepository;

	@Autowired
	LogRepositoryImpl logRepositoryImpl;

	@RequestMapping("/credit")
	public CreditResult credit(@RequestParam(value = "value", defaultValue = "90") Integer value) throws Exception {
		TVStatus tvStatus = tvCheckScheduler.getStandByState();
		CreditResult creditResult = new CreditResult("Ok");
		if (fileService.writeCountDown(value)) {
			// The value is written in the file, we assume that it is properly
			// propagated the the C schedulers
			tvStatus.setRemainingSecond(value);
		} else {
			creditResult.setContent("KO");
		}
		creditResult.setStatus(tvStatus);
		return creditResult;
	}

	@RequestMapping("/tvstatus")
	public TVStatus tvStatus() throws Exception {
		TVStatus tvStatus = tvCheckScheduler.getStandByState();
		return tvStatus;
	}

	@RequestMapping("/chart-channel")
	public Wrapper chartChannelChart() throws Exception {
		Wrapper jsChart = logRepositoryImpl.getMinutesPerChannel();
		return jsChart;
	}

	@RequestMapping("/punition")
	public PunitionResult punition(@RequestBody Punition punition) throws Exception {
		bonPointRepository
				.save(new BonPoint(punition.getValue(), punition.getValue(), new Date(), punition.getRationale()));
		PunitionResult punitionResult = new PunitionResult();
		if(punition.getValue()<0){
		punitionResult.setMessage("La punition a été appliquée");
		}else{
			punitionResult.setMessage("Les bons point ont bien été attribué");
		}
		dayScheduler.scheduleForTheDay();
		return punitionResult;
	}
}
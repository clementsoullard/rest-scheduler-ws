package com.clement.magichome.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.FileService;
import com.clement.magichome.TvCheckScheduler;
import com.clement.magichome.dto.CreditResult;
import com.clement.magichome.dto.graph.Wrapper;
import com.clement.magichome.object.LogEntry;
import com.clement.magichome.object.TVStatus;
import com.clement.magichome.service.LogRepository;
import com.clement.magichome.service.LogRepositoryImpl;
import com.clement.magichome.service.LogService;

@RestController
public class SchedulerController {

	@Resource
	TvCheckScheduler tvCheckScheduler;

	@Resource
	FileService fileService;

	@Resource
	private LogRepository logRepository;

	@Autowired
	LogRepositoryImpl logRepositoryImpl;

	@Autowired
	LogService logService;

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
	public Wrapper test() throws Exception {
		Wrapper jsChart = logRepositoryImpl.getMinutesPerChannel();
		return jsChart;
	}

	@RequestMapping("/test2")
	public void test2() throws Exception {
		// logService.insertlogEntry(new Date(), new Date(), 1, 2.2F);
		logRepository.save(new LogEntry("TV", 12, 2.2F, new Date(), new Date()));
	}
}
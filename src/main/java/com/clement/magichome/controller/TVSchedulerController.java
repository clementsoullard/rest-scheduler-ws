package com.clement.magichome.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.dto.CreditResult;
import com.clement.magichome.dto.Punition;
import com.clement.magichome.dto.PunitionResult;
import com.clement.magichome.dto.graph.Wrapper;
import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.Vacances;
import com.clement.magichome.object.WebStatus;
import com.clement.magichome.scheduler.DayScheduler;
import com.clement.magichome.scheduler.TvCheckScheduler;
import com.clement.magichome.service.BonPointRepository;
import com.clement.magichome.service.FileService;
import com.clement.magichome.service.LogRepository;
import com.clement.magichome.service.LogRepositoryImpl;
import com.clement.magichome.service.StatusService;
import com.clement.magichome.service.VacancesRepository;
import com.clement.magichome.service.VacancesService;

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
	private VacancesRepository vacancesRepository;
	@Resource
	private VacancesService vacancesService;

	@Resource
	private BonPointRepository bonPointRepository;

	@Autowired
	LogRepositoryImpl logRepositoryImpl;

	@Autowired
	StatusService statusService;
	static final Logger LOG = LoggerFactory.getLogger(TVSchedulerController.class);

	@RequestMapping("/credit")
	public CreditResult credit(@RequestParam(value = "value", defaultValue = "90") Integer value) throws Exception {
		WebStatus tvStatus = statusService.getStatus();
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
	public WebStatus tvStatus() throws Exception {
		LOG.debug("Controller checking status");
		dayScheduler.computeNextOccurenceOfCredit();
		statusService.updateTvStatusLivelyParameters();
		statusService.updateLessLivelyParameters();
		WebStatus tvStatus = statusService.getStatus();
		return tvStatus;
	}

	@RequestMapping("/chart-channel")
	public Wrapper chartChannelChart() throws Exception {
		Wrapper jsChart = logRepositoryImpl.getHoursPerChannel();
		return jsChart;
	}

	@RequestMapping(value = "/punition", method = RequestMethod.POST)
	public PunitionResult punition(@RequestBody Punition punition) throws Exception {
		bonPointRepository
				.save(new BonPoint(punition.getValue(), punition.getValue(), new Date(), punition.getRationale()));
		PunitionResult punitionResult = new PunitionResult();
		if (punition.getValue() < 0) {
			punitionResult.setMessage("La punition a été appliquée");
		} else {
			punitionResult.setMessage("Les bons points ont bien été attribués");
		}
		dayScheduler.computeNextOccurenceOfCredit();
		return punitionResult;
	}

	@RequestMapping(value = "/create-vacances", method = RequestMethod.POST)

	public void creatVacances(@RequestBody Vacances vacances) throws Exception {
		if (!vacancesService.checkVacanceNotExisting(vacances)) {
			vacancesRepository.save(vacances);
		} else {
			throw new Exception("Ces vacances existent déjà");
		}
	}
}
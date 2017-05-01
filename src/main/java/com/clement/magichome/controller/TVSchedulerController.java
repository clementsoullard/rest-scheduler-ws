package com.clement.magichome.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.dto.CreditResult;
import com.clement.magichome.dto.Punition;
import com.clement.magichome.dto.PunitionResult;
import com.clement.magichome.dto.graph.JSChart;
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
import com.clement.magichome.task.AllowDenyUserTask;

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

	@Resource
	private PropertyManager propertyManager;

	@Autowired
	StatusService statusService;
	static final Logger LOG = LoggerFactory.getLogger(TVSchedulerController.class);

	@RequestMapping("/credit")
	public CreditResult credit(@RequestParam(value = "value", defaultValue = "90") Integer value) throws Exception {
		WebStatus tvStatus = statusService.getStatus();
		CreditResult creditResult = new CreditResult("Ok");
		if (fileService.writeCredit(value)) {
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

	/**
	 * Display the most watched channels
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chart-channel")
	public Wrapper channelChartAllTime() throws Exception {
		Wrapper wrapper = logRepositoryImpl.getHoursPerChannel();
		JSChart jsChart = new JSChart();
		jsChart.setCaption("Consommation Télé par chaine (h)");
		jsChart.setSubCaption("Depuis le début");
		wrapper.setJSChart(jsChart);
		return wrapper;
	}

	/**
	 * Display the most watched channels
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/hour-consumption")
	public Wrapper hourConsumption() throws Exception {
		Wrapper wrapper = logRepositoryImpl.getConsumptionPerHours();
		JSChart jsChart = new JSChart();
		jsChart.setCaption("Consommation Télé par heure du jour");
		jsChart.setSubCaption("Depuis le début");
		wrapper.setJSChart(jsChart);
		return wrapper;
	}

	/**
	 * Display the usage per channel
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chart-computer")
	public Wrapper chartChannelChart() throws Exception {
		Wrapper wrapper = logRepositoryImpl.getHoursPerUserComputer();
		JSChart jsChart = new JSChart();
		jsChart.setCaption("Consommation d'ordinateur par user (h)");
		jsChart.setSubCaption("Depuis le début");
		wrapper.setJSChart(jsChart);
		return wrapper;
	}

	/**
	 * Display the usage per channel
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/pc-activate/{enable}")
	public void activationPc(@PathVariable("enable") Boolean enable) throws Exception {
		LOG.debug("Actiovation " + enable);
		AllowDenyUserTask allowDenyUserTask = new AllowDenyUserTask(null, propertyManager, enable, "cesar");
		allowDenyUserTask.run();
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

	/**
	 * Service to create holidays
	 * 
	 * @param vacances
	 * @throws Exception
	 */
	@RequestMapping(value = "/create-vacances", method = RequestMethod.POST)
	public void createVacances(@RequestBody Vacances vacances) throws Exception {
		if (!vacancesService.checkVacanceNotExisting(vacances)) {
			vacancesRepository.save(vacances);
		} else {
			throw new Exception("Ces vacances existent déjà");
		}
	}
}
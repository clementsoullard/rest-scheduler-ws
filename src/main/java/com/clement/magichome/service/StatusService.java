package com.clement.magichome.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.Channel;
import com.clement.magichome.object.Task;
import com.clement.magichome.object.WebStatus;
import com.clement.magichome.object.livebox.TVWrapper;
import com.clement.magichome.scheduler.DayScheduler;
import com.google.gson.Gson;

@Repository
public class StatusService {

	@Resource
	private PropertyManager propertyManager;

	static final Logger LOG = LoggerFactory.getLogger(StatusService.class);

	private Gson gson = new Gson();

	private TVWrapper tvWrapper;

	private WebStatus webStatus;

	@Value("${scheduler.tvcheckinterval}")
	private Integer timeIncrease;
	/**
	 * Store and track the time spent on each channel
	 */
	private static Map<Integer, Integer> secondsPerChannel = new HashMap<Integer, Integer>();

	private static Map<String, Integer> secondPcUsagePerUsers = new HashMap<String, Integer>();

	/**
	 * TODO The day scheduler shall not be present here.
	 */
	@Autowired
	private DayScheduler dayScheduler;

	@Autowired
	private TaskService taskService;

	@Autowired
	private BonPointDaoImpl bonPointDaoImpl;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private LogRepositoryImpl logRepositoryImpl;

	@Resource
	private FileService fileService;

	public void updatePCStatusLivelyParameters() {

		try {

			HttpURLConnection connection = getStreamStanbyStateFromPC();
			InputStream is = connection.getInputStream();
			int responseCode = connection.getResponseCode();
			byte buffer[] = new byte[512];
			if (is == null) {
				LOG.debug("Le service n'est pas démarré");
				return;
			}
			if (responseCode == 200) {
				int lenght = IOUtils.read(is, buffer);
				String userStr = new String(buffer, 0, lenght);
				webStatus.setCurrentLoggedUser(userStr);
				tracePcStatus(userStr);
				LOG.debug("Utilisateur connecté au PC :" + userStr);
			} else {
				LOG.debug("Code retour connection PC :" + responseCode);
				webStatus.setCurrentLoggedUser("Utilisateur non connecté");
			}
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * 
	 * Update TV status on information where you need to be reactive. Those
	 * parameters are.
	 * <ul>
	 * <li>Is the TV switched on.
	 * <li>Is the relay switched on.
	 * <li>What channel was watched on the instant of the request.
	 * </ul>
	 * This class also update the status of the relay (write function)
	 * 
	 * @return true if the TV should be swithed off.
	 */
	public boolean updateTvStatusLivelyParameters() {
		if (webStatus == null) {
			webStatus = new WebStatus();
		}

		boolean shouldPressOnOffButton = false;
		/**
		 * We read the status from the livebox
		 */
		InputStream is = getStreamStanbyStateFromLivebox();

		if (is != null) {
			InputStreamReader xml = new InputStreamReader(is);
			tvWrapper = gson.fromJson(xml, TVWrapper.class);
			Integer activeStandbyState = tvWrapper.getResult().getData().getActiveStandbyState();
			webStatus.setActiveStandbyState(activeStandbyState);
			Boolean standbyState = (activeStandbyState == 1);
			/**
			 * In case the TV is on we retrieve the media played id (channel)
			 */
			if (!standbyState) {
				traceTvStatus();
			}
			/**
			 * We write the standby state, so if the status is paused the
			 * countdown will stop.
			 */
			fileService.writeStandby(standbyState);

			/**
			 * We read the status of the Relay controlling power of the TV.
			 */
			Boolean relayStatus = fileService.getTvStatusRelay();
			webStatus.setRelayStatus(relayStatus);

			if (!relayStatus && !standbyState) {
				shouldPressOnOffButton = true;
			}
			LOG.debug("Checking status : Standby=" + standbyState + ", getTvStatusRelay=" + relayStatus);

		} else {
			tvWrapper = new TVWrapper();
		}

		return shouldPressOnOffButton;
	}

	/**
	 * This trace in DB the TV status
	 */
	private void traceTvStatus() {
		Integer channelId = tvWrapper.getResult().getData().getPlayedMediaId();
		webStatus.setPlayedMediaId(channelId);
		if (channelId != null) {
			List<Channel> channels = channelRepository.findByEpgId(channelId.toString());
			if (channels != null && channels.size() > 0) {
				Channel channel = channels.get(0);
				webStatus.setChannelName(channel.getName());
			}
		}
		if (channelId != null) {
			Integer minutes = secondsPerChannel.get(channelId);
			if (minutes == null) {
				minutes = 0;
			}
			/**
			 * Number of minutes is increased by 20s
			 */
			minutes += timeIncrease;
			secondsPerChannel.put(channelId, minutes);
		}

	}

	/**
	 * This trace in DB the TV status
	 */
	private void tracePcStatus(String user) {
		Integer secondsPc = secondsPerChannel.get(user);
		if (secondsPc == null) {
			secondsPc = 0;
		}
		/**
		 * Number of minutes is increased by 20s
		 */
		secondsPc += timeIncrease;
		secondPcUsagePerUsers.put(user, secondsPc);

	}

	/**
	 * Is oa map of the channel watched during the day
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getSecondsPerChannel() {
		return secondsPerChannel;
	}

	/**
	 * Is oa map of the channel watched during the day
	 * 
	 * @return
	 */
	public Map<String, Integer> getSecondsPerUserPc() {
		return secondPcUsagePerUsers;
	}

	/**
	 * Contact the livebox and get the status of it.
	 * 
	 * @return
	 * @throws IOException
	 */
	private InputStream getStreamStanbyStateFromLivebox() {
		String uri = null;
		try {
			if (propertyManager.getProductionMode()) {
				uri = propertyManager.getLiveboxUrlPrefix() + "/remoteControl/cmd?operation=10";
			} else {
				uri = "http://localhost:8080/tvscheduler/test/livebox-sample-actif.json";
			}
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			return connection.getInputStream();
		} catch (IOException e) {
			LOG.error("Could not connect to the livebox with uri " + uri);
			return null;
		}
	}

	/**
	 * Contact the livebox and get the status of it.
	 * 
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection getStreamStanbyStateFromPC() {
		String uri = null;
		try {
			uri = propertyManager.getPcUrlPrefix() + "/Api/status";
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			return connection;
		} catch (IOException e) {
			LOG.debug("Could not connect to the pc at " + uri);
			return null;
		}
	}

	/**
	 * This enrich all the parameters that are required on request from the web
	 * service.
	 */
	public void updateLessLivelyParameters() {
		if (webStatus == null) {
			webStatus = new WebStatus();
		}
		/***
		 * Update the number of bons points TODO is it necessary to perform this
		 * every 15 sec.
		 * 
		 */
		webStatus.setBonPoints(bonPointDaoImpl.sumBonPointV2().getTotal().intValue());

		webStatus.setTimeConsumedToday(logRepositoryImpl.getMinutesToday());

		/***
		 * Update the number of bons points from the beginning of the week. TODO
		 * is it necessary to perform this avery 15 sec.
		 */
		webStatus.setBonPointsWeek(bonPointDaoImpl.sumBonPointBeginningOfWeek().getTotal().intValue());

		/***
		 * Update the number of second remaining
		 */
		webStatus.setRemainingSecond(fileService.getSecondRemaining());
		/**
		 * Update when the next credit will happen
		 */
		if (dayScheduler.getCreditTask() != null) {
			webStatus.setDateOfCredit(dayScheduler.getCreditTask().getExecutionDate());
			webStatus.setAmountOfCreditInMinutes(dayScheduler.getCreditTask().getMinutes());
		}

		updateSufficientActionToWatchTv();
	}

	private boolean sufficientActionToWatchTv = false;

	public boolean getSufficientActionToWatchTv() {
		return sufficientActionToWatchTv;
	}

	/**
	 * Update the status if there are sufficient actions today to watch TV.
	 * 
	 * @return
	 */
	private void updateSufficientActionToWatchTv() {
		Integer nbTaskToday = 0;
		List<Task> tasks = taskService.getCesarTasksExpiringToday();
		if (tasks != null) {
			for (Task task : tasks) {
				if (task.getDone() != null && task.getDone()) {
					nbTaskToday++;
				}
			}
			if (nbTaskToday < 3) {
				sufficientActionToWatchTv = false;
			}
		}
		sufficientActionToWatchTv = true;
	}

	/**
	 * Get the tv status
	 * 
	 * @param withRefresh
	 *            if with refresh is required, there is a specifi
	 * @return
	 */
	public WebStatus getStatus() {
		return webStatus;
	}

}

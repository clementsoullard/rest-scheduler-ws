package com.clement.magichome.scheduler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.clement.magichome.PropertyManager;
import com.clement.magichome.object.Channel;
import com.clement.magichome.object.LogEntry;
import com.clement.magichome.service.BonPointDaoImpl;
import com.clement.magichome.service.ChannelRepository;
import com.clement.magichome.service.FileService;
import com.clement.magichome.service.LogRepository;
import com.clement.magichome.service.StatusService;
import com.clement.magichome.service.TaskService;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
/**
 * This class manages all preriodic tasks for a tv.
 * 
 * @author Clément
 *
 */
public class TvCheckScheduler {

	static final Logger LOG = LoggerFactory.getLogger(TvCheckScheduler.class);

	@Resource
	StatusService statusService;
	@Resource
	private LogRepository logRepository;

	@Resource
	private ChannelRepository channelRepository;

	@Resource
	private BonPointDaoImpl bonPointDaoImpl;

	@Resource
	private TaskService taskService;

	@Resource
	private PropertyManager propertyManager;

	private Date from = new Date();

	private Date to;

	@Resource
	private FileService fileService;

	/**
	 * Every 15 sec. check the status of the TV and depending on the result it
	 * will sitch the TV off.
	 */
	@Scheduled(cron = "*/${scheduler.tvcheckinterval} * * * * *")
	public void updateTvStatus() {
		Boolean shouldPressOnOffBasedOnTime = statusService.updateTvStatusLivelyParameters();
		if (shouldPressOnOffBasedOnTime) {
			LOG.debug("En dehors des horaires, on ne regarde pas la télé");
			pressOnOffButton();
		} else {
			LOG.debug("On laisse la télé tourner");
		}
		statusService.updatePCStatusLivelyParameters();

	}

	/** *Cache map for channel name */
	Map<Integer, String> channelNameCache = new HashMap<Integer, String>();

	/**
	 * Every 5 minutes the time spent watching a channel is stored in db adn
	 * also the time watching PC.
	 */
	@Scheduled(cron = "7 */5 * * * *")
	public void storeUsageInDb() throws IOException {
		LOG.debug("Storing in Db");
		to = new Date();
		Map<Integer, Integer> secondsPerChannel = statusService.getSecondsPerChannel();
		for (Integer channel : secondsPerChannel.keySet()) {
			Integer seconds = secondsPerChannel.get(channel);
			String channelName = getChannelName(channel);
			logRepository.save(new LogEntry("TV", channel, channelName, null, seconds, from, to));
		}
		from = new Date();
		secondsPerChannel.clear();
		/**
		 * 
		 */
		Map<String, Integer> secondsPc = statusService.getSecondsPerUserPc();
		for (String userName : secondsPc.keySet()) {
			Integer seconds = secondsPc.get(userName);
			logRepository.save(new LogEntry("PC", null, null, userName, seconds, from, to));
		}
		from = new Date();
		secondsPc.clear();

	}

	/**
	 * Get the channel name and manage a cache.
	 * 
	 * @param channel
	 * @return
	 */
	private String getChannelName(Integer channelId) {
		String channelName = channelNameCache.get(channelId);
		if (channelName == null) {
			List<Channel> channels = channelRepository.findByEpgId(channelId.toString());
			LOG.debug("Nombre de chaine " + channels.size());
			if (channels.size() > 0) {
				channelName = channels.get(0).getName();
				LOG.debug("Chanine name " + channelName);
			} else {
				channelName = "Chaine EPG:" + channelId.toString();
				LOG.debug("Chaine inconnue " + channelName);
			}
			channelNameCache.put(channelId, channelName);
		}
		return channelName;
	}

	/**
	 * Switch of TV, in case difference with the relay status
	 */
	private void pressOnOffButton() {
		LOG.debug("Appui sur le bouton on/off");
		if (propertyManager.getProductionMode()) {
			try {

				String uri = propertyManager.getLiveboxUrlPrefix() + "/remoteControl/cmd?operation=01&key=116&mode=0";
				URL url = new URL(uri);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/xml");
				connection.getInputStream().read();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}

	}

}

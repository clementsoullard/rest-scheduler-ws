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

@Configuration
@EnableAutoConfiguration
@EnableScheduling
/**
 * This class credit some minutes for the TV.
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
	private PropertyManager propertyManager;

	private Date from = new Date();

	private Date to;

	@Resource
	private FileService fileService;

	/**
	 * Every 15 sec. check the status of the TV and .
	 */
	@Scheduled(cron = "*/${scheduler.tvcheckinterval} * * * * *")
	public void updateTvStatus() {
		Boolean shouldPressOnOffButton = statusService.updateTvStatusLivelyParameters();
		if (shouldPressOnOffButton) {
			pressOnOffButton();
		}
	}

	/** *Cache map for channel name */
	Map<Integer, String> channelNameCache = new HashMap<Integer, String>();

	/**
	 * Every 5 minutes the time spent watching a channel is stored in db.
	 */
	@Scheduled(cron = "7 */5 * * * *")
	public void putInDb() throws IOException {
		to = new Date();
		Map<Integer, Float> minutesPerChannel = statusService.getSecondsPerChannel();
		for (Integer channel : minutesPerChannel.keySet()) {
			Float minutes = minutesPerChannel.get(channel);
			String channelName = getChannelName(channel);
			logRepository.save(new LogEntry("TV", channel, channelName, minutes, from, to));
		}
		from = new Date();
		minutesPerChannel.clear();
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
				channelName = "Chaine inconnue";
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

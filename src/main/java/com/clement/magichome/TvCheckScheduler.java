package com.clement.magichome;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.clement.magichome.object.Channel;
import com.clement.magichome.object.LogEntry;
import com.clement.magichome.object.TVStatus;
import com.clement.magichome.object.TVWrapper;
import com.clement.magichome.service.ChannelRepository;
import com.clement.magichome.service.LogRepository;
import com.google.gson.Gson;

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
	private LogRepository logRepository;

	@Resource
	private ChannelRepository channelRepository;

	@Resource
	private PropertyManager propertyManager;

	private Gson gson = new Gson();

	private TVWrapper tvWrapper = new TVWrapper();

	private Map<Integer, Float> minutesPerChannel = new HashMap<Integer, Float>();

	private Date from = new Date();

	private Date to;

	@Resource
	private FileService fileService;

	/**
	 * Every 15 sec. check the status of the TV and .
	 */
	@Scheduled(cron = "*/15 * * * * *")
	public void updateSandbyStatus() throws IOException {
		InputStreamReader xml = new InputStreamReader(getStreamStanbyStateFromLivebox());
		tvWrapper = gson.fromJson(xml, TVWrapper.class);

		Integer activeStandbyState = tvWrapper.getResult().getData().getActiveStandbyState();
		Boolean standbyState = (activeStandbyState == 1);
		if (!standbyState) {
			Integer channel = tvWrapper.getResult().getData().getPlayedMediaId();
			if (channel != null) {
				Float minutes = minutesPerChannel.get(channel);
				if (minutes == null) {
					minutes = 0F;
				}
				minutes += .25F;
				minutesPerChannel.put(channel, minutes);
				// LOG.debug("Chaine=" + channel + ", minute=" + minutes);
			}
		}

		fileService.writeStandby(standbyState);
		Boolean relayStatus = fileService.getTvStatusRelay();
		tvWrapper.getResult().setRelayStatus(relayStatus);

		tvWrapper.getResult().setRemainingSecond(fileService.getSecondRemaining());
		if (!relayStatus && !standbyState) {
			pressOnOffButton();
		}
		LOG.debug("Standby=" + standbyState + ", getTvStatusRelay=" + relayStatus + " ");

	}

	/** *Cache map for channel name */
	Map<Integer, String> channelNameCache = new HashMap<>();

	/**
	 * Every 15 sec. check the status of the TV and .
	 */
	@Scheduled(cron = "7 */5 * * * *")
	public void putInDb() throws IOException {
		to = new Date();
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

	/**
	 * Contact the livebox and get the status of it.
	 * 
	 * @return
	 * @throws IOException
	 */
	private InputStream getStreamStanbyStateFromLivebox() throws IOException {
		String uri = propertyManager.getLiveboxUrlPrefix() + "/remoteControl/cmd?operation=10";
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");
		return connection.getInputStream();
	}

	/**
	 * Get the tv status
	 * 
	 * @param withRefresh
	 *            if with refresh is required, there is a specifi
	 * @return
	 */
	public TVStatus getStandByState() {
		return tvWrapper.getResult();
	}

}

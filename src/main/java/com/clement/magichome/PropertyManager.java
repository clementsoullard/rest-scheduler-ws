package com.clement.magichome;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class PropertyManager {
	@Value("${scheduler.work.path}")
	String workPath;
	
	@Value("${tvscheduler.monitor}")
	Boolean monitorTVAndPC;


	@Value("${livebox.urlPrefix}")
	String liveboxUrlPrefix;

	@Value("${pc.urlPrefix}")
	String pcUrlPrefix;

	@Value("${production.mode}")
	Boolean productionMode;

	public String getPathCountDown() {
		return workPath + "/CD";
	}

	public String getPathSecondLine() {
		return workPath + "/SL";
	}

	public String getPathStandby() {
		return workPath + "/SB";
	}

	public String getPathStatus() {
		return workPath + "/ST";
	}

	public String getPathRemaining() {
		return workPath + "/REM";
	}

	public String getLiveboxUrlPrefix() {
		return liveboxUrlPrefix;
	}
	
	public Boolean getProductionMode() {
		return productionMode;
	}
	
	public String getPcUrlPrefix() {
		return pcUrlPrefix;
	}

	public Boolean getMonitorTVAndPC() {
		return monitorTVAndPC;
	}
	
	
	
	
}

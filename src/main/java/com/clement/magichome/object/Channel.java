package com.clement.magichome.object;

import org.springframework.data.annotation.Id;

public class Channel {

	@Id private String id;

	private String epgId;
	
	private String name;

	public String getEpgId() {
		return epgId;
	}

	public void setEpgId(String epgId) {
		this.epgId = epgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}

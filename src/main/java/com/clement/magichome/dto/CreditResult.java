package com.clement.magichome.dto;

import com.clement.magichome.object.WebStatus;

public class CreditResult {

	private String content;

	private WebStatus status;

	public CreditResult(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setStatus(WebStatus status) {
		this.status = status;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public WebStatus getStatus() {
		return status;
	}
}
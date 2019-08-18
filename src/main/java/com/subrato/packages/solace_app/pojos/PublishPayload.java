package com.subrato.packages.solace_app.pojos;

public class PublishPayload {
	
	private String topicName;
	private String message;
	
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}

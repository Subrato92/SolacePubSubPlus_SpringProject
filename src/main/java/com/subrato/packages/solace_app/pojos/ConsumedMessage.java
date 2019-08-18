package com.subrato.packages.solace_app.pojos;

public class ConsumedMessage {
	
	private final String msg;
	
	public ConsumedMessage(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
	
}

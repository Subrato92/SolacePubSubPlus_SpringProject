package com.subrato.packages.solace_app.config;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.subrato.packages.solace_app.pojos.RouterConfig;
import com.subrato.packages.solace_app.pojos.SessionResponse;

public class MsgRouter {
	
	private JCSMPProperties properties;
	private JCSMPSession session = null;
	
	public MsgRouter(RouterConfig config) {
		properties = new JCSMPProperties();
		
		properties.setProperty(JCSMPProperties.HOST, config.getHost());
		properties.setProperty(JCSMPProperties.USERNAME, config.getUsername());
		properties.setProperty(JCSMPProperties.VPN_NAME, config.getVpn_name());
		properties.setProperty(JCSMPProperties.PASSWORD, config.getPassword());
		
	}
	
	private String initializeSession() {
		String response;
		
		try {
			session = JCSMPFactory.onlyInstance().createSession(properties);
			response = "Success - Instance";
		} catch (InvalidPropertiesException e) {
			session = null;
			response = "Session Instance Creation Failed - " + e.getMessage();
		}
		
		return response;		
	}
	
	public SessionResponse connect() {
		String response = null;
		
		if( session == null ) {
			response = initializeSession();
		}
		
		if( session!=null && !session.isClosed()) {
			try {
				session.connect();
				response = "Success - Connected";
			} catch (JCSMPException e) {
				session = null;
				response = "Failed To Connect " + e.getMessage();
			}
		}
		
		return new SessionResponse(session, response);
	}

}

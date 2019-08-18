package com.subrato.packages.solace_app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPSession;
import com.subrato.packages.solace_app.config.Consumer;
import com.subrato.packages.solace_app.config.MsgRouter;
import com.subrato.packages.solace_app.config.Publisher;
import com.subrato.packages.solace_app.pojos.PublishPayload;
import com.subrato.packages.solace_app.pojos.PublishResponse;
import com.subrato.packages.solace_app.pojos.RouterConfig;
import com.subrato.packages.solace_app.pojos.SessionResponse;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
public class RestEndPoints {

	private MsgRouter router = null;
	private JCSMPSession session = null;
	private Publisher publisher = null;
	private Consumer consumer = null;

	@CrossOrigin
	@RequestMapping(value = "/publish", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public PublishResponse publish(PublishPayload payload) {

		if (session == null) {

			PublishResponse resp = new PublishResponse();
			resp.setStatus("Failed - Session not initialized");

			return resp;
		}

		try {
			if (publisher == null) {
				publisher = new Publisher(session, payload.getTopicName());
			}
		} catch (JCSMPException e) {

			PublishResponse resp = new PublishResponse();
			resp.setStatus("Failed : " + e.getMessage());

			return resp;
		}

		return publisher.publish(payload.getMessage());
	}

	@CrossOrigin
	@RequestMapping(value = "/initialize", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String initialize(RouterConfig config) {

		router = new MsgRouter(config);
		SessionResponse sResp = router.connect();

		session = sResp.getSession();
		System.out.println("[Initialize] Response: " + sResp.getResponse());

		return sResp.getResponse();
	}

	@CrossOrigin
	@RequestMapping(value = "/getMsg", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String consume(String topicName) {

		String response = "";

		if (session == null) {
			return "Failed - Session not initialized";
		}

		try {
			if (consumer == null) {
				consumer = new Consumer(session, topicName);
			}
		} catch (JCSMPException e) {
			return "Failed : " + e.getMessage();
		}

		try {
			response = consumer.getMessage();
		} catch (JCSMPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = "Failed - " + e.getMessage();
		}

		return response;
	}

}

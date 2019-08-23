package com.subrato.packages.solace_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
	private Logger log = LoggerFactory.getLogger(RestEndPoints.class);
	private String topicName = null;

	@CrossOrigin
	@RequestMapping(
			value = "/publish", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			headers = "Content-Type=application/json"
			)
	@ResponseBody
	public PublishResponse publish(@RequestBody PublishPayload payload) {

		if (session == null) {

			PublishResponse resp = new PublishResponse();
			resp.setStatus("Failed - Session not initialized");

			return resp;
		}

		try {
			if (publisher == null) {
				publisher = new Publisher(session, topicName);
			}
		} catch (JCSMPException e) {

			PublishResponse resp = new PublishResponse();
			resp.setStatus("Failed : " + e.getMessage());

			return resp;
		}
		
		log.info("[PRODUCER] Msg: " + payload.getMessage());
		
		return publisher.publish(payload.getMessage());
	}

	@CrossOrigin
	@RequestMapping(
			value = "/initialize", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.TEXT_PLAIN_VALUE,
			headers = "Content-Type=application/json")
	@ResponseBody
	public String initialize(@RequestBody RouterConfig config) {
		log.info("Into initialize Method");
		
		router = new MsgRouter(config);
		this.topicName = config.getTopicName();
		SessionResponse sResp = router.connect();

		session = sResp.getSession();

		try {
			consumer = new Consumer(session, topicName);
		} catch (JCSMPException e) {
			return "Failed : " + e.getMessage();
		}
		log.info("[Initialize] Response: " + sResp.getResponse());

		return "[Response Test]"+sResp.getResponse();
	}

	@CrossOrigin
	@RequestMapping(
			value = "/getMsg", 
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String consume() {

		String response = "";

		if (session == null) {
			return "Failed - Session not initialized";
		}

		try {
			response = consumer.getMessage();
			log.info("[CONSUME] Msg: " + response);
		} catch (JCSMPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = "Failed - " + e.getMessage();
		}

		return response;
	}

}

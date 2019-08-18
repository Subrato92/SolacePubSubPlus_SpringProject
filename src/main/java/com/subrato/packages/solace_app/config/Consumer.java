package com.subrato.packages.solace_app.config;

import java.util.concurrent.CountDownLatch;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageConsumer;
import com.solacesystems.jcsmp.XMLMessageListener;

public class Consumer {

	private XMLMessageConsumer consumer = null;
	private CountDownLatch latch = null;
	private Topic topic = null;

	public Consumer(JCSMPSession session, String topicRef) throws JCSMPException {
		
		topic = JCSMPFactory.onlyInstance().createTopic(topicRef);
		session.addSubscription(topic);
		latch = new CountDownLatch(1);
		
		consumer = session.getMessageConsumer(new XMLMessageListener() {

			@Override
			public void onReceive(BytesXMLMessage msg) {
				latch.countDown(); // unblock main thread
			}

			@Override
			public void onException(JCSMPException e) {
				System.out.printf("Consumer received exception: %s%n", e);
				latch.countDown(); // unblock main thread
			}
		});
	}
	
	public String getMessage() throws JCSMPException {
		
		String textMsg = null;

		consumer.start();

		try {
			latch.await(); // block here until message received, and latch will flip
		} catch (InterruptedException e) {
			System.out.println("I was awoken while waiting");
		}

		BytesXMLMessage msg = consumer.receive();
		if (msg instanceof TextMessage) {
			textMsg = ((TextMessage) msg).getText();
			System.out.printf("TextMessage received: '%s'%n", textMsg);
		} else {
			textMsg = msg.dump();
			System.out.printf("Message Dump:%n%s%n", textMsg);
			System.out.println("Message received.");
		}

		consumer.close();

		return textMsg;

	}

}

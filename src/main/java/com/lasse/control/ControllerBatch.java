package com.lasse.control;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lasse.model.EventString;

@Component
public class ControllerBatch {
	private static final Logger logger = LogManager.getLogger(ControllerBatch.class);

	public ControllerBatch() {
	}
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	/**
	 * Ausführung starten
	 */
	@Async
	public CompletableFuture<Boolean> run() {
		logger.info("Start");
		boolean erfolgreich = true;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		//PayloadApplicationEvent<String> event1 = new PayloadApplicationEvent<>(this, "Test Event 1");
		EventString event1 = new EventString(this, "Event 1", 1);
		publisher.publishEvent(event1);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		//PayloadApplicationEvent<String> event2 = new PayloadApplicationEvent<>(this, "Test Event 2");
		EventString event2 = new EventString(this, "Event 2", 1);
		publisher.publishEvent(event2);
		logger.info("Ende: " + erfolgreich);
		return CompletableFuture.completedFuture(erfolgreich);
	}

}

package com.github.kdvolder.fortune.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}
	
	@Scheduled(fixedDelay = 15000)
	public void delayedTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}

	@Scheduled(fixedDelay = 50000, initialDelay = 1000)
	public void lateTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}
	
	@Scheduled(cron = "0 15 10 15 * ?")
	public void scheduleTaskUsingCronExpression() {
	 
	    long now = System.currentTimeMillis() / 1000;
	    System.out.println(
	      "schedule tasks using cron jobs - " + now);
	}
	
	@Async
	public void doSomething() {
		System.out.println("Here we go");
	}

}
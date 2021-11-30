package com.github.kdvolder.fortune.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fortunes")
public class FortuneConfig {
	
	private String hello = "Default greeting message";
	
	private String[] messages;

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(String[] messages) {
		this.messages = messages;
	}

	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}

}

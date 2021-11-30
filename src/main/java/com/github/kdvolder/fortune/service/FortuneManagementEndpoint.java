package com.github.kdvolder.fortune.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import com.github.kdvolder.fortune.service.data.FortuneMessage;
import com.github.kdvolder.fortune.service.data.FortuneRepository;
import com.github.kdvolder.fortune.service.data.InitializeFortunes;

@Component
@Endpoint(id="fortunes", enableByDefault = true)
public class FortuneManagementEndpoint {

	@Autowired FortuneRepository fortunes;
	@Autowired InitializeFortunes init;

    public static class HelloInfo {
    	String cached;
    	String configured;
		public String getCached() {
			return cached;
		}
		public void setCached(String cached) {
			this.cached = cached;
		}
		public String getConfigured() {
			return configured;
		}
		public void setConfigured(String configured) {
			this.configured = configured;
		}
	}
    
	@ReadOperation
    public Iterable<FortuneMessage> fortunes() {
		return fortunes.findAll();
    }

	@ReadOperation
    public Optional<FortuneMessage> getFortune(@Selector long id) {
		return fortunes.findById(id);
    }

    @WriteOperation
    public FortuneMessage setFortune(@Selector long id, String message) {
    	FortuneMessage entity = new FortuneMessage();
    	entity.setId(id);
    	entity.setMessage(message);
		fortunes.save(entity);
		return entity;
    }

    @DeleteOperation
    public void delete(@Selector long id) {
    	fortunes.deleteById(id);
    }

    @DeleteOperation
    public void resetToDefault() {
    	fortunes.deleteAll();
    	init.contextStarted();
    }
	
}

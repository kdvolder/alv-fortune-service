package com.github.kdvolder.fortune.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class HelloController {
	
	@Autowired
	FortuneConfig conf;
	String cache;

	@GetMapping(value = "/hello")
	public String hello() {
		if (cache==null) {
			cache = conf.getHello();
		}
		return cache; 
	}
	
	public String getCache() {
		return cache;
	}

	public void setCache(String newCache) {
		this.cache = newCache;
	}
}

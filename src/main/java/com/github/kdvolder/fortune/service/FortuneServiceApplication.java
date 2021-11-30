package com.github.kdvolder.fortune.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import  org.springframework.cache.annotation.EnableCaching;
import  org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.github.kdvolder.fortune.service.data.FortuneMessage;
import com.github.kdvolder.fortune.service.data.FortuneRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
@EnableConfigurationProperties(FortuneConfig.class)
@EnableCaching
@EnableScheduling
public class FortuneServiceApplication {
	
	private static final Logger log = LoggerFactory.getLogger(FortuneServiceApplication.class);

	@Autowired
	FortuneRepository fortunes;

	Random rnd = new Random();

    @LoadBalanced @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

	@GetMapping("/")
	public String random() {
		//TODO: more efficient way to select a random element?
		List<FortuneMessage> all = new ArrayList<>();
		for (FortuneMessage m : fortunes.findAll()) {
			all.add(m);
		}
		return all.get(rnd.nextInt(all.size())).getMessage();
	}
	
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<FortuneMessage> stream() {
		Function<FortuneMessage, Mono<FortuneMessage>> progressiveDelay = new Function<FortuneMessage, Mono<FortuneMessage>>() {
			long iter = 0;

			@Override
			public Mono<FortuneMessage> apply(FortuneMessage t) {
				long delay = iter ++;
				delay = delay * delay;
				return Mono.just(t).delaySubscription(Duration.ofSeconds(delay));
			}
		};
		
		
		return Flux.fromIterable(fortunes.findAll()).repeat().flatMapSequential(progressiveDelay);
	}

	public static void main(String[] args) {
		SpringApplication.run(FortuneServiceApplication.class, args);
	}

	@PostMapping("/fortune")
	public FortuneMessage create(@RequestBody String message) {
		FortuneMessage msg = new FortuneMessage();
		msg.setMessage(message);
		return fortunes.save(msg);
	}

	
	@GetMapping("/fortune/{id}")
	public FortuneMessage getById(@PathVariable long id) {
		Optional<FortuneMessage> found = fortunes.findById(id);
		if (found.isPresent()) {
			return found.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fortune with that id");
	}

	@Bean
	@Primary
	public CacheManager primaryCacheManager() {
		return new ConcurrentMapCacheManager("vets", "addresses", "cities");
	}

	@Bean
	public CacheManager sampleCacheManagerOneCacheManager() {
		return new ConcurrentMapCacheManager("sampleCacheOne");
	}
	@Bean
	public CacheManager sampleCacheManagerTwoCache() {
		return new ConcurrentMapCacheManager("sampleCacheTwo");
	}
	
	@Bean
	public HttpTraceRepository htttpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}
	
	@Scheduled(fixedRate = 10000)
	public void reportFortne() {
		log.info("Fortune now {}", random());
	}

}

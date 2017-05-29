package com.bandi.spring;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

@Configuration
@PropertySource("classpath:activiti.properties")
@ComponentScan(basePackages = "com.bandi")
@Order(1)
@Slf4j
public class SpringConfiguration {

	private AnnotationConfigApplicationContext annotationConfigApplicationContext;

	public void initializeSpring() {
		annotationConfigApplicationContext = new AnnotationConfigApplicationContext("com.bandi");
		annotationConfigApplicationContext.start();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PreDestroy
	public void stopSpring() {
		log.debug("Stopping spring!!");
		annotationConfigApplicationContext.close();
	}

}

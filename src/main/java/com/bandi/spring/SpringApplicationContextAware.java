package com.bandi.spring;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringApplicationContextAware implements ApplicationContextAware {

	@Inject
	private static ApplicationContext CONTEXT;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		CONTEXT = ctx;
	}

	public static Object getBean(String name) {
		return CONTEXT.getBean(name);
	}

	public static <T> T getBean(Class<T> tClass) throws BeansException {
		return CONTEXT.getBean(tClass);
	}
}
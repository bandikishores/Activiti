package com.bandi.listener;

import javax.servlet.annotation.WebListener;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@WebListener
public class MyHealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

	public final HealthCheckRegistry HEALTH_CHECK_REGISTRY;

	@Override
	protected HealthCheckRegistry getHealthCheckRegistry() {
		return HEALTH_CHECK_REGISTRY;
	}

}
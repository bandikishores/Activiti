package com.bandi.listener;

import javax.servlet.annotation.WebListener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@WebListener
public class MyMetricsServletContextListener extends MetricsServlet.ContextListener {

	public final MetricRegistry METRIC_REGISTRY;

	@Override
	protected MetricRegistry getMetricRegistry() {
		return METRIC_REGISTRY;
	}

}

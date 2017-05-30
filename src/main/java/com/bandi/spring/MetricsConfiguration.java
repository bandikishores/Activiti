package com.bandi.spring;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.support.ServletContextAttributeExporter;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteReporter.Builder;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for supporting Metrics
 * 
 * @author kishore.bandi
 *
 */
@Configuration
@EnableMetrics
@Slf4j
public class MetricsConfiguration extends MetricsConfigurerAdapter {

	@Value("${metric.graphite.host}")
	private String graphiteHost;

	@Value("${metric.graphite.port}")
	private int graphitePort;

	@Value("${metric.graphite.interval}")
	private long interval;

	@Value("${metric.graphite.prefix}")
	private String prefix;

	@Inject
	private Graphite graphite;

	@Inject
	public MetricsConfiguration() {
		super();
	}

	@Bean
	@Primary
	public HealthCheckRegistry healthCheckRegistry() {
		return new HealthCheckRegistry();
	}

	@Bean
	@Primary
	public MetricRegistry metricRegistry() {
		return new MetricRegistry();
	}

	@Bean
	public Graphite graphite() {
		return new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
	}

	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
		GraphiteReporter graphiteReporter = getGraphiteReporterBuilder(metricRegistry).build(graphite);
		// registerReporter(JmxReporter.forRegistry(metricRegistry).build()).start();
		registerReporter(graphiteReporter).start(interval, TimeUnit.SECONDS);
	}

	@Bean
	public ServletContextAttributeExporter servletContextAttributeExporter(MetricRegistry metricRegistry,
			HealthCheckRegistry healthCheckRegistry) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("com.codahale.metrics.servlets.HealthCheckServlet.registry", healthCheckRegistry);
		attributes.put("com.codahale.metrics.servlets.MetricsServlet.registry", metricRegistry);
		ServletContextAttributeExporter servletContextAttributeExporter = new ServletContextAttributeExporter();
		servletContextAttributeExporter.setAttributes(attributes);
		return servletContextAttributeExporter;
	}

	private Builder getGraphiteReporterBuilder(MetricRegistry metricRegistry) {
		metricRegistry.register(MetricRegistry.name("jvm", "buffers"),
				new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
		metricRegistry.register(MetricRegistry.name("jvm", "fd", "usage"), new FileDescriptorRatioGauge());
		metricRegistry.register(MetricRegistry.name("jvm", "gc"), new GarbageCollectorMetricSet());
		metricRegistry.register(MetricRegistry.name("jvm", "memory"), new MemoryUsageGaugeSet());
		metricRegistry.register(MetricRegistry.name("jvm", "thread-states"), new ThreadStatesGaugeSet());

		return GraphiteReporter.forRegistry(metricRegistry).prefixedWith(getPrefix()).convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS).filter(MetricFilter.ALL);
	}

	private String getPrefix() {
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.error("Exception occured while getting hostname", e);
			return prefix;
		}
		return MetricRegistry.name(prefix, hostname);
	}
}
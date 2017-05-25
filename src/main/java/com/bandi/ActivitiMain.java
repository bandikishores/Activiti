package com.bandi;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bandi.servlet.AsyncRequestDispatcherServlet;
import com.bandi.spring.SpringConfiguration;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;

/**
 * End points available
 * 
 * 1) localhost:8283/async
 * 
 * @author kishore.bandi
 *
 */
public class ActivitiMain {

	public static void main(String[] args) throws Exception {
		int listenPort = 8283;

		Server server = new Server(listenPort);
		server.setStopAtShutdown(true);

		ServletContextHandler context = new ServletContextHandler();
		// context.setContextPath("/activiti"); // Set only if

		// Add Health Check related Servlets
		context.addServlet(MetricsServlet.class, "/metrics");
		context.addServlet(ThreadDumpServlet.class, "/threads");
		ServletHolder pingServletHolder = new ServletHolder(PingServlet.class);
		context.addServlet(pingServletHolder, "/ping");
		context.addServlet(HealthCheckServlet.class, "/healthcheck");
		ServletHolder adminServletHolder = context.addServlet(AdminServlet.class, "/admin");
		adminServletHolder.setInitParameter(AdminServlet.PING_URI_PARAM_KEY, "/ping");
		adminServletHolder.setInitParameter(AdminServlet.METRICS_URI_PARAM_KEY, "/metrics");
		adminServletHolder.setInitParameter(AdminServlet.HEALTHCHECK_URI_PARAM_KEY, "/healthcheck");
		adminServletHolder.setInitParameter(AdminServlet.THREADS_URI_PARAM_KEY, "/threads");

		// Bind Servlet
		ServletHolder asyncRequestDispatcherServletHolder = context.addServlet(AsyncRequestDispatcherServlet.class,
				"/async/*");
		asyncRequestDispatcherServletHolder.setAsyncSupported(true);
		// Setting the Init Order as initialize during start up is needed for
		// stopping first request from being dropped.
		asyncRequestDispatcherServletHolder.setInitOrder(1);

		// Setup Spring with Annotation.
		new SpringConfiguration().initializeSpring();
		context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
		context.setInitParameter("contextConfigLocation", SpringConfiguration.class.getName());
		context.addEventListener(new ContextLoaderListener());

		// Setup Spring context - with XMLs
		/*
		 * context.setInitParameter("contextConfigLocation",
		 * "classpath*:META-INF/spring/activiti-application-context.xml");
		 * 
		 */

		// Add handlers and contexts to server.
		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { context, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		server.join();
	}

}

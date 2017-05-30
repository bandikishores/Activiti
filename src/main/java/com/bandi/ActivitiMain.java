package com.bandi;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.bandi.jersey.JerseyResource;
import com.bandi.servlet.AsyncRequestDispatcherServlet;
import com.bandi.spring.SpringApplicationContextAware;
import com.bandi.spring.SpringConfiguration;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;

//@formatter:off
/**
 * End points available
 * 
 * 1) http://localhost:8283/async 
 * 2) http://localhost:8283/annotatedAsync 
 * 3) http://localhost:8283/admin 
 * 4) http://localhost:8283/jersey 
 * 5) http://localhost:8283/inner
 * 
 * @author kishore.bandi
 *
 */
//@formatter:on
public class ActivitiMain {

	public static void main(String[] args) throws Exception {
		int listenPort = 8283;

		Server server = new Server(listenPort);
		server.setStopAtShutdown(true);

		System.setProperty("org.eclipse.jetty.LEVEL=DEBUG", "true");

		// ServletContextHandler context = new ServletContextHandler(); // Add
		// only if you need just Servlet Context

		// context.setContextPath("/activiti"); // Set only if you need a
		// sub-path to be added for all the calls

		// Add Health Check related Servlets
		WebAppContext context = new WebAppContext();
		context.addServlet(MetricsServlet.class, "/metrics");
		context.addServlet(ThreadDumpServlet.class, "/threads");
		ServletHolder pingServletHolder = new ServletHolder(PingServlet.class);
		context.addServlet(pingServletHolder, "/ping");
		context.addServlet(HealthCheckServlet.class, "/healthcheck");
		ServletHolder adminServletHolder = context.addServlet(AdminServlet.class, "/admin");
		adminServletHolder.setInitParameter(AdminServlet.PING_URI_PARAM_KEY, "/../ping");
		adminServletHolder.setInitParameter(AdminServlet.METRICS_URI_PARAM_KEY, "/../metrics");
		adminServletHolder.setInitParameter(AdminServlet.HEALTHCHECK_URI_PARAM_KEY, "/../healthcheck");
		adminServletHolder.setInitParameter(AdminServlet.THREADS_URI_PARAM_KEY, "/../threads");

		// Bind Servlet
		ServletHolder asyncRequestDispatcherServletHolder = context.addServlet(AsyncRequestDispatcherServlet.class,
				"/async/*");
		asyncRequestDispatcherServletHolder.setAsyncSupported(true);
		// Setting the Init Order as initialize during start up is needed for
		// stopping first request from being dropped.
		asyncRequestDispatcherServletHolder.setInitOrder(10);

		// Setup Spring with Annotation.
		// new SpringConfiguration().initializeSpring();
		context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
		context.setInitParameter("contextConfigLocation", SpringConfiguration.class.getName());
		context.addEventListener(new ContextLoaderListener());

		/*context.setAttribute("com.codahale.metrics.servlets.HealthCheckServlet.registry",
				SpringApplicationContextAware.getBean(HealthCheckRegistry.class));
		context.setAttribute("com.codahale.metrics.servlets.MetricsServlet.registry",
				SpringApplicationContextAware.getBean(MetricRegistry.class));*/
		/*
		 * context.addEventListener(new MyMetricsServletContextListener());
		 * context.addEventListener(new MyHealthCheckServletContextListener());
		 */

		String jar = ActivitiMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		System.out.println("path = " + jar);
		context.setResourceBase("/");

		// Setup Annotation driven Scanning of Web Services.
		context.setConfigurations(new Configuration[] { new AnnotationConfiguration(), new WebInfConfiguration(),
				new WebXmlConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(),
				new EnvConfiguration(), new PlusConfiguration(), new JettyWebXmlConfiguration() });
		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
		// Add this string to the end if you want to search in specific jars
		// ".*/classes/.*|.*/foo-[^/]*\\.jar$"

		context.setContextPath("/");
		context.setParentLoaderPriority(true);

		// Jersey Configuration
		ResourceConfig config = new ResourceConfig();
		config.packages("com.inmobi.jersey");
		ServletHolder servlet = new ServletHolder(new ServletContainer(config));
		servlet.setInitOrder(1);
		context.addServlet(servlet, "/*");
		/*
		 * ServletHolder jerseyServlet =
		 * context.addServlet(org.glassfish.jersey.servlet.ServletContainer.
		 * class, "/rest/*"); jerseyServlet.setInitOrder(0);
		 * jerseyServlet.setInitParameter(
		 * "jersey.config.server.provider.classnames",
		 * "org.glassfish.jersey.media.multipart.MultiPartFeature;org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;org.glassfish.jersey.filter.LoggingFilter"
		 * ); jerseyServlet.setInitParameter(
		 * "jersey.config.server.provider.packages", "com.inmobi");
		 */

		// Add handlers and contexts to server.
		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { context, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		server.join();
	}

}

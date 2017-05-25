package com.bandi.servlet;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.bandi.service.TestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncRequestDispatcherServlet extends HttpServlet {

	@Inject
	private TestService testService;

	ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String resourcePath = req.getPathInfo();
		log.debug("serving for " + resourcePath);

		final AsyncContext aCtx = req.startAsync(req, resp);
		aCtx.setTimeout(10000L);
		aCtx.addListener(new AsyncServletListener(testService));

		newScheduledThreadPool.execute(() -> {
			try {
				testService.writeResponse(aCtx);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		log.debug("async context completed");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	@Override
	public void destroy() {
		super.destroy();
		newScheduledThreadPool.shutdown();
	}

}

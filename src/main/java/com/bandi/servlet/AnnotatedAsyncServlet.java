package com.bandi.servlet;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.bandi.service.TestService;

import lombok.extern.slf4j.Slf4j;

@WebServlet(urlPatterns = "/annotatedAsync", asyncSupported = true)
@Slf4j
public class AnnotatedAsyncServlet extends HttpServlet {

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

		aCtx.start(() -> {
			try {
				Thread.sleep(100);
				aCtx.getResponse().getWriter().println("Annotated Async Servlet called");
				aCtx.complete();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		log.debug("annotated async context completed");
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

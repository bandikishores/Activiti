package com.bandi.jersey;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ManagedAsync;

import com.bandi.service.TestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("jersey")
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class JerseyResource {

	private final TestService testService;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String jerseyEndPoint() {
		return testService.getJersey();
	}

	@GET
	@Path("inner")
	@Produces(MediaType.TEXT_PLAIN)
	public String jerseyInnerEndPoint() {
		return "Inner";
	}

	@GET
	@ManagedAsync
	@Path("async")
	@Produces(MediaType.TEXT_PLAIN)
	public void jerseyAsyncEndPoint(@Suspended final AsyncResponse asyncResponse) {
		try {
			Thread.sleep(200);
			asyncResponse.resume("Response from Async Rest Service");
			log.info("Async Jersey call responded with result");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

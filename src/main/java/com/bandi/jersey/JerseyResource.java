package com.bandi.jersey;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.server.ManagedAsync;

import com.bandi.service.TestService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("jersey")
@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class JerseyResource {

	private final String Json = "";
	
	private static AtomicInteger totalCalls = new AtomicInteger(0);
	private final TestService testService;
	
	private String LoadJson() {
		try {
			return FileUtils.readFileToString(new File("C:\\Users\\kibandi\\Desktop\\SimpleExchangeOutput.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

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
	@Path("sleep")
	@Produces(MediaType.APPLICATION_JSON)
	public String jerseyInnerEndPoint(@QueryParam("sleep") @DefaultValue("50") int sleep) {
		try {
			totalCalls.incrementAndGet();
			System.out.println(totalCalls.get());
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return LoadJson();
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

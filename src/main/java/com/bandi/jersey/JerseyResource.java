package com.bandi.jersey;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.RequiredArgsConstructor;

@Path("jersey")
@RequiredArgsConstructor(onConstructor = @__({ @Inject}))
public class JerseyResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String jerseyEndPoint() {
		return "Jersey";
	}

	@GET
	@Path("inner")
	@Produces(MediaType.TEXT_PLAIN)
	public String jerseyInnerEndPoint() {
		return "Inner";
	}
}

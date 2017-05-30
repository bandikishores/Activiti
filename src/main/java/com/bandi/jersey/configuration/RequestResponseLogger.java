package com.bandi.jersey.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

enum PathToIgnore {
	PING("ping");

	private String path;

	private PathToIgnore(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}

@Slf4j
@Provider
public class RequestResponseLogger extends LoggingFeature implements ContainerRequestFilter, ContainerResponseFilter {

	private static final int MAX_SIZE_TO_LOG = 5000;
	private final AtomicLong requestIdCounter = new AtomicLong(0);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (ignoreLogging(requestContext)) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" - Method: ").append(requestContext.getMethod()).append("\n");
		sb.append(" - Path: ").append(requestContext.getUriInfo().getPath()).append("\n");
		sb.append(" - Header: ").append(requestContext.getHeaders()).append("\n");
		sb.append(" - Entity: ").append(getEntityBody(requestContext)).append("\n");

		MDC.put("customId", String.valueOf(requestIdCounter.incrementAndGet()));

		log.debug("HTTP REQUEST : \n" + sb.toString());
	}

	private boolean ignoreLogging(ContainerRequestContext requestContext) {
		for (PathToIgnore pathToIgnore : PathToIgnore.values()) {
			if (pathToIgnore.getPath().equalsIgnoreCase(requestContext.getUriInfo().getPath())) {
				return true;
			}
		}
		return false;
	}

	private String getEntityBody(ContainerRequestContext requestContext) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = requestContext.getEntityStream();

		final StringBuilder b = new StringBuilder();
		try {
			ReaderWriter.writeTo(in, out);

			byte[] requestEntity = out.toByteArray();
			if (requestEntity.length == 0) {
				b.append("").append("\n");
			} else if (requestEntity.length > MAX_SIZE_TO_LOG) {
				b.append("Request Body is too big to log ").append(requestEntity.length).append("\n");
			} else {
				b.append(new String(requestEntity)).append("\n");
			}
			requestContext.setEntityStream(new ByteArrayInputStream(requestEntity));

		} catch (IOException ex) {
			log.error("Exception occured while Reading request body : ", ex);
		}
		return b.toString();
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		if (ignoreLogging(requestContext)) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" - Path: ").append(requestContext.getUriInfo().getPath()).append("\n");
		sb.append(" - Header: ").append(responseContext.getHeaders()).append("\n");
		if (responseContext.getEntity() != null && responseContext.getEntity().toString().length() > MAX_SIZE_TO_LOG) {
			sb.append("Response Body is too big to log ").append(responseContext.getEntity().toString().length())
					.append("\n");
		} else {
			sb.append(" - Entity: ").append(responseContext.getEntity()).append("\n");
		}
		log.debug("HTTP RESPONSE : \n" + sb.toString());
		MDC.clear();
	}
}

package com.bandi.jersey.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

	private static final String UTF_8 = "UTF-8";

	private static final double CURRENT_VERSION = 1.0;

	private static Map<Double, Gson> gsonVersionedCache = new ConcurrentHashMap<>();

	private final HttpHeaders httpHeaders;

	public GsonMessageBodyHandler(@Context HttpHeaders httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	private Gson getGson() {

		double version = CURRENT_VERSION;

		if (httpHeaders != null && StringUtils.isNotBlank(httpHeaders.getHeaderString("version"))
				&& NumberUtils.isCreatable(httpHeaders.getHeaderString("version"))) {
			version = Double.valueOf(httpHeaders.getHeaderString("version"));
		}

		if (gsonVersionedCache.containsKey(version)) {
			return gsonVersionedCache.get(version);
		}

		final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setVersion(version);

		final Gson gson = gsonBuilder.create();
		gsonVersionedCache.put(version, gson);

		return gson;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		try (InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8)) {
			return getGson().fromJson(streamReader, genericType);
		} catch (JsonSyntaxException | MalformedJsonException e) {
			log.error("JSON Passed was Invalid ", e);
			throw new RuntimeException("Invalid Request JSON", e);
		} catch (IOException e) {
			log.error("Exception Occured while converting json To Object ", e);
		}
		return null;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {

		try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8)) {
			getGson().toJson(t, genericType, writer);
		}
	}

}

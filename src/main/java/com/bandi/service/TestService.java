package com.bandi.service;

import java.io.IOException;

import javax.servlet.AsyncContext;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestService {

	public TestService() {
		log.info("Called TestService Constructor");
	}

	public String printSuccess() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("success");
		return "value from Service";
	}

	public void writeResponse(AsyncContext aCtx) throws IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("writing response");
		aCtx.getResponse().getWriter().println("value from Service");
		aCtx.complete();
	}
}

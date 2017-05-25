package com.bandi.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

import com.bandi.service.TestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncServletListener implements AsyncListener {

	TestService testService;

	public AsyncServletListener(TestService testService) {
		this.testService = testService;
	}

	public void onComplete(AsyncEvent event) throws IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Completed Listener");
	}

	public void onTimeout(AsyncEvent event) throws IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Completed Timeout");
	}

	public void onError(AsyncEvent event) throws IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Completed OnError");
	}

	public void onStartAsync(AsyncEvent event) throws IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Started Async");
	}

}

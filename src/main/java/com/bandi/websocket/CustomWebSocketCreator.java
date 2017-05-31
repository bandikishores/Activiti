package com.bandi.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomWebSocketCreator implements WebSocketCreator {
	// private MyBinaryEchoSocket binaryEcho;
	private StringWebSocket textWebSocket;

	public CustomWebSocketCreator() {
		// Create the reusable sockets
		// this.binaryEcho = new MyBinaryEchoSocket();
		this.textWebSocket = new StringWebSocket();
	}

	@Override
	public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
		for (String subprotocol : req.getSubProtocols()) {
			/*
			 * if ("binary".equals(subprotocol)) {
			 * resp.setAcceptedSubProtocol(subprotocol); return binaryEcho; }
			 */
			if ("text".equals(subprotocol)) {
				resp.setAcceptedSubProtocol(subprotocol);
				return textWebSocket;
			}
		}

		log.info("Protocol passed was not Matching with Text, {}", req.getSubProtocols());

		// No valid subprotocol in request, ignore the request
		return null;
	}
}

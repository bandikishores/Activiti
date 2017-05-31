package com.bandi.websocket;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
@WebServlet(name = "My String WebSocket Servlet", urlPatterns = { "/stringWebSocket" })
public class StringWebSocketServlet extends WebSocketServlet {
	@Override
	public void configure(WebSocketServletFactory factory) {
		// set a 10 second timeout
		factory.getPolicy().setIdleTimeout(10000);

		// register MyEchoSocket as the WebSocket to create on Upgrade, if you
		// want to register just the WebSocket directly.
		// factory.register(StringWebSocket.class);

		factory.setCreator(new CustomWebSocketCreator());
	}
}

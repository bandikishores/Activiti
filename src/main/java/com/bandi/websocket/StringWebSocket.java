package com.bandi.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebSocket(maxTextMessageSize = 64 * 1024)
public class StringWebSocket {

	@OnWebSocketMessage
	public void onText(Session session, String message) {
		if (session.isOpen()) {
			log.info("Echoing back message {}", message);
			// echo the message back
			session.getRemote().sendString("Websocket Server responded with : " + message, null);
		} else {
			log.info("Session is not active!!");
		}
	}

	@OnWebSocketMessage
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		log.info("Binary data received {} {}", offset, len);
	}

	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		log.info("Socket Closed with code {} and reason {} ", statusCode, reason);
	}

	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {
		log.info("Web Socket Connect received, Session : {}", session);
	}

	@OnWebSocketError
	public void onWebSocketError(Throwable cause) {
		log.info("Web Socket Error received, cause : {}", cause, cause);
	}

	@OnWebSocketFrame
	public void onWebSocketFrame(Session session, Frame frame) {
		log.info("Web Socket Frame received, Session : {}, Frame : {}", session, frame);
	}

}

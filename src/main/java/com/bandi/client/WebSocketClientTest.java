package com.bandi.client;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class WebSocketClientTest {
	public static void main(String[] args) {
		String destUri = "ws://localhost:8283/stringWebSocket";
		if (args.length > 0) {
			destUri = args[0];
		}

		WebSocketClient client = new WebSocketClient();
		SimpleSocketClient socket = new SimpleSocketClient();
		try {
			client.start();

			URI echoUri = new URI(destUri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			request.setSubProtocols("text");
			client.connect(socket, echoUri, request);
			System.out.printf("Connecting to : %s%n", echoUri);

			// wait for closed socket connection.
			socket.awaitClose(50, TimeUnit.SECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

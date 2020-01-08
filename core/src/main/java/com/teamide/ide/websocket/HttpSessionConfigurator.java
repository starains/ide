//package com.teamide.ide.websocket;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.HandshakeResponse;
//import javax.websocket.server.HandshakeRequest;
//import javax.websocket.server.ServerEndpointConfig;
//import javax.websocket.server.ServerEndpointConfig.Configurator;
//
//public class HttpSessionConfigurator extends Configurator {
//	@Override
//	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//		HttpSession httpSession = (HttpSession) request.getHttpSession();
//		if (sec != null && sec.getUserProperties() != null) {
//			sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
//		}
//	}
//
//}
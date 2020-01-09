package com.teamide.server;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		String root = System.getProperty("SERVER_ROOT");
		String port = System.getProperty("SERVER_PORT");
		String hostname = System.getProperty("SERVER_HOSTNAME");
		String contextpath = System.getProperty("SERVER_CONTEXTPATH");
		String app = System.getProperty("SERVER_APP");

		ServerTomcat tomcat = new ServerTomcat(root, hostname, Integer.valueOf(port), app, contextpath);
		tomcat.startTomcat();
		tomcat.restartApp();
	}

}

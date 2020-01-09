package com.teamide.server;
//package com.coospro;
//
//import java.io.File;
//
//import org.apache.commons.io.FileUtils;
//
//import com.alibaba.fastjson.JSONObject;
//
//public class ServerEventListener extends Thread {
//
//	public final String root;
//
//	private static final ServerEventListener LISTENER = new ServerEventListener();
//
//	private final ServerTomcat server;
//
//	private final long wait = 1000 * 5;
//
//	public static ServerEventListener get() {
//		return LISTENER;
//	}
//
//	private ServerEventListener() {
//		String root = System.getenv("server.root");
//		if (root == null || root.trim().length() == 0) {
//			root = System.getProperty("server.root");
//		}
//		if (root == null || root.trim().length() == 0) {
//			System.err.println("server.root is null.");
//			System.exit(1);
//		}
//		this.root = root;
//		JSONObject config = readConfig();
//		if (config == null) {
//			System.err.println("server.json is null.");
//			System.exit(1);
//		}
//		server = new ServerTomcat(this.root, readConfig());
//	}
//
//	public ServerEvent getEvent() {
//		String event = readEvent();
//		if (event != null) {
//			try {
//				return ServerEvent.valueOf(event);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
//
//	public JSONObject readConfig() {
//		JSONObject json = new JSONObject();
//		String text = read(getConfigFile());
//		if (text != null && text.trim().length() > 0) {
//			json = JSONObject.parseObject(text);
//		}
//		return json;
//
//	}
//
//	public String readEvent() {
//		try {
//			return read(getEventFile());
//		} finally {
//			File file = getEventFile();
//			if (file.exists() && file.isFile()) {
//				file.delete();
//			}
//		}
//
//	}
//
//	public File getConfigFile() {
//		return new File(root, "server.json");
//	}
//
//	public File getEventFile() {
//		return new File(root, "server.event");
//	}
//
//	public String read(File file) {
//		if (file == null || !file.exists() || !file.isFile()) {
//			return null;
//		}
//		try {
//			return FileUtils.readFileToString(file, "UTF-8");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
//
//	@Override
//	public void run() {
//		server.startTomcat();
//		while (true) {
//			try {
//				Thread.sleep(wait);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			ServerEvent event = getEvent();
//			doEvent(event);
//		}
//	}
//
//	public void doEvent(ServerEvent event) {
//		if (event == null) {
//			return;
//		}
//		System.out.println(event);
//		switch (event) {
//		case START_TOMCAT:
//			server.startTomcat();
//			break;
//		case STOP_TOMCAT:
//			server.stopTomcat();
//			break;
//		case RESTART_APP:
//			server.restartApp();
//			break;
//		case START_APP:
//			server.startApp();
//			break;
//		case STOP_APP:
//			server.stopApp();
//			break;
//		case UPDATE_APP:
//			server.updateApp();
//			break;
//		default:
//			break;
//		}
//	}
//
//}

package com.teamide.ide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IDEConf {

	private final Server server = new Server();

	private IDEConf() {

	}

	public Server getServer() {
		return server;
	}

	public class Server {
		private String hostname = "0.0.0.0";
		private String port = "19000";
		private String timeout = "7200";
		private String contextPath = "/";

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getTimeout() {
			return timeout;
		}

		public void setTimeout(String timeout) {
			this.timeout = timeout;
		}

		public String getContextPath() {
			return contextPath;
		}

		public void setContextPath(String contextPath) {
			this.contextPath = contextPath;
		}

	}

	public static IDEConf load(File conf) {
		IDEConf ideConf = new IDEConf();
		Map<String, String> map = readConf(conf);

		if (map.get("server.hostname") != null && map.get("server.hostname").length() > 0) {
			ideConf.getServer().setHostname(map.get("server.hostname"));
		}
		if (map.get("server.port") != null && map.get("server.port").length() > 0) {
			ideConf.getServer().setPort(map.get("server.port"));
		}
		if (map.get("server.context-path") != null && map.get("server.context-path").length() > 0) {
			ideConf.getServer().setContextPath(map.get("server.context-path"));
		}
		if (map.get("server.timeout") != null && map.get("server.timeout").length() > 0) {
			ideConf.getServer().setTimeout(map.get("server.timeout"));
		}

		return ideConf;
	}

	public static Map<String, String> readConf(File conf) {
		Map<String, String> map = new HashMap<String, String>();
		if (conf == null || !conf.exists()) {
			return map;
		}
		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(conf));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}

				String name = line;
				String value = "";
				int index = line.indexOf("=");
				if (index > 0) {
					name = line.substring(0, index).trim();
					if (line.length() > index + 1) {
						value = line.substring(index + 1).trim();
						if (value.startsWith("\"") && value.endsWith("\"")) {
							value = value.substring(1, value.length() - 1);
						} else if (value.startsWith("'") && value.endsWith("'")) {
							value = value.substring(1, value.length() - 1);
						}
					}
				}
				map.put(name, value);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
			}
		}
		return map;
	}
}

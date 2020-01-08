package com.teamide.deploer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class IDEOption {

	private static final IDEOption OPTION = load();

	public static IDEOption get() {
		return OPTION;
	}

	private static IDEOption load() {
		JSONObject json = new JSONObject();

		Properties properties = new Properties();
		InputStream stream = null;
		try {
			stream = new FileInputStream(IDEConstant.CONF);
			properties.load(stream);
			json = (JSONObject) JSON.toJSON(properties);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {

				}
			}
		}
		IDEOption option = json.toJavaObject(IDEOption.class);

		JSONObject clientJSON = read(json, "client");
		option.client = new Client();
		if (clientJSON != null) {
			option.client = clientJSON.toJavaObject(IDEOption.Client.class);
		}

		JSONObject serverJSON = read(json, "server");
		option.server = new Server();
		if (serverJSON != null) {
			option.server = serverJSON.toJavaObject(IDEOption.Server.class);
		}

		return option;
	}

	private static JSONObject read(JSONObject json, String prefix) {
		JSONObject prefixJSON = new JSONObject();
		for (String key : json.keySet()) {
			key = key.trim();
			if (key.startsWith(prefix + ".")) {
				Object value = json.get(key);
				String name = key.substring((prefix + ".").length());
				prefixJSON.put(name, value);
			}
		}
		return prefixJSON;
	}

	private String mode;

	private String name;

	private String token;

	private Client client;

	private Server server;

	static public class Client {

		private String server;

		public String getServer() {
			return server;
		}

		public void setServer(String server) {
			this.server = server;
		}

	}

	static public class Server {

		private Integer port;

		private String host;

		private String contextPath;

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getContextPath() {
			return contextPath;
		}

		public void setContextPath(String contextPath) {
			this.contextPath = contextPath;
		}

	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}

package com.teamide.deployer;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deployer.server.ServerListener;
import com.teamide.util.StringUtil;

public class IDEDeployer {

	private final String[] args;

	public final JSONObject param = new JSONObject();

	private final String model;

	public final Listener listener;

	public IDEDeployer(String[] args) throws Exception {

		this.args = args;
		resolveParam(this.args);
		if (StringUtil.isNotEmpty(param.getString("TEAMIDE_DEPLOER_HOME"))) {
			System.setProperty("TEAMIDE_DEPLOER_HOME", param.getString("TEAMIDE_DEPLOER_HOME"));
		}
		String model = param.getString("model");
		if (StringUtil.isEmpty(model)) {
			model = "server";
		}
		this.model = model;
		switch (this.model.toUpperCase()) {
		case "SERVER":
			listener = new ServerListener(this);
			break;
		case "CLIENT":
			// return new ClientListener(option.getName(), option.getToken(),
			// option.getClient());
			// break;
		default:
			throw new Exception("no listener for model [" + model + "].");
		}
		
		

	}

	private void resolveParam(String[] args) {
		if (args == null) {
			return;
		}
		for (String arg : args) {
			if (arg == null || !arg.startsWith("--")) {
				continue;
			}
			arg = arg.substring(2);
			String name = arg;
			String value = "";
			int index = arg.indexOf("=");
			if (index > 0) {
				name = arg.substring(0, index);
				if (arg.length() > index + 1) {
					value = arg.substring(index + 1);
					if (value.startsWith("\"") && value.endsWith("\"")) {
						value = value.substring(1, value.length() - 1);
					} else if (value.startsWith("'") && value.endsWith("'")) {
						value = value.substring(1, value.length() - 1);
					}
				}
			}
			param.put(name, value);
		}

	}

}

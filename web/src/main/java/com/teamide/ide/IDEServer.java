package com.teamide.ide;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.IDEConf.Server;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.util.StringUtil;

public class IDEServer implements Runnable {

	private final String[] args;

	private final JSONObject param = new JSONObject();

	private final Server server;

	public IDEServer(String[] args) {
		this.args = args;
		resolveParam(this.args);
		if (StringUtil.isNotEmpty(param.getString("TEAMIDE_HOME"))) {
			System.setProperty("TEAMIDE_HOME", param.getString("TEAMIDE_HOME"));
		}
		server = IDEConstant.CONF.getServer();
		if (StringUtil.isNotEmpty(param.getString("port"))) {
			server.setPort(param.getString("port"));
		}
	}

	IDETomcat tomcat;

	@Override
	public void run() {
		System.out.println("TEAMIDE_HOME:" + IDEConstant.HOME);
		System.out.println("server:" + JSON.toJSONString(server));

		tomcat = new IDETomcat(server);
		try {
			tomcat.startTomcat();
		} catch (Exception e) {
			e.printStackTrace();
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

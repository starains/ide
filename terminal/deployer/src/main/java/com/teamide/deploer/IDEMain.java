package com.teamide.deploer;

import com.teamide.deploer.client.ClientListener;
import com.teamide.deploer.server.ServerListener;
import com.teamide.deploer.util.StringUtil;

public class IDEMain {

	public static void main(String[] args) throws Exception {
		Listener listener = createListener(IDEOption.get());

		new Thread(listener).start();
	}

	private static Listener createListener(IDEOption option) throws Exception {
		if (option == null) {
			throw new Exception("option is null.");
		}
		if (StringUtil.isEmpty(option.getMode())) {
			throw new Exception("model is null.");
		}
		switch (option.getMode().toUpperCase()) {
		case "SERVER":
			return new ServerListener(option.getName(), option.getToken(), option.getServer());
		case "CLIENT":
			return new ClientListener(option.getName(), option.getToken(), option.getClient());
		default:
			throw new Exception("model is null.");
		}
	}
}

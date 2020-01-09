package com.teamide.deploer.server;

import com.teamide.deploer.Listener;
import com.teamide.deploer.IDEOption.Server;
import com.teamide.deploer.util.StringUtil;

public class ServerListener extends Listener {

	public final Server server;

	public ServerTomcat tomcat;

	public ServerListener(String name, String token, Server server) throws Exception {
		super(name, token);

		this.server = server;

		if (server == null) {
			throw new Exception("server is null.");
		}
		if (server.getPort() == null) {
			throw new Exception("server.port is null.");
		}
		if (StringUtil.isEmpty(server.getContextPath())) {
			server.setContextPath("/");
		}
		if (StringUtil.isEmpty(server.getHost())) {
			server.setHost("0.0.0.0");
		}
	}

	@Override
	public void listen() throws Exception {
		tomcat = new ServerTomcat(this);
		tomcat.await();
	}

}

package com.teamide.deploer.server;

import com.teamide.deploer.Listener;
import com.teamide.deploer.IDEDeployer;

public class ServerListener extends Listener {

	public IDEDeployer deployer;

	public ServerTomcat tomcat;

	public ServerListener(IDEDeployer deployer) throws Exception {
		this.deployer = deployer;
	}

	@Override
	public void listen() throws Exception {
		tomcat = new ServerTomcat(deployer);
		tomcat.await();
	}

}

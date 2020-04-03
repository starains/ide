package com.teamide.deployer.server;

import com.teamide.deployer.IDEDeployer;
import com.teamide.deployer.Listener;

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

package com.teamide.deployer;

public class IDEMain {

	public static void main(String[] args) throws Exception {

		IDEDeployer deployer = new IDEDeployer(args);

		new Thread(deployer.listener).start();
	}

}

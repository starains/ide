package com.teamide.deployer.starter.shell.node;

import java.io.File;

import com.teamide.deployer.shell.Shell;
import com.teamide.deployer.shell.node.NodeShell;
import com.teamide.deployer.starter.Starter;
import com.teamide.deployer.starter.StarterShell;

public class NodeStarterProcess extends StarterShell {

	private final String node_home;

	public NodeStarterProcess(Starter starter) {
		super(starter);

		this.node_home = starter.starterJSON.getString("node_home");

	}

	@Override
	public Shell getShell() {
		NodeShell shell = new NodeShell(starter.starterFolder);
		return shell;
	}

	@Override
	public String getStartShell() throws Exception {
		NodeShell shell = (NodeShell) this.shell;
		shell.setNode_home(getNodeHome());
		shell.setNode_command(getNodeCommand());

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

	public String getNodeCommand() {
		return starter.starterJSON.getJSONObject("option").getString("nodecommand");
	}

	public String getNodeHome() {
		return node_home;
	}

}

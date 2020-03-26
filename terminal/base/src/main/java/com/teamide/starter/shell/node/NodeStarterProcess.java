package com.teamide.starter.shell.node;

import java.io.File;

import com.teamide.shell.Shell;
import com.teamide.shell.node.NodeShell;
import com.teamide.starter.StarterParam;
import com.teamide.starter.StarterShell;

public class NodeStarterProcess extends StarterShell {

	private final String node_home;

	public NodeStarterProcess(StarterParam param) {
		super(param);

		this.node_home = param.starterJSON.getString("node_home");

	}

	@Override
	public Shell getShell() {
		NodeShell shell = new NodeShell(param.starterFolder);
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
		return param.getOptionString("nodecommand");
	}

	public String getNodeHome() {
		return node_home;
	}

}

package com.teamide.ide.shell.node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teamide.ide.shell.Shell;
import com.teamide.util.StringUtil;

public class NodeShell extends Shell {

	public NodeShell(File starterFolder) {
		super(starterFolder);
	}

	private String node_home;

	private String node_command;

	@Override
	public List<String> getWindowShell() {
		List<String> shell = new ArrayList<String>();
		// shell.add("@echo off");
		// shell.add("cmd");
		StringBuffer run = new StringBuffer();
		// run.append("start /b ");
		if (StringUtil.isEmpty(node_home)) {
			run.append(" ");
		} else {
			run.append(" ");
			run.append(node_home + "/bin/");
		}

		if (!StringUtil.isEmpty(node_command)) {
			run.append(node_command.trim());
		}
		run.append(" ");

		// if (logFile != null) {
		// run.append(" > " + logFile.getAbsolutePath());
		// }
		run.append(" ");
		shell.add(run.toString());

		return shell;
	}

	@Override
	public List<String> getLinuxShell() {
		List<String> shell = new ArrayList<String>();
		// shell.add("/bin/sh");
		// shell.add("source /etc/profile");
		// shell.add("cd `dirname $0`");
		StringBuffer run = new StringBuffer();
		run.append("nohup ");
		if (StringUtil.isEmpty(node_home)) {
		} else {
			run.append(node_home + "/bin/");
		}

		if (!StringUtil.isEmpty(node_command)) {
			run.append(node_command.trim());
		}
		run.append(" ");

		if (starterFolder != null) {
			run.append(" >> " + new File(starterFolder, "log/starter.log").getAbsolutePath());
			run.append(" 2>&1 ");
		}
		run.append(" & ");

		shell.add(run.toString());

		File pidFile = getPIDFile();
		if (pidFile != null) {
			shell.add("echo $! > " + pidFile.getAbsolutePath());
		}
		return shell;
	}

	public String getNode_home() {
		return node_home;
	}

	public void setNode_home(String node_home) {
		this.node_home = node_home;
	}

	public String getNode_command() {
		return node_command;
	}

	public void setNode_command(String node_command) {
		this.node_command = node_command;
	}

}

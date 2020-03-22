package com.teamide.ide.processor.repository.starter.node;

import java.io.File;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.processor.repository.starter.StarterParam;
import com.teamide.ide.processor.repository.starter.StarterProcess;
import com.teamide.ide.service.impl.EnvironmentService;
import com.teamide.ide.shell.Shell;
import com.teamide.ide.shell.node.NodeShell;

public class NodeStarterProcess extends StarterProcess {

	private final String node_home;

	public NodeStarterProcess(StarterParam param) {
		super(param);
		String node_home = null;
		if (!StringUtil.isEmpty(param.option.getNodeenvironmentid())) {
			try {
				EnvironmentBean environment = new EnvironmentService().get(param.option.getNodeenvironmentid());
				if (environment != null) {
					node_home = environment.getPath();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.node_home = param.formatToRoot(node_home);
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
	public File getServer() {
		return null;
	}

	@Override
	public File getWorkFolder() {
		return this.param.projectFolder;
	}

	@Override
	public void compile() throws Exception {

	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

	public String getNodeCommand() {
		return param.option.getNodecommand();
	}

	public String getNodeHome() {
		return node_home;
	}

}

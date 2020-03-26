package com.teamide.ide.deployer.install.node;

import java.io.File;
import java.io.IOException;

import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.ide.deployer.install.DefaultInstall;
import com.teamide.ide.service.impl.EnvironmentService;

public class NodeInstall extends DefaultInstall {

	private final String node_home;

	public NodeInstall(DeployParam param) {
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
		this.node_home = param.starter.formatToRoot(node_home);

		try {
			param.starter.starterJSON.put("node_home", node_home);
			FileUtil.write(param.starter.starterJSON.toJSONString().getBytes(), param.starter.starterJSONFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public File getServer() {
		return null;
	}

	@Override
	public void compile() throws Exception {

	}

	public String getNodeCommand() {
		return param.option.getNodecommand();
	}

	public String getNodeHome() {
		return node_home;
	}

	@Override
	public void copyProject() throws Exception {
		super.copyProject();
	}

}

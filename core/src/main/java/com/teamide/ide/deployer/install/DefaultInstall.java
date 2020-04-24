package com.teamide.ide.deployer.install;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import com.teamide.util.StringUtil;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.deployer.DeployInstall;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.starter.bean.OtherOptionBean;

public class DefaultInstall extends DeployInstall {

	protected final OtherOptionBean option;

	public DefaultInstall(DeployParam param) {
		super(param);
		this.option = (OtherOptionBean) param.starter.option;
	}

	@Override
	public File getServer() {
		return null;
	}

	@Override
	public void compile() throws Exception {
		String command = option.getCompilecommand();
		if (StringUtil.isEmpty(command)) {
			return;
		}
		String[] cmd = null;
		if (IDEConstant.IS_OS_WINDOW) {
			cmd = new String[] { "cmd" };
		} else {
			cmd = new String[] { "/bin/sh" };
		}
		Process process = Runtime.getRuntime().exec(cmd);

		PrintWriter writer = new PrintWriter(process.getOutputStream());
		writer.println("cd " + this.param.codeFolder);
		writer.println(command);
		writer.flush();
		writer.close();
		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();
		this.param.starter.read(inputStream, false);
		this.param.starter.read(errorStream, true);
		process.destroy();
	}

}

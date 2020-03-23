package com.teamide.ide.deployer;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.util.FileUtil;

public abstract class Deploy extends DeployParam {

	public Deploy(File starterFolder) {
		super(starterFolder);
	}

	public abstract void remove() throws Exception;

	public abstract void destroy() throws Exception;

	public abstract void start() throws Exception;

	public abstract void stop() throws Exception;

	public abstract void cleanLog() throws Exception;

	public abstract void deploy() throws Exception;

	public void install() {
		try {
			installStarerJar();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeDeployStatus("INSTALL_STARER_ERROR");
		}
		try {
			installServer();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeDeployStatus("INSTALL_SERVER_ERROR");
		}
		try {
			compile();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeDeployStatus("COMPILE_ERROR");
		}
		try {
			installShell();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeDeployStatus("INSTALL_SHELL_ERROR");
		}

	}

	protected void installServer() throws Exception {

		this.starter.writeDeployStatus("INSTALL_SERVER");
		this.starter.getLog().info("starter install server");
		if (this.starter.starterServerFolder.exists()) {
			FileUtils.deleteDirectory(this.starter.starterServerFolder);
		}
		File server = deployInstall.getServer();
		if (server != null && server.exists()) {
			this.starter.starterServerFolder.mkdirs();
			FileUtils.copyDirectory(server, this.starter.starterServerFolder);
		}
	}

	protected void compile() throws Exception {
		this.starter.writeDeployStatus("COMPILE");
		doCompile();
	}

	protected void doCompile() throws Exception {
		deployInstall.compile();
	}

	protected void installShell() throws Exception {
		this.starter.writeDeployStatus("INSTALL_SHELL");
		installStartShell();
	}

	protected void installStartShell() throws Exception {
		this.starter.writeDeployStatus("INSTALL_SHELL");
		if (this.starter.starterStartShellFile.exists()) {
			this.starter.starterStartShellFile.delete();
		}

		this.starter.starterStartShellFile.createNewFile();
		this.starter.starterStartShellFile.setExecutable(true);
		String shell = deployInstall.getStartShell();
		if (shell == null) {
			shell = "";
		}
		this.starter.getLog().info("install start shell");
		this.starter.getLog().info(shell.toString());
		FileUtil.write(shell.toString().getBytes(), this.starter.starterStartShellFile);
	}

	protected void installStopShell() throws Exception {
		this.starter.writeDeployStatus("INSTALL_SHELL");
		if (this.starter.starterStopShellFile.exists()) {
			this.starter.starterStopShellFile.delete();
		}

		this.starter.starterStopShellFile.createNewFile();
		this.starter.starterStopShellFile.setExecutable(true);
		String shell = deployInstall.getStopShell();
		if (shell == null) {
			shell = "";
		}
		this.starter.getLog().info("install stop shell");
		this.starter.getLog().info(shell.toString());
		FileUtil.write(shell.toString().getBytes(), this.starter.starterStopShellFile);
	}

	public void installStarerJar() throws Exception {
		boolean installed = true;
		if (!this.starter.starterFolder.exists()) {
			installed = false;
		}
		if (this.starter.readTimestamp() < System.currentTimeMillis() - (1000 * 5)) {
			installed = false;
		}
		if (!installed) {
			this.starter.getLog().info("install starter start...");
			this.starter.writeDeployStatus("INSTALL_STARER");
			if (!this.starter.starterFolder.exists()) {
				this.starter.starterFolder.mkdirs();
			}
			if (this.starter.starterJarFile.exists()) {
				this.starter.starterJarFile.delete();
			}
			File jar = new File(IDEConstant.PLUGIN_STARTER_JAR);
			if (jar != null && jar.exists()) {
				FileUtils.copyFile(jar, this.starter.starterJarFile);
			} else {
				this.starter.getLog().error("plugin starter.jar does not exist.");
			}

			this.starter.getLog().info("install starter end...");
		}
	}

	public void checkStartStarter() throws Exception {
		boolean stoped = true;
		if (!this.starter.starterFolder.exists()) {
			stoped = false;
		}
		if (this.starter.readTimestamp() < System.currentTimeMillis() - (1000 * 5)) {
			stoped = false;
		}
		if (!stoped) {
			this.starter.startStarter(this.deployInstall.getWorkFolder(), this.deployInstall.getPIDFile());

		}
	}

	public abstract JSONObject getStatus() throws Exception;

	public abstract JSONObject read(int start, int end, String timestamp);
}

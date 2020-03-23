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

	public abstract void remove();

	public abstract void destroy();

	public abstract void start();

	public abstract void stop();

	public abstract void cleanLog();

	public abstract void deploy();

	public void install() {
		try {
			installStarer();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeStatus("INSTALL_STARER_ERROR");
		}
		try {
			installServer();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeStatus("INSTALL_SERVER_ERROR");
		}
		try {
			compile();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeStatus("COMPILE_ERROR");
		}
		try {
			installShell();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeStatus("INSTALL_SHELL_ERROR");
		}
	}

	protected void installServer() throws Exception {

		this.starter.writeStatus("INSTALL_SERVER");
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
		this.starter.writeStatus("COMPILE");
		doCompile();
	}

	protected void doCompile() throws Exception {
		deployInstall.compile();
	}

	protected void installShell() throws Exception {
		this.starter.writeStatus("INSTALL_SHELL");
		installStartShell();
	}

	protected void installStartShell() throws Exception {
		this.starter.writeStatus("INSTALL_SHELL");
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
		this.starter.writeStatus("INSTALL_SHELL");
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

	public void installStarer() throws Exception {
		boolean installed = true;
		if (!this.starter.starterFolder.exists()) {
			installed = false;
		}
		if (this.starter.readTimestamp() < System.currentTimeMillis() - (1000 * 5)) {
			installed = false;
		}
		if (!installed) {
			this.starter.getLog().info("install starter start...");
			this.starter.writeStatus("INSTALL_STARER");
			if (!this.starter.starterFolder.exists()) {
				this.starter.starterFolder.mkdirs();
			}
			if (this.starter.starterJarFile.exists()) {
				this.starter.starterJarFile.delete();
			}
			File jar = new File(IDEConstant.PLUGIN_STARTER_JAR);
			if (jar != null && jar.exists()) {
				FileUtils.copyFile(jar, this.starter.starterJarFile);
				this.starter.writeStatus("STARTING_STARTER");
				this.starter.startStarter(this.deployInstall.getWorkFolder(), this.deployInstall.getPIDFile());
				this.starter.writeStatus("STARTED_STARTER");
			} else {
				this.starter.getLog().error("plugin starter.jar does not exist.");
			}

			this.starter.getLog().info("install starter end...");
		}
	}

	public JSONObject getStatus() {
		return this.starter.getStarterInfo();
	}
}

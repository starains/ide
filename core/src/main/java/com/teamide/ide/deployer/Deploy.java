package com.teamide.ide.deployer;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.enums.InstallStatus;
import com.teamide.util.FileUtil;

public abstract class Deploy extends DeployParam {

	public Deploy(File starterFolder) {
		super(starterFolder);
	}

	public abstract void remove() throws Exception;

	public abstract void start() throws Exception;

	public abstract void stop() throws Exception;

	public abstract void cleanLog() throws Exception;

	public abstract void deploy() throws Exception;

	public void install() throws Exception {
		try {
			installServer();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeInstallStatus(InstallStatus.INSTALL_SERVER_ERROR);
		}
		try {
			installShell();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeInstallStatus(InstallStatus.INSTALL_SHELL_ERROR);
		}

		File workFolder = this.starter.workFolder;
		File pidFile = deployInstall.getPIDFile();

		if (workFolder != null) {
			String path = workFolder.getAbsolutePath().substring(this.starter.starterFolder.getAbsolutePath().length());
			this.starter.starterJSON.put("WORK_FOLDER", path);
		} else {
			this.starter.starterJSON.remove("WORK_FOLDER");
		}

		if (pidFile != null) {
			String path = pidFile.getAbsolutePath().substring(this.starter.starterFolder.getAbsolutePath().length());
			this.starter.starterJSON.put("PID_FILE", path);
			this.starter.starterJSON.put("BACKSTAGE", "1");

		} else {
			this.starter.starterJSON.remove("PID_FILE");
			this.starter.starterJSON.remove("BACKSTAGE");
		}
		this.starter.starterJSON.put("READYED", true);
		FileUtil.write(this.starter.starterJSON.toJSONString().getBytes(), this.starter.starterJSONFile);

	}

	protected void installServer() throws Exception {

		this.starter.writeInstallStatus(InstallStatus.INSTALL_SERVER);
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

	protected void installProject() throws Exception {
		this.starter.writeInstallStatus(InstallStatus.INSTALL_PROJECT_ING);
		doCompile();
		this.starter.writeInstallStatus(InstallStatus.INSTALL_PROJECT_ED);
	}

	protected void doCompile() throws Exception {
		deployInstall.compile();
	}

	protected void installShell() throws Exception {
		this.starter.writeInstallStatus(InstallStatus.INSTALL_SHELL);
		installStartShell();
		installStopShell();
	}

	protected void installStartShell() throws Exception {
		this.starter.writeInstallStatus(InstallStatus.INSTALL_SHELL);
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
		this.starter.writeInstallStatus(InstallStatus.INSTALL_SHELL);
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

	public abstract JSONObject getStatus() throws Exception;

	public abstract JSONObject read(int start, int end, String timestamp);
}

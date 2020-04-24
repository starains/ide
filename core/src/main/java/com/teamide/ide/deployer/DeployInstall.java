package com.teamide.ide.deployer;

import java.io.File;

public abstract class DeployInstall {

	public final DeployParam param;

	public DeployInstall(DeployParam param) {
		this.param = param;
	}

	public abstract void compile() throws Exception;

	public abstract File getServer() throws Exception;

	// public abstract File getWorkFolder() throws Exception;
}

package com.teamide.ide.deployer;

import java.io.File;

public class LocalDeploy extends Deploy {

	public LocalDeploy(File starterFolder) {
		super(starterFolder);
	}

	public void deploy() {
		this.starter.writeStatus("DEPLOYED");
	}

	public void remove() {
		this.starter.remove();
	}

	public void destroy() {
		this.starter.destroy();
	}

	public void start() {
		install();
		this.starter.start();
	}

	public void stop() {
		this.starter.stop();
	}

	public void cleanLog() {
		this.starter.cleanLog();
	}

}

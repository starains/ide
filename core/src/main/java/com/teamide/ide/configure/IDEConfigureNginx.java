package com.teamide.ide.configure;

import java.io.Serializable;

public class IDEConfigureNginx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String folder;

	private String stopcommand;

	private String startcommand;

	private String reloadcommand;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getStopcommand() {
		return stopcommand;
	}

	public void setStopcommand(String stopcommand) {
		this.stopcommand = stopcommand;
	}

	public String getStartcommand() {
		return startcommand;
	}

	public void setStartcommand(String startcommand) {
		this.startcommand = startcommand;
	}

	public String getReloadcommand() {
		return reloadcommand;
	}

	public void setReloadcommand(String reloadcommand) {
		this.reloadcommand = reloadcommand;
	}

}

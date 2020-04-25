package com.teamide.ide.configure;

import java.io.Serializable;

public class IDEConfigureAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean openregister;

	private Boolean openactivation;

	private String defaultpassword;

	public Boolean getOpenregister() {
		return openregister;
	}

	public void setOpenregister(Boolean openregister) {
		this.openregister = openregister;
	}

	public Boolean getOpenactivation() {
		return openactivation;
	}

	public void setOpenactivation(Boolean openactivation) {
		this.openactivation = openactivation;
	}

	public String getDefaultpassword() {
		return defaultpassword;
	}

	public void setDefaultpassword(String defaultpassword) {
		this.defaultpassword = defaultpassword;
	}

}

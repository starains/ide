package com.teamide.starter.bean;

public class OtherOptionBean extends StarterOptionBean {

	private String compilecommand;
	private String startcommand;
	private String stopcommand;
	private String pidfile;

	public String getCompilecommand() {
		return compilecommand;
	}

	public void setCompilecommand(String compilecommand) {
		this.compilecommand = compilecommand;
	}

	public String getStartcommand() {
		return startcommand;
	}

	public void setStartcommand(String startcommand) {
		this.startcommand = startcommand;
	}

	public String getStopcommand() {
		return stopcommand;
	}

	public void setStopcommand(String stopcommand) {
		this.stopcommand = stopcommand;
	}

	public String getPidfile() {
		return pidfile;
	}

	public void setPidfile(String pidfile) {
		this.pidfile = pidfile;
	}
}

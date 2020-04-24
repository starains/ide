package com.teamide.starter.bean;

public class StarterOptionBean {

	private String name;

	private String token;

	private String spaceid;

	private String branch;

	private String path;

	private String local;

	private String language;

	private String status;

	private String deploy_status;

	private String install_status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeploy_status() {
		return deploy_status;
	}

	public void setDeploy_status(String deploy_status) {
		this.deploy_status = deploy_status;
	}

	public String getInstall_status() {
		return install_status;
	}

	public void setInstall_status(String install_status) {
		this.install_status = install_status;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSpaceid() {
		return spaceid;
	}

	public void setSpaceid(String spaceid) {
		this.spaceid = spaceid;
	}
}

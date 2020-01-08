package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.RUNNER_SERVER)
public class RunnerServerBean extends BaseBean {

	@Column(name = "name")
	private String name;

	@Column(name = "server")
	private String server;

	@Column(name = "token")
	private String token;

	@Column(name = "userid")
	private String userid;

	@Column(name = "lastconnectiontime")
	private String lastconnectiontime;

	@Column(name = "version")
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLastconnectiontime() {
		return lastconnectiontime;
	}

	public void setLastconnectiontime(String lastconnectiontime) {
		this.lastconnectiontime = lastconnectiontime;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}

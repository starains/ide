package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;
import com.teamide.ide.enums.DeployServerStatus;

@Table(name = TableInfoConstant.REMOTE)
public class RemoteBean extends BaseBean {

	@Column(name = "name")
	private String name;

	@Column(name = "token")
	private String token;

	@Column(name = "mode")
	private String mode;

	@Column(name = "timestamp")
	private String timestamp;

	@Column(name = "server")
	private String server;

	@Column(name = "version")
	private String version;

	private DeployServerStatus status;

	private String errmsg;

	public DeployServerStatus getStatus() {
		return status;
	}

	public void setStatus(DeployServerStatus status) {
		this.status = status;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}

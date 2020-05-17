package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.ENVIRONMENT_INFO)
public class EnvironmentBean extends BaseBean {

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "type", length = 10)
	private String type;

	@Column(name = "version", length = 100)
	private String version;

	@Column(name = "path", length = 200)
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

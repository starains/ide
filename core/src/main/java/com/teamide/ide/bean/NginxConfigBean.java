package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.NGINX_CONFIG_INFO)
public class NginxConfigBean extends BaseBean {

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "type", length = 100)
	private String type;

	@Column(name = "content", length = 3000)
	private String content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

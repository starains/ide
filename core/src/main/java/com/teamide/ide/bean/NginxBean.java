package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.NGINX_INFO)
public class NginxBean extends BaseBean {

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "domainprefix", length = 100)
	private String domainprefix;

	@Column(name = "userid", length = 20)
	private String userid;

	@Column(name = "type", length = 100)
	private String type;

	@Column(name = "parentid", length = 20)
	private String parentid;

	@Column(name = "option", length = 1000)
	private String option;

	@Column(name = "content", length = 3000)
	private String content;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getDomainprefix() {
		return domainprefix;
	}

	public void setDomainprefix(String domainprefix) {
		this.domainprefix = domainprefix;
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

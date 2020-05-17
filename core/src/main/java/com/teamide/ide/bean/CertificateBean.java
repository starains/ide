package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.TableInfoConstant;
import com.teamide.util.StringUtil;

@Table(name = TableInfoConstant.CERTIFICATE_INFO)
public class CertificateBean extends BaseBean {

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "username", length = 200)
	private String username;

	@Column(name = "password", length = 200)
	private String password;

	@Column(name = "userid", length = 20)
	private String userid;

	@Column(name = "type", length = 10)
	private String type;

	@Column(name = "option", length = 1000)
	private String option;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public JSONObject getJSONOption() {
		if (StringUtil.isEmpty(this.option)) {
			return new JSONObject();
		}
		return JSONObject.parseObject(this.option);
	}

	public void setJSONOption(JSONObject json) {
		if (json == null || json.size() == 0) {
			this.option = new JSONObject().toJSONString();
		} else {
			this.option = json.toJSONString();
		}
	}

}

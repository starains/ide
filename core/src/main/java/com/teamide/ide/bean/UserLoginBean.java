package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.USER_LOGIN)
public class UserLoginBean extends BaseBean {

	@Column(name = "token", length = 200)
	private String token;

	@Column(name = "type", length = 100)
	private String type;

	@Column(name = "starttime", length = 20)
	private String starttime;

	@Column(name = "endtime", length = 20)
	private String endtime;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

}

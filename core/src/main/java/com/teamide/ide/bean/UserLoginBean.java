package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.USER_LOGIN)
public class UserLoginBean extends BaseBean {

	@Column(name = "userid", length = 20)
	private String userid;

	@Column(name = "username", length = 100)
	private String username;

	@Column(name = "loginname", length = 100)
	private String loginname;

	@Column(name = "token", length = 200)
	private String token;

	@Column(name = "type", length = 100)
	private String type;

	@Column(name = "starttime", length = 20)
	private String starttime;

	@Column(name = "endtime", length = 20)
	private String endtime;

	@Column(name = "ip", length = 100)
	private String ip;

	@Column(name = "province", length = 100)
	private String province;

	@Column(name = "city", length = 100)
	private String city;

	@Column(name = "district", length = 100)
	private String district;

	@Column(name = "street", length = 200)
	private String street;

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

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

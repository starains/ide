package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.USER_INFO)
public class UserBean extends BaseBean {

	@Column(name = "name")
	private String name;

	@Column(name = "loginname")
	private String loginname;

	@Column(name = "photo")
	private String photo;

	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "spaceid")
	private String spaceid;

	@Column(name = "status")
	private Integer status;

	public String getSpaceid() {
		return spaceid;
	}

	public void setSpaceid(String spaceid) {
		this.spaceid = spaceid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginname() {

		return loginname;
	}

	public void setLoginname(String loginname) {

		this.loginname = loginname;
	}

	public String getPhoto() {

		return photo;
	}

	public void setPhoto(String photo) {

		this.photo = photo;
	}

	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public Integer getStatus() {

		return status;
	}

	public void setStatus(Integer status) {

		this.status = status;
	}

}

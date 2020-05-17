package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.USER_INFO)
public class UserBean extends BaseBean {

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "loginname", length = 100)
	private String loginname;

	@Column(name = "photo", length = 20)
	private String photo;

	@Column(name = "phone", length = 200)
	private String phone;

	@Column(name = "email", length = 200)
	private String email;

	@Column(name = "password", length = 200)
	private String password;

	@Column(name = "spaceid", length = 20)
	private String spaceid;

	@Column(name = "status", length = 10)
	private Integer status;

	@Column(name = "disabletime", length = 20)
	private String disabletime;

	@Column(name = "enabletime", length = 20)
	private String enabletime;

	@Column(name = "locktime", length = 20)
	private String locktime;

	@Column(name = "unlocktime", length = 20)
	private String unlocktime;

	@Column(name = "activestatus", length = 10)
	private Integer activestatus;

	@Column(name = "activetime", length = 20)
	private String activetime;

	public String getDisabletime() {
		return disabletime;
	}

	public void setDisabletime(String disabletime) {
		this.disabletime = disabletime;
	}

	public String getEnabletime() {
		return enabletime;
	}

	public void setEnabletime(String enabletime) {
		this.enabletime = enabletime;
	}

	public String getUnlocktime() {
		return unlocktime;
	}

	public void setUnlocktime(String unlocktime) {
		this.unlocktime = unlocktime;
	}

	public String getActivetime() {
		return activetime;
	}

	public void setActivetime(String activetime) {
		this.activetime = activetime;
	}

	public String getLocktime() {
		return locktime;
	}

	public void setLocktime(String locktime) {
		this.locktime = locktime;
	}

	public Integer getActivestatus() {
		return activestatus;
	}

	public void setActivestatus(Integer activestatus) {
		this.activestatus = activestatus;
	}

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

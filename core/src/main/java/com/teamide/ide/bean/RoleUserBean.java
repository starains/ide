package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.ROLE_USER)
public class RoleUserBean extends BaseBean {

	@Column(name = "roleid", length = 20)
	private String roleid;

	@Column(name = "userid", length = 20)
	private String userid;

	@Column(name = "status", length = 10)
	private Integer status;

	@Column(name = "sequence", length = 10)
	private Integer sequence;

	public Integer getStatus() {

		return status;
	}

	public void setStatus(Integer status) {

		this.status = status;
	}

	public Integer getSequence() {

		return sequence;
	}

	public void setSequence(Integer sequence) {

		this.sequence = sequence;
	}

	public String getRoleid() {

		return roleid;
	}

	public void setRoleid(String roleid) {

		this.roleid = roleid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}

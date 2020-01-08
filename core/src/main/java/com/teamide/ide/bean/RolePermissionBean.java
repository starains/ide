package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.ROLE_PERMISSION)
public class RolePermissionBean extends BaseBean {

	@Column(name = "roleid")
	private String roleid;

	@Column(name = "permissionid")
	private String permissionid;

	@Column(name = "status")
	private Integer status;

	@Column(name = "sequence")
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

	public String getPermissionid() {

		return permissionid;
	}

	public void setPermissionid(String permissionid) {

		this.permissionid = permissionid;
	}

}

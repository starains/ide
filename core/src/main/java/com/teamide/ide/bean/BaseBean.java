package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Id;

public class BaseBean {

	@Id
	@Column(name = "id", length = 20)
	private String id;

	@Column(name = "createuserid", length = 20)
	private String createuserid;

	@Column(name = "createtime", length = 20)
	private String createtime;

	@Column(name = "updateuserid", length = 20)
	private String updateuserid;

	@Column(name = "updatetime", length = 20)
	private String updatetime;

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getCreatetime() {

		return createtime;
	}

	public void setCreatetime(String createtime) {

		this.createtime = createtime;
	}

	public String getUpdatetime() {

		return updatetime;
	}

	public void setUpdatetime(String updatetime) {

		this.updatetime = updatetime;
	}

	public String getCreateuserid() {
		return createuserid;
	}

	public void setCreateuserid(String createuserid) {
		this.createuserid = createuserid;
	}

	public String getUpdateuserid() {
		return updateuserid;
	}

	public void setUpdateuserid(String updateuserid) {
		this.updateuserid = updateuserid;
	}

}

package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.ROLE_INFO)
public class RoleBean extends BaseBean {

	@Column(name = "name")
	private String name;

	@Column(name = "forsuper")
	private Boolean forsuper;

	@Column(name = "fordefault")
	private Boolean fordefault;

	@Column(name = "status")
	private Integer status;

	@Column(name = "sequence")
	private Integer sequence;

	public Integer getSequence() {

		return sequence;
	}

	public void setSequence(Integer sequence) {

		this.sequence = sequence;
	}

	public Boolean isForsuper() {

		return forsuper;
	}

	public void setForsuper(Boolean forsuper) {

		this.forsuper = forsuper;
	}

	public Boolean isFordefault() {

		return fordefault;
	}

	public void setFordefault(Boolean fordefault) {

		this.fordefault = fordefault;
	}

	public Integer getStatus() {

		return status;
	}

	public void setStatus(Integer status) {

		this.status = status;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}

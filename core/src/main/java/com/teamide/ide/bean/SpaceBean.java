package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.SPACE)
public class SpaceBean extends BaseBean {

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "type", length = 100)
	private String type;

	@Column(name = "parentid", length = 20)
	private String parentid;

	@Column(name = "publictype", length = 100)
	private String publictype;

	@Column(name = "comment", length = 1000)
	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPublictype() {
		return publictype;
	}

	public void setPublictype(String publictype) {
		this.publictype = publictype;
	}

	public String getParentid() {

		return parentid;
	}

	public void setParentid(String parentid) {

		this.parentid = parentid;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}

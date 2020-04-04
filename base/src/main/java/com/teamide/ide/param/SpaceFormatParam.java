package com.teamide.ide.param;

import com.teamide.ide.enums.SpacePermission;

public class SpaceFormatParam {

	private String id;

	private String name;

	private String root;

	private String servletpath;

	private SpacePermission permission;

	private String type;

	private String parentid;

	private String publictype;

	private String comment;

	private String createuserid;

	private String createtime;

	private String updateuserid;

	private String updatetime;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getServletpath() {
		return servletpath;
	}

	public void setServletpath(String servletpath) {
		this.servletpath = servletpath;
	}

	public SpacePermission getPermission() {
		return permission;
	}

	public void setPermission(SpacePermission permission) {
		this.permission = permission;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getPublictype() {
		return publictype;
	}

	public void setPublictype(String publictype) {
		this.publictype = publictype;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

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

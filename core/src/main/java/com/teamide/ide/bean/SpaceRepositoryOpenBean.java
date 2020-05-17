package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.SPACE_REPOSITORY_OPEN)
public class SpaceRepositoryOpenBean extends BaseBean {

	@Column(name = "spaceid", length = 20)
	private String spaceid;

	@Column(name = "branch", length = 200)
	private String branch;

	@Column(name = "path", length = 300)
	private String path;

	@Column(name = "userid", length = 20)
	private String userid;

	@Column(name = "opentime", length = 20)
	private Long opentime;

	public String getSpaceid() {
		return spaceid;
	}

	public void setSpaceid(String spaceid) {
		this.spaceid = spaceid;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Long getOpentime() {
		return opentime;
	}

	public void setOpentime(Long opentime) {
		this.opentime = opentime;
	}

}

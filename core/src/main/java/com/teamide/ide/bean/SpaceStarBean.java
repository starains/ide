package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.SPACE_STAR)
public class SpaceStarBean extends BaseBean {

	@Column(name = "spaceid", length = 20)
	private String spaceid;

	@Column(name = "userid", length = 20)
	private String userid;

	public String getSpaceid() {
		return spaceid;
	}

	public void setSpaceid(String spaceid) {
		this.spaceid = spaceid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}

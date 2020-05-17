package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.SPACE_TEAM)
public class SpaceTeamBean extends BaseBean {

	@Column(name = "spaceid", length = 20)
	private String spaceid;

	@Column(name = "type", length = 100)
	private String type;

	@Column(name = "recordid", length = 20)
	private String recordid;

	@Column(name = "permission", length = 100)
	private String permission;

	public String getSpaceid() {
		return spaceid;
	}

	public void setSpaceid(String spaceid) {
		this.spaceid = spaceid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRecordid() {
		return recordid;
	}

	public void setRecordid(String recordid) {
		this.recordid = recordid;
	}

	public String getPermission() {

		return permission;
	}

	public void setPermission(String permission) {

		this.permission = permission;
	}

}

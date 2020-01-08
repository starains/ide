package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.TableInfoConstant;
import com.teamide.util.StringUtil;

@Table(name = TableInfoConstant.SPACE_REPOSITORY_OPTION)
public class SpaceRepositoryOptionBean extends BaseBean {

	@Column(name = "name")
	private String name;

	@Column(name = "spaceid")
	private String spaceid;

	@Column(name = "branch")
	private String branch;

	@Column(name = "type")
	private String type;

	@Column(name = "userid")
	private String userid;

	@Column(name = "path")
	private String path;

	@Column(name = "option", length = 5000)
	private String option;

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public JSONObject getJSONOption() {
		if (StringUtil.isEmpty(this.option)) {
			return new JSONObject();
		}
		return JSONObject.parseObject(this.option);
	}

	public void setJSONOption(JSONObject json) {
		if (json == null || json.size() == 0) {
			this.option = new JSONObject().toJSONString();
		} else {
			this.option = json.toJSONString();
		}
	}

}

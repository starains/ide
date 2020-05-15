package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.TableInfoConstant;
import com.teamide.util.StringUtil;

@Table(name = TableInfoConstant.DATABASE_INFO)
public class DatabaseBean extends BaseBean {

	@Column(name = "name")
	private String name;

	@Column(name = "userid")
	private String userid;

	@Column(name = "type")
	private String type;

	@Column(name = "option", length = 5000)
	private String option;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

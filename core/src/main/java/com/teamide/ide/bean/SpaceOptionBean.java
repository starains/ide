package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.SPACE_OPTION)
public class SpaceOptionBean extends BaseBean {

	@Column(name = "option", length = 5000)
	private String option;

	private JSONObject optionJSON = new JSONObject();

	public SpaceOptionBean set(String key, Object value) {
		optionJSON.put(key, value);
		option = optionJSON.toJSONString();
		return this;
	}

	public SpaceOptionBean set(JSONObject optionJSON) {
		if (optionJSON == null) {
			optionJSON = new JSONObject();
		}
		option = optionJSON.toJSONString();
		return this;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public JSONObject getOptionJSON() {
		return optionJSON;
	}

	public void setOptionJSON(JSONObject optionJSON) {
		this.optionJSON = optionJSON;
	}

}

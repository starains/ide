package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.SPACE_EVENT)
public class SpaceEventBean extends BaseBean {

	@Column(name = "spaceid")
	private String spaceid;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "data", length = 5000)
	private String data;

	private JSONObject dataJSON = new JSONObject();

	public SpaceEventBean set(String key, Object value) {
		dataJSON.put(key, value);
		data = dataJSON.toJSONString();
		return this;
	}

	public SpaceEventBean set(JSONObject dataJSON) {
		if (dataJSON == null) {
			dataJSON = new JSONObject();
		}
		data = dataJSON.toJSONString();
		return this;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}

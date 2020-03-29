package com.teamide.ide.plugin;

import java.util.List;

import com.teamide.bean.OptionBean;

public class IDEInput {

	public String name;

	public String defaultvalue;

	public String label;

	public String type;

	public boolean required;

	public List<OptionBean> options;

	public IDEInput() {

	}

	public IDEInput(String label, String name) {
		this.label = label;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<OptionBean> getOptions() {
		return options;
	}

	public void setOptions(List<OptionBean> options) {
		this.options = options;
	}

}

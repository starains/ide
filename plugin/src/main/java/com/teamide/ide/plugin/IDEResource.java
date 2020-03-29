package com.teamide.ide.plugin;

public class IDEResource {

	public IDEResourceType type;

	public String name;

	public IDEResource() {

	}

	public IDEResource(IDEResourceType type, String name) {
		this.type = type;
		this.name = name;
	}

	public IDEResourceType getType() {
		return type;
	}

	public void setType(IDEResourceType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

package com.teamide.ide.plugin;

public class IDEResource {

	public IDEResourceType type;

	public String path;

	public IDEResourceType getType() {
		return type;
	}

	public void setType(IDEResourceType type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

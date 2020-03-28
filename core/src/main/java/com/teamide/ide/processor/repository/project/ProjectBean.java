package com.teamide.ide.processor.repository.project;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;

public class ProjectBean {

	private String name;
	private String path;
	private boolean isRoot;
	private boolean isMaven;
	private String packaging;
	private List<FileBean> files = new ArrayList<FileBean>();

	private AppBean app;

	private final JSONObject attribute = new JSONObject();

	public void setAttribute(String key, JSONObject value) {
		attribute.put(key, value);
	}

	public AppBean getApp() {
		return app;
	}

	public void setApp(AppBean app) {
		this.app = app;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		if (StringUtil.isEmpty(packaging)) {
			packaging = "jar";
		}
		this.packaging = packaging;
	}

	public boolean isMaven() {
		return isMaven;
	}

	public void setMaven(boolean isMaven) {
		this.isMaven = isMaven;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public List<FileBean> getFiles() {
		return files;
	}

	public void setFiles(List<FileBean> files) {
		this.files = files;
	}

}

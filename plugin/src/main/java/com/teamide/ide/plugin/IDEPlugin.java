package com.teamide.ide.plugin;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface IDEPlugin {

	public String getName();

	public String getVersion();

	public String getOptionType();

	public String getProjectAttributeName();

	public String getFileAttributeName();

	public List<IDEResource> getResources();

	public List<IDEContextmenu> getContextmenus();

	public IDEListener getListener();

	public JSONObject getEnumMap();

	public IDESpaceListener getSpaceListener();

	public IDERepositoryListener getRepositoryListener();

	public IDERepositoryProjectListener getRepositoryProjectListener();

}

package com.teamide.ide.plugin;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public interface IDERepositoryProjectListener {

	public JSONObject onLoad(PluginParam param, String path);

	public void onLoadFile(PluginParam param, File file);

	public void onCreateFile(PluginParam param, File file);

	public void onCreateFolder(PluginParam param, File folder);

	public void onUpdateFile(PluginParam param, File file);

	public void onDeleteFile(PluginParam param, File file);

	public void onDeleteFolder(PluginParam param, File folder);

	public void onRenameFile(PluginParam param, File oldFile, File newFile);

	public void onRenameFolder(PluginParam param, File oldFolder, File newFolder);

	public void onMoveFile(PluginParam param, File oldFile, File newFile);

	public void onMoveFolder(PluginParam param, File oldFolder, File newFolder);

	public void onMenuClick(PluginParam param, File folder);
}

package com.teamide.ide.plugin;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public interface IDERepositoryProjectListener extends IDEListener {

	public void onLoad(JSONObject data, File folder);

	public void onLoadFile(JSONObject data, File file);

	public void onCreateFile(JSONObject data, File file);

	public void onCreateFolder(JSONObject data, File folder);

	public void onUpdateFile(JSONObject data, File file);

	public void onDeleteFile(JSONObject data, File file);

	public void onDeleteFolder(JSONObject data, File folder);

	public void onRenameFile(JSONObject data, File oldFile, File newFile);

	public void onRenameFolder(JSONObject data, File oldFolder, File newFolder);

	public void onMoveFile(JSONObject data, File oldFile, File newFile);

	public void onMoveFolder(JSONObject data, File oldFolder, File newFolder);

	public void onMenuClick(JSONObject data, File folder);
}

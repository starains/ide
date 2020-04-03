package com.teamide.app.plugin;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.enums.BeanModelType;
import com.teamide.ide.plugin.IDERepositoryProjectListener;
import com.teamide.ide.plugin.PluginParam;
import com.teamide.util.StringUtil;

public class AppRepositoryProjectListener implements IDERepositoryProjectListener {

	@Override
	public JSONObject onLoad(PluginParam param, String path) {
		ProjectAppLoader loader = new ProjectAppLoader(param.getParam().getSourceFolder());
		try {
			AppBean app = loader.loadApp(param.getParam().getProjectPath(), param.getOption());
			if (app != null) {
				return (JSONObject) JSON.toJSON(app);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onLoadFile(PluginParam param, File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateFile(PluginParam param, File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateFolder(PluginParam param, File folder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateFile(PluginParam param, File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteFile(PluginParam param, File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteFolder(PluginParam param, File folder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRenameFile(PluginParam param, File oldFile, File newFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRenameFolder(PluginParam param, File oldFolder, File newFolder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMoveFile(PluginParam param, File oldFile, File newFile) {
		BeanModelType type = getModelType(param, oldFile);
		if (type == null) {
			return;
		}
		File appFolder = getAppFolder(param);
		File modelFolder = new File(appFolder, type.getName());
		try {
			new FileMoveService().modelRename(appFolder, modelFolder, type, oldFile, newFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onMoveFolder(PluginParam param, File oldFolder, File newFolder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuClick(PluginParam param, File folder) {
		// TODO Auto-generated method stub

	}

	public File getAppFolder(PluginParam param) {
		if (!findModel(param)) {
			return null;
		}
		AppOption option = getOption(param);
		File projectFolder = param.getParam().getFile(param.getParam().getProjectPath());
		File appFolder = new File(projectFolder, option.getModelpath());
		return appFolder;
	}

	public BeanModelType getModelType(PluginParam param, File file) {
		if (!findModel(param)) {
			return null;
		}

		File appFolder = getAppFolder(param);

		String path = file.toURI().getPath();
		for (BeanModelType one : BeanModelType.values()) {
			if (one.isDirectory()) {
				if (path.startsWith(new File(appFolder, one.getName()).toURI().getPath())) {
					return one;
				}
			} else {
				if (path.equals(new File(appFolder, one.getName()).toURI().getPath())) {
					return one;
				}
			}
		}

		return null;
	}

	public boolean findModel(PluginParam param) {
		AppOption option = getOption(param);
		if (option == null || StringUtil.isEmpty(option.getModelpath())) {
			return false;
		}
		return true;
	}

	public AppOption getOption(PluginParam param) {
		if (param == null || param.getOption() == null) {
			return null;
		}
		AppOption option = param.getOption().toJavaObject(AppOption.class);
		return option;
	}
}

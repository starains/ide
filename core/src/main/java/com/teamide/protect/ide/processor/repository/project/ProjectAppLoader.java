package com.teamide.protect.ide.processor.repository.project;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.Bean;
import com.teamide.app.enums.BeanModelType;
import com.teamide.app.source.FileSource;
import com.teamide.app.source.Source;
import com.teamide.util.StringUtil;
import com.teamide.protect.ide.bean.AppOption;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;

public class ProjectAppLoader {

	protected final RepositoryProcessorParam param;

	public ProjectAppLoader(RepositoryProcessorParam param) {
		this.param = param;
	}

	public AppBean loadApp(final String path) throws Exception {
		JSONObject option = param.getAppOption(path);
		if (option == null) {
			return null;
		}

		File projectFolder = param.getFile(path);
		AppOption appOption = option.toJavaObject(AppOption.class);
		return loadApp(projectFolder, appOption);

	}

	public AppBean loadApp(final File projectFolder, AppOption option) {
		if (option == null || StringUtil.isEmpty(option.getModelpath())) {
			return null;
		}
		File appFolder = new File(projectFolder, option.getModelpath());
		AppBean app = new AppBean();
		app.setOption(option);

		String appPath = this.param.getPath(appFolder);
		app.setPath(appPath);
		app.setLocalpath(appFolder.toURI().getPath());

		JSONObject path_model_type = new JSONObject();

		for (BeanModelType type : BeanModelType.values()) {
			String model_path = BeanModelType.getPath(type.getBeanClass(), null);
			JSONObject model_type = new JSONObject();
			model_type.put("value", type.getValue());
			model_type.put("text", type.getText());
			model_type.put("isDirectory", type.isDirectory());
			model_path = this.param.getPath(new File(appFolder, model_path));
			path_model_type.put(model_path, model_type);
		}
		app.setPath_model_type(path_model_type);
		loadAppContext(app);
		return app;
	}

	public void loadAppContext(AppBean app) {
		if (app == null || StringUtil.isEmpty(app.getLocalpath())) {
			return;
		}
		Source source = new FileSource(app.getLocalpath());
		JSONObject path_model_bean = new JSONObject();
		app.setPath_model_bean(path_model_bean);
		AppContext appContext = source.load();
		if (appContext != null) {
			for (BeanModelType type : BeanModelType.values()) {
				List<?> beans = appContext.get(type.getBeanClass());

				if (beans != null) {
					for (Object obj : beans) {
						Bean bean = (Bean) obj;
						if (!StringUtil.isEmpty(bean.getLocalfilepath())) {
							String p = param.getPath(new File(bean.getLocalfilepath()));
							path_model_bean.put(p, JSON.toJSON(bean));
						}

					}
				}
				if (type.isDirectory()) {
					if (beans != null) {
						app.setContext(type.getValue(), JSON.toJSON(beans));
					} else {
						app.setContext(type.getValue(), new JSONArray());
					}
				} else {
					if (beans != null && beans.size() > 0) {
						app.setContext(type.getValue(), JSON.toJSON(beans.get(0)));
					} else {
						app.setContext(type.getValue(), null);
					}
				}
			}
		}
	}

}

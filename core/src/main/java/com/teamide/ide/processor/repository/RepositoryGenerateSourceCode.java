package com.teamide.ide.processor.repository;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.plugin.AppBean;
import com.teamide.app.plugin.ProjectAppLoader;
import com.teamide.app.source.FileSource;
import com.teamide.app.source.Source;
import com.teamide.ide.generater.AppGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;

public class RepositoryGenerateSourceCode extends RepositoryBase {

	public RepositoryGenerateSourceCode(RepositoryProcessorParam param) {

		super(param);
	}

	public void generate(final String path) throws Exception {
		param.getLog().info("generate source code path:" + path);

		JSONObject option = param.getAppOption(path);

		AppBean app = new ProjectAppLoader(param.getSourceFolder()).loadApp(path, option);
		param.getLog().info("generate source code app:" + app);
		if (app == null) {
			throw new Exception("path[" + path + "] app is null.");
		}
		param.getLog().info("generate source code app option:" + app.getOption());

		Source source = new FileSource(app.getLocalpath());
		AppGenerater generater = new AppGenerater(param.getSourceFolder(), app, source.load());
		generater.generate();
	}

}

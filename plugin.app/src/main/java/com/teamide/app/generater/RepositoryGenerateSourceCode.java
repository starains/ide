package com.teamide.app.generater;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.generater.AppGenerater;
import com.teamide.app.plugin.AppBean;
import com.teamide.app.plugin.ProjectAppLoader;
import com.teamide.app.source.FileSource;
import com.teamide.app.source.Source;

public class RepositoryGenerateSourceCode {

	protected final File sourceFolder;

	public RepositoryGenerateSourceCode(File sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public void generate(final String path, JSONObject option) throws Exception {

		AppBean app = new ProjectAppLoader(sourceFolder).loadApp(path, option);
		if (app == null) {
			throw new Exception("path[" + path + "] app is null.");
		}

		Source source = new FileSource(app.getLocalpath());
		AppGenerater generater = new AppGenerater(sourceFolder, app, source.load());
		generater.generate();
	}

}

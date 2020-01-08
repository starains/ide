package com.teamide.protect.ide.processor.repository;

import com.teamide.app.source.FileSource;
import com.teamide.app.source.Source;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.generater.AppGenerater;
import com.teamide.protect.ide.processor.repository.project.AppBean;
import com.teamide.protect.ide.processor.repository.project.ProjectAppLoader;

public class RepositoryGenerateSourceCode extends RepositoryBase {

	public RepositoryGenerateSourceCode(RepositoryProcessorParam param) {

		super(param);
	}

	public void generate(final String path) throws Exception {
		param.getLog().info("generate source code path:" + path);
		AppBean app = new ProjectAppLoader(param).loadApp(path);
		param.getLog().info("generate source code app:" + app);
		if (app == null) {
			throw new Exception("path[" + path + "] app is null.");
		}
		param.getLog().info("generate source code app option:" + app.getOption());

		Source source = new FileSource(app.getLocalpath());
		AppGenerater generater = new AppGenerater(param, app, source.load());
		generater.generate();
	}

}

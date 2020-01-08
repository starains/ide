package com.teamide.protect.ide.processor.repository.generater;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.util.StringUtil;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.project.AppBean;

public abstract class Generater {

	protected final AppBean app;

	protected final AppContext context;

	protected final RepositoryProcessorParam param;

	public Generater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		this.param = param;
		this.app = app;
		this.context = context;
	}

	public abstract void generate() throws Exception;

	public File getJavaFolder() {
		return param.getFile(app.getOption().getJavapath());
	}

	public String getBasePackage() {
		return app.getOption().getBasepackage();
	}

	public String packageToPath(String pack) {
		if (StringUtil.isEmpty(pack)) {
			return pack;
		}
		return pack.replaceAll("\\.", "/");
	}

}

package com.teamide.ide.protect.processor.repository.generater;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

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

	public File getResourceFolder() {
		return param.getFile(app.getOption().getResourcepath());
	}

	public String getAppFactoryPackage() {
		String pack = app.getOption().getFactorypackage();
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".factory";
		}
		return pack;
	}

	public String getJdbcPath() {
		String path = app.getOption().getJdbcpath();
		if (StringUtil.isEmpty(path)) {
			path = "jdbc.properties";
		}
		return path;
	}

	public String getJdbcDirectoryPath() {
		String path = app.getOption().getJdbcdirectorypath();
		if (StringUtil.isEmpty(path)) {
			path = "jdbcs";
		}
		return path;
	}

	public String getAppFactoryClassname() {
		return "AppFactory";
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

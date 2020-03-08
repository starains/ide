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
		String javadirectory = null;
		if (context.getJava() != null) {
			javadirectory = context.getJava().getJavadirectory();
		}
		if (StringUtil.isEmpty(javadirectory)) {
			javadirectory = "src/main/java";
		}

		return param.getFile(javadirectory);
	}

	public File getResourceFolder() {
		String resourcesdirectory = null;
		if (context.getJava() != null) {
			resourcesdirectory = context.getJava().getResourcesdirectory();
		}
		if (StringUtil.isEmpty(resourcesdirectory)) {
			resourcesdirectory = "src/main/resources";
		}

		return param.getFile(resourcesdirectory);
	}

	public String getFactoryPackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getFactorypackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".factory";
		}
		return pack;
	}

	public String getComponentPackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getComponentpackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".component";
		}
		return pack;
	}

	public String getControllerPackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getControllerpackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".controller";
		}
		return pack;
	}

	public String getDaoPackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getDaopackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".dao";
		}
		return pack;
	}

	public String getServicePackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getServicepackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".service";
		}
		return pack;
	}

	public String getDictionaryPackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getDictionarypackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".dictionary";
		}
		return pack;
	}

	public String getBeanPackage() {
		String pack = null;
		if (context.getJava() != null) {
			pack = context.getJava().getBeanpackage();
		}
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".bean";
		}
		return pack;
	}

	public String getAppFactoryClassname() {
		return "AppFactory";
	}

	public String getDaoComponentClassname() {
		return "DaoComponent";
	}

	public String getTransactionComponentClassname() {
		return "TransactionComponent";
	}

	public String getBasePackage() {
		String basepackage = null;
		if (context.getJava() != null) {
			basepackage = context.getJava().getBasepackage();
		}
		if (StringUtil.isEmpty(basepackage)) {
			basepackage = "com.teamide.app";
		}

		return basepackage;
	}

	public String packageToPath(String pack) {
		if (StringUtil.isEmpty(pack)) {
			return pack;
		}
		return pack.replaceAll("\\.", "/");
	}

}

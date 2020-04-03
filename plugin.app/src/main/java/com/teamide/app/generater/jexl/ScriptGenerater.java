package com.teamide.app.generater.jexl;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;

public class ScriptGenerater extends BaseGenerater {

	public final String scriptName;

	public final String propertyname;

	public ScriptGenerater(File sourceFolder, AppBean app, AppContext context, String scriptName, String propertyname) {
		super(null, sourceFolder, app, context);
		this.scriptName = scriptName;
		this.propertyname = propertyname;
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		return getJexlScriptPackage();
	}

	@Override
	public String getPropertyname() {
		return propertyname;
	}

	@Override
	public void buildData() {
	}

	public String getClassName() {
		return scriptName;
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/jexl/script/" + scriptName;
	}

}

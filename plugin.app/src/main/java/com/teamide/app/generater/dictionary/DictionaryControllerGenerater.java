package com.teamide.app.generater.dictionary;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;

public class DictionaryControllerGenerater extends BaseGenerater {

	protected final DictionaryBean dictionary;

	public DictionaryControllerGenerater(DictionaryBean dictionary, File sourceFolder, AppBean app,
			AppContext context) {
		super(dictionary, sourceFolder, app, context);
		this.dictionary = dictionary;
	}

	public String getPackage() {
		String pack = getControllerPackage();
		pack += ".dictionary";
		return pack;
	}

	public String getClassName() {
		return super.getClassName() + "Controller";
	}

	@Override
	public void buildData() {
		data.put("$method_name", "invoke");

		DictionaryGenerater dictionaryGenerater = new DictionaryGenerater(dictionary, sourceFolder, app, context);
		dictionaryGenerater.init();
		data.put("$dictionary", dictionaryGenerater.data);
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/controller/dictionary";
	}

}

package com.teamide.ide.protect.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public class DictionaryControllerGenerater extends CodeGenerater {

	protected final DictionaryBean dictionary;

	public DictionaryControllerGenerater(DictionaryBean dictionary, RepositoryProcessorParam param, AppBean app,
			AppContext context) {
		super(dictionary, param, app, context);
		this.dictionary = dictionary;
	}

	public String getPackage() {
		String pack = app.getOption().getControllerpackage();
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".controller";
		}
		pack += ".dictionary";
		return pack;
	}

	public String getClassName() {
		return super.getClassName() + "Controller";
	}

	@Override
	public void buildData() {

		DictionaryGenerater dictionaryGenerater = new DictionaryGenerater(dictionary, param, app, context);
		dictionaryGenerater.init();
		data.put("$dictionary", dictionaryGenerater.data);
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/controller/dictionary";
	}

}

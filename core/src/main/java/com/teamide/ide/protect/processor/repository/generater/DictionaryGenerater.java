package com.teamide.ide.protect.processor.repository.generater;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.bean.DictionaryOptionBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class DictionaryGenerater extends CodeGenerater {

	protected final DictionaryBean dictionary;

	public DictionaryGenerater(DictionaryBean dictionary, RepositoryProcessorParam param, AppBean app,
			AppContext context) {
		super(dictionary, param, app, context);
		this.dictionary = dictionary;
	}

	public String getPackage() {
		return getDictionaryPackage();
	}

	@Override
	public void buildData() {
		JSONArray $options = new JSONArray();
		List<DictionaryOptionBean> options = dictionary.getOptions();

		for (DictionaryOptionBean option : options) {
			JSONObject $option = (JSONObject) JSONObject.toJSON(option);
			$options.add($option);
		}

		data.put("$options", $options);

		data.put("$dictionary", JSON.toJSON(dictionary));
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/dictionary";
	}

}

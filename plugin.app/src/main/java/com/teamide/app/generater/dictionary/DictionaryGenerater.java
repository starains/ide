package com.teamide.app.generater.dictionary;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.bean.DictionaryOptionBean;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;

public class DictionaryGenerater extends BaseGenerater {

	protected final DictionaryBean dictionary;

	public DictionaryGenerater(DictionaryBean dictionary, File sourceFolder, AppBean app, AppContext context) {
		super(dictionary, sourceFolder, app, context);
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

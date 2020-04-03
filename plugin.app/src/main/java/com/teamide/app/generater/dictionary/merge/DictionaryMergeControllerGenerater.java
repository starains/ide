package com.teamide.app.generater.dictionary.merge;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.generater.BaseMergeGenerater;
import com.teamide.app.generater.dictionary.DictionaryControllerGenerater;
import com.teamide.app.plugin.AppBean;

public class DictionaryMergeControllerGenerater extends BaseMergeGenerater {

	protected final List<DictionaryBean> dictionarys;

	public DictionaryMergeControllerGenerater(String directory, List<DictionaryBean> dictionarys, File sourceFolder,
			AppBean app, AppContext context) {
		super(directory, dictionarys, sourceFolder, app, context);
		this.dictionarys = dictionarys;
	}

	public String getPackage() {
		String pack = getControllerPackage();
		pack += "";
		return pack;
	}

	public String getClassName() {
		return super.getClassName() + "Controller";
	}

	@Override
	public void buildData() {

		JSONArray $dictionarys = new JSONArray();
		data.put("$dictionarys", $dictionarys);
		JSONArray list = new JSONArray();

		for (DictionaryBean dictionary : dictionarys) {
			DictionaryControllerGenerater generater = new DictionaryControllerGenerater(dictionary, sourceFolder, app,
					context);
			generater.init();
			try {
				String name = dictionary.getName();
				if (name.indexOf("/") > 0) {
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				generater.data.put("$method_name", name);

				if (generater.data.get("$dictionary") != null) {
					JSONObject $dictionary = generater.data.getJSONObject("$dictionary");
					String $method_name = $dictionary.getString("$name");
					if ($method_name.indexOf("/") > 0) {
						$method_name = $method_name.substring($method_name.lastIndexOf("/") + 1);
					}
					$dictionary.put("$method_name", $method_name);

					$dictionarys.add($dictionary);
				}
				generater.data.put("$only_content", true);
				String content = generater.build();
				JSONObject data = generater.data;
				data.put("$content", content);
				list.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		data.put("$list", list);

	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/merge/controller/dictionary";
	}

}

package com.teamide.ide.generater.dao.merge;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.generater.dao.DaoControllerGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class DaoMergeControllerGenerater extends BaseMergeGenerater {

	protected final List<DaoBean> daos;

	public DaoMergeControllerGenerater(String directory, List<DaoBean> daos, RepositoryProcessorParam param,
			AppBean app, AppContext context) {
		super(directory, daos, param, app, context);
		this.daos = daos;
	}

	public String getPackage() {
		String pack = getControllerPackage();
		pack += ".dao";
		return pack;
	}

	public String getClassName() {
		return super.getClassName() + "Controller";
	}

	@Override
	public void buildData() {

		JSONArray $propertys = new JSONArray();
		data.put("$propertys", $propertys);
		JSONArray list = new JSONArray();

		for (DaoBean dao : daos) {
			DaoControllerGenerater generater = new DaoControllerGenerater(dao, param, app, context);
			generater.init();
			try {
				String name = dao.getName();
				if (name.indexOf("/") > 0) {
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				generater.data.put("$method_name", name);

				if (generater.data.get("$dao") != null) {
					JSONObject $dao = generater.data.getJSONObject("$dao");
					String $method_name = $dao.getString("$name");
					if ($method_name.indexOf("/") > 0) {
						$method_name = $method_name.substring($method_name.lastIndexOf("/") + 1);
					}
					$dao.put("$method_name", $method_name);

					$dao.put("$propertyname", $dao.getString("$merge_propertyname"));
					String $merge_propertyname = $dao.getString("$merge_propertyname");
					boolean find = false;
					for (int n = 0; n < $propertys.size(); n++) {
						JSONObject $property = $propertys.getJSONObject(n);
						if ($property.getString("$name").equals($merge_propertyname)) {
							find = true;
						}
					}
					if (!find) {
						JSONObject $property = new JSONObject();
						$property.put("$package", $dao.getString("$merge_package"));
						$property.put("$classname", $dao.getString("$merge_classname"));
						$property.put("$name", $merge_propertyname);
						$propertys.add($property);
					}
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

		return "template/java/merge/controller/dao";
	}

}

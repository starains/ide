package com.teamide.app.generater.service.merge;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.generater.BaseMergeGenerater;
import com.teamide.app.generater.service.ServiceControllerGenerater;
import com.teamide.app.plugin.AppBean;

public class ServiceMergeControllerGenerater extends BaseMergeGenerater {

	protected final List<ServiceBean> services;

	public ServiceMergeControllerGenerater(String directory, List<ServiceBean> services, File sourceFolder, AppBean app,
			AppContext context) {
		super(directory, services, sourceFolder, app, context);
		this.services = services;
	}

	public String getPackage() {
		String pack = getControllerPackage();
		pack += ".service";
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

		for (ServiceBean service : services) {
			ServiceControllerGenerater generater = new ServiceControllerGenerater(service, sourceFolder, app, context);
			generater.init();
			try {
				String name = service.getName();
				if (name.indexOf("/") > 0) {
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				generater.data.put("$method_name", name);

				if (generater.data.get("$service") != null) {
					JSONObject $service = generater.data.getJSONObject("$service");
					String $method_name = $service.getString("$name");
					if ($method_name.indexOf("/") > 0) {
						$method_name = $method_name.substring($method_name.lastIndexOf("/") + 1);
					}
					$service.put("$method_name", $method_name);

					$service.put("$propertyname", $service.getString("$merge_propertyname"));
					String $merge_propertyname = $service.getString("$merge_propertyname");
					boolean find = false;
					for (int n = 0; n < $propertys.size(); n++) {
						JSONObject $property = $propertys.getJSONObject(n);
						if ($property.getString("$name").equals($merge_propertyname)) {
							find = true;
						}
					}
					if (!find) {
						JSONObject $property = new JSONObject();
						$property.put("$package", $service.getString("$merge_package"));
						$property.put("$classname", $service.getString("$merge_classname"));
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
		return "template/java/merge/controller/service";
	}

}

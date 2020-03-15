package com.teamide.ide.generater.service.merge;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.generater.service.ServiceGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class ServiceMergeGenerater extends BaseMergeGenerater {

	protected final List<ServiceBean> services;

	public ServiceMergeGenerater(String directory, List<ServiceBean> services, RepositoryProcessorParam param,
			AppBean app, AppContext context) {
		super(directory, services, param, app, context);
		this.services = services;
	}

	public String getPackage() {
		return getServicePackage();
	}

	@Override
	public void buildData() {

		JSONArray $propertys = new JSONArray();
		data.put("$propertys", $propertys);
		JSONArray list = new JSONArray();

		for (ServiceBean service : services) {
			ServiceGenerater generater = new ServiceGenerater(service, param, app, context);
			generater.init();
			try {
				String name = service.getName();
				if (name.indexOf("/") > 0) {
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				generater.data.put("$method_name", name);

				JSONArray $processs = generater.data.getJSONArray("$processs");
				if (generater.data.get("$start") != null) {
					if (generater.data.getJSONObject("$start").get("$next") != null) {
						JSONObject $next = generater.data.getJSONObject("$start").getJSONObject("$next");
						String $next_method = $next.getString("$method");
						$next_method = name + $next_method.substring(0, 1).toUpperCase() + $next_method.substring(1);
						$next.put("$method", $next_method);
					}
				}
				for (int i = 0; i < $processs.size(); i++) {
					JSONObject $process = $processs.getJSONObject(i);
					String $method = $process.getString("$method");
					$method = name + $method.substring(0, 1).toUpperCase() + $method.substring(1);
					$process.put("$method", $method);

					if ($process.get("$next") != null) {
						JSONObject $next = $process.getJSONObject("$next");
						String $next_method = $next.getString("$method");
						$next_method = name + $next_method.substring(0, 1).toUpperCase() + $next_method.substring(1);
						$next.put("$method", $next_method);
					}

					if ($process.get("$conditions") != null) {
						JSONArray $conditions = $process.getJSONArray("$conditions");
						for (int n = 0; n < $conditions.size(); n++) {
							JSONObject $condition = $conditions.getJSONObject(n);
							if ($condition.get("$next") != null) {
								JSONObject $next = $condition.getJSONObject("$next");
								String $next_method = $next.getString("$method");
								$next_method = name + $next_method.substring(0, 1).toUpperCase()
										+ $next_method.substring(1);
								$next.put("$method", $next_method);
							}
						}
					}
					if ($process.get("$dao") != null) {
						JSONObject $dao = $process.getJSONObject("$dao");
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
					if ($process.get("$service") != null) {
						JSONObject $service = $process.getJSONObject("$service");
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
		return "template/java/merge/service/default";
	}

}

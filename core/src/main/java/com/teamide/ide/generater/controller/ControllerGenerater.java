package com.teamide.ide.generater.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.ControlBean;
import com.teamide.app.bean.ControlMethodBean;
import com.teamide.app.bean.ControlMethodProcessBean;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.generater.BaseGenerater;
import com.teamide.ide.generater.code.ValidateGenerater;
import com.teamide.ide.generater.code.VariableGenerater;
import com.teamide.ide.generater.dao.DaoGenerater;
import com.teamide.ide.generater.service.ServiceGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class ControllerGenerater extends BaseGenerater {

	protected final ControlBean control;

	public ControllerGenerater(ControlBean control, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(control, param, app, context);
		this.control = control;
	}

	public void generate() throws Exception {
		super.generate();
	}

	public String getPackage() {
		return getControllerPackage();
	}

	public String getClassName() {
		return super.getClassName() + "Controller";
	}

	@Override
	public void buildData() {
		List<ControlMethodBean> methods = control.getMethods();
		JSONArray $methods = new JSONArray();
		JSONArray $propertys = new JSONArray();
		this.data.put("$methods", $methods);
		this.data.put("$propertys", $propertys);
		if (methods != null) {
			for (ControlMethodBean method : methods) {
				String $requestmapping = method.getRequestmapping();
				if (StringUtil.isEmpty($requestmapping)) {
					$requestmapping = "";
				}
				String $requestmethod = method.getRequestmethod();
				if (StringUtil.isEmpty($requestmethod)) {
					$requestmethod = null;
				}
				JSONObject $method = (JSONObject) JSON.toJSON(method);
				$methods.add($method);
				$method.put("$requestmapping", $requestmapping);
				$method.put("$$requestmethod", $requestmethod);
				$method.put("$userrequestbody", ObjectUtil.isTrue(method.getUserrequestbody()));
				VariableGenerater variableGenerater = new VariableGenerater(getAppFactoryClassname());
				StringBuffer $variable_content = variableGenerater.generate(3, method.getVariables());
				if (StringUtil.isEmpty($variable_content)) {
					$variable_content = null;
				}
				$method.put("$variable_content", $variable_content);

				ValidateGenerater validateGenerater = new ValidateGenerater(getAppFactoryClassname());
				StringBuffer $validate_content = validateGenerater.generate(3, method.getValidates());
				if (StringUtil.isEmpty($validate_content)) {
					$validate_content = null;
				}
				$method.put("$validate_content", $validate_content);

				JSONArray $processs = new JSONArray();
				$method.put("$processs", $processs);
				if (method.getProcesss() != null) {

					for (ControlMethodProcessBean process : method.getProcesss()) {
						if (StringUtil.isEmpty(process.getType())) {
							continue;
						}
						JSONObject $process = (JSONObject) JSON.toJSON(process);
						$processs.add($process);

						int tab = 3;

						String ifrule = process.getIfrule();
						if (StringUtil.isEmpty(ifrule)) {
							$process.put("$ifrule", null);
						} else {
							tab++;
							$process.put("$ifrule", ifrule);
						}
						variableGenerater = new VariableGenerater(getAppFactoryClassname());
						$variable_content = variableGenerater.generate(tab, process.getVariables());
						if (StringUtil.isEmpty($variable_content)) {
							$variable_content = null;
						}
						$process.put("$variable_content", $variable_content);

						validateGenerater = new ValidateGenerater(getAppFactoryClassname());
						$validate_content = validateGenerater.generate(tab, process.getValidates());
						if (StringUtil.isEmpty($validate_content)) {
							$validate_content = null;
						}
						$process.put("$validate_content", $validate_content);

						$process.put("$property", null);
						if (process.getType().equalsIgnoreCase("DAO")) {
							DaoBean dao = context.get(DaoBean.class, process.getDaoname());
							if (dao != null) {
								DaoGenerater daoGenerater = new DaoGenerater(dao, param, app, context);
								daoGenerater.init();
								String $propertyname = daoGenerater.data.getString("$propertyname");
								String $classname = daoGenerater.data.getString("$classname");
								String $package = daoGenerater.data.getString("$package");
								if (isMergedirectory()) {
									$propertyname = daoGenerater.data.getString("$merge_propertyname");
									$classname = daoGenerater.data.getString("$merge_classname");
									$package = daoGenerater.data.getString("$merge_package");
								}
								boolean find = false;
								for (int n = 0; n < $propertys.size(); n++) {
									JSONObject $property = $propertys.getJSONObject(n);
									if ($property.getString("$name").equals($propertyname)) {
										find = true;
									}
								}
								JSONObject $property = new JSONObject();
								$property.put("$package", $package);
								$property.put("$classname", $classname);
								$property.put("$name", $propertyname);
								if (!find) {
									$propertys.add($property);
								}

								$process.put("$property", $property);
							}
						} else if (process.getType().equalsIgnoreCase("SERVICE")) {
							ServiceBean service = context.get(ServiceBean.class, process.getServicename());
							if (service != null) {
								ServiceGenerater serviceGenerater = new ServiceGenerater(service, param, app, context);
								serviceGenerater.init();

								String $propertyname = serviceGenerater.data.getString("$propertyname");
								String $classname = serviceGenerater.data.getString("$classname");
								String $package = serviceGenerater.data.getString("$package");
								if (isMergedirectory()) {
									$propertyname = serviceGenerater.data.getString("$merge_propertyname");
									$classname = serviceGenerater.data.getString("$merge_classname");
									$package = serviceGenerater.data.getString("$merge_package");
								}
								boolean find = false;
								for (int n = 0; n < $propertys.size(); n++) {
									JSONObject $property = $propertys.getJSONObject(n);
									if ($property.getString("$name").equals($propertyname)) {
										find = true;
									}
								}
								JSONObject $property = new JSONObject();
								$property.put("$package", $package);
								$property.put("$classname", $classname);
								$property.put("$name", $propertyname);
								if (!find) {
									$propertys.add($property);
								}

								$process.put("$property", $property);
							}
						}
					}
				}

			}
		}

	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/controller/controller";
	}

}

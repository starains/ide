package com.teamide.ide.protect.processor.repository.generater;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.enums.ServiceProcessType;
import com.teamide.app.process.ServiceProcess;
import com.teamide.app.process.service.DaoProcess;
import com.teamide.app.process.service.ErrorEndProcess;
import com.teamide.bean.PageResultBean;
import com.teamide.exception.Errcode;
import com.teamide.ide.generater.ValidateGenerater;
import com.teamide.ide.generater.VariableGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public class ServiceGenerater extends CodeGenerater {
	protected final ServiceBean service;

	public ServiceGenerater(ServiceBean service, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(service, param, app, context);
		this.service = service;
	}

	public String getPackage() {
		String pack = app.getOption().getServicepackage();
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".service";
		}
		return pack;
	}

	@Override
	public void buildData() {
		JSONArray $processs = new JSONArray();
		JSONArray $propertys = new JSONArray();
		ServiceProcess start = null;
		List<ServiceProcess> processs = service.getProcesss();
		for (ServiceProcess process : processs) {
			if (ServiceProcessType.START.getValue().equalsIgnoreCase(process.getType())) {
				start = process;
				break;
			}
		}
		data.put("$start", null);
		data.put("$end", null);
		data.put("$result_name", null);
		data.put("$has_last_result", false);
		appendProcess($processs, start, $propertys);
		data.put("$processs", $processs);
		data.put("$propertys", $propertys);
		data.put("$service", JSON.toJSON(service));
		data.put("$requestmethod", null);
		if (StringUtil.isNotEmpty(service.getRequestmethod())) {
			data.put("$requestmethod", service.getRequestmethod());
		}

	}

	public void appendProcess(JSONArray $processs, ServiceProcess process, JSONArray $propertys) {
		if (process == null) {
			return;
		}

		JSONObject $process = (JSONObject) JSONObject.toJSON(process);
		String method = process.getName().replaceAll("\\.", "_");
		$process.put("$method", method);

		VariableGenerater variableGenerater = new VariableGenerater(getAppFactoryClassname());
		$process.put("$variable_content", variableGenerater.generate(2, process.getVariables()));

		ValidateGenerater validateGenerater = new ValidateGenerater(getAppFactoryClassname());
		$process.put("$validate_content", validateGenerater.generate(2, process.getValidates()));

		if (data.get("$process_" + process.getName()) != null) {
			return;
		}
		data.put("$process_" + process.getName(), $process);
		$processs.add($process);

		appendProcessTos($processs, process, $process, $propertys);

		if (ServiceProcessType.START.getValue().equalsIgnoreCase(process.getType())) {
			data.put("$start", $process);
		}
		if (ServiceProcessType.END.getValue().equalsIgnoreCase(process.getType())) {
			data.put("$end", $process);
		}

		if (ServiceProcessType.DAO.getValue().equalsIgnoreCase(process.getType())) {
			DaoProcess daoProcess = (DaoProcess) process;
			DaoBean dao = context.get(DaoBean.class, daoProcess.getDaoname());
			if (dao != null) {
				DaoGenerater daoGenerater = new DaoGenerater(dao, param, app, context);
				daoGenerater.init();

				JSONObject $dao = daoGenerater.data;
				String $propertyname = $dao.getString("$propertyname");
				boolean find = false;
				for (int i = 0; i < $propertys.size(); i++) {
					JSONObject $property = $propertys.getJSONObject(i);
					if ($property.getString("$name").equals($propertyname)) {
						find = true;
					}
				}
				if (!find) {
					JSONObject $property = new JSONObject();
					$property.put("$comment", $dao.getString("$comment"));
					$property.put("$package", $dao.getString("$package"));
					$property.put("$classname", $dao.getString("$classname"));
					$property.put("$name", $propertyname);
					$propertys.add($property);
				}
				$process.put("$dao", $dao);

				int dao_service_count = 0;
				for (ServiceProcess p : service.getProcesss()) {
					if (ServiceProcessType.DAO.getValue().equalsIgnoreCase(p.getType())) {
						dao_service_count++;
					}
				}
				if (dao_service_count == 1) {
					if (StringUtil.isEmpty(daoProcess.getResultname())) {
						data.put("$result_name", daoProcess.getName());
					} else {
						data.put("$result_name", daoProcess.getResultname());
					}
					data.put("$result_classname", $dao.getString("$result_classname"));
					if ($dao.getString("$result_classname").indexOf("Page") >= 0) {
						imports.add(PageResultBean.class.getName());
					}
				}
				if (dao_service_count > 1) {
					data.put("$result_classname", "Object");
				}
				data.put("$has_last_result", true);
			}
			String datarule = StringUtil.trim(daoProcess.getData());
			$process.put("$datarule", null);
			if (!StringUtil.isEmpty(datarule)) {
				$process.put("$datarule", datarule);
			}
		}

		if (ServiceProcessType.ERROR_END.getValue().equalsIgnoreCase(process.getType())) {
			ErrorEndProcess errorEndProcess = (ErrorEndProcess) process;
			String errcode = errorEndProcess.getErrcode();
			String errmsg = errorEndProcess.getErrmsg();
			if (StringUtil.isEmpty(errcode)) {
				errcode = Errcode.FAIL;
			}
			$process.put("$errcode", errcode);
			$process.put("$errmsg", errmsg);
		}
	}

	public void appendProcessTos(JSONArray $processs, ServiceProcess process, JSONObject $process,
			JSONArray $propertys) {
		if (process == null) {
			return;
		}

		List<String> tos = process.getTos();
		if (tos == null || tos.size() == 0) {
			return;
		}

		JSONArray $conditions = new JSONArray();
		for (String to : tos) {
			if (StringUtil.isEmpty(to)) {
				continue;
			}
			ServiceProcess toProcess = getProcessByName(to);
			if (toProcess == null) {
				continue;
			}

			if (ServiceProcessType.DECISION.getValue().equalsIgnoreCase(process.getType())) {
				JSONObject $condition = (JSONObject) JSONObject.toJSON(toProcess);
				$conditions.add($condition);
				$process.put("$conditions", $conditions);

				if (toProcess.getTos() != null) {
					ServiceProcess conditionToProcess = null;
					for (String conditionTo : toProcess.getTos()) {
						if (StringUtil.isEmpty(conditionTo)) {
							continue;
						}
						conditionToProcess = getProcessByName(conditionTo);
					}
					if (conditionToProcess != null) {
						JSONObject $next = (JSONObject) JSON.toJSON(conditionToProcess);
						String method = conditionToProcess.getName().replaceAll("\\.", "_");
						$next.put("$method", method);

						$condition.put("$next", $next);

						appendProcess($processs, conditionToProcess, $propertys);
					}
				}

			} else {
				JSONObject $next = (JSONObject) JSON.toJSON(toProcess);
				String method = toProcess.getName().replaceAll("\\.", "_");
				$next.put("$method", method);
				$process.put("$next", $next);
				appendProcess($processs, toProcess, $propertys);
			}
		}
	}

	public ServiceProcess getProcessByName(String name) {
		if (name == null) {
			return null;
		}

		for (ServiceProcess p : service.getProcesss()) {
			if (name.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/service/default";
	}

}

package com.teamide.protect.ide.processor.repository.generater;

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
import com.teamide.util.StringUtil;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.project.AppBean;

public class ServiceGenerater extends CodeGenerater {

	protected final ServiceBean service;

	public ServiceGenerater(ServiceBean service, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(service, param, app, context);
		this.service = service;
	}

	public void appendContentCenter() throws Exception {
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
		appendProcess($processs, start);
		data.put("$processs", $processs);
		data.put("$propertys", $propertys);
		data.put("$service", JSON.toJSON(service));

	}

	public void appendProcess(JSONArray $processs, ServiceProcess process) {
		if (process == null) {
			return;
		}

		JSONObject $process = (JSONObject) JSONObject.toJSON(process);
		if (data.get("$process_" + process.getName()) != null) {
			return;
		}
		data.put("$process_" + process.getName(), $process);
		$processs.add($process);

		appendProcessTos($processs, process, $process);

		if (ServiceProcessType.START.getValue().equalsIgnoreCase(process.getType())) {
			data.put("$start", $process);
		}
		if (ServiceProcessType.END.getValue().equalsIgnoreCase(process.getType())) {
			data.put("$end", $process);
		}

		if (ServiceProcessType.DAO.getValue().equalsIgnoreCase(process.getType())) {
			DaoProcess daoProcess = (DaoProcess) process;
			DaoBean dao = context.get(DaoBean.class, daoProcess.getDaoname());
			DaoGenerater daoGenerater = new DaoGenerater(dao, param, app, context);
			daoGenerater.init();
			$process.put("$dao", daoGenerater.data);
		}
	}

	public void appendProcessTos(JSONArray $processs, ServiceProcess process, JSONObject $process) {
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
						$condition.put("$next", JSON.toJSON(conditionToProcess));

						appendProcess($processs, conditionToProcess);
					}
				}

			} else {
				$process.put("$next", JSON.toJSON(toProcess));
				appendProcess($processs, toProcess);
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
		return "template/java/service";
	}

}

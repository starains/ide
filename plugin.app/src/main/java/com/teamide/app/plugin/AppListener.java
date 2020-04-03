package com.teamide.app.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.Application;
import com.teamide.app.ApplicationFactory;
import com.teamide.app.application.base.ApplicationDatabase;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.bean.TableBean;
import com.teamide.app.enums.BeanModelType;
import com.teamide.app.generater.RepositoryGenerateSourceCode;
import com.teamide.app.util.ModelFileUtil;
import com.teamide.bean.Status;
import com.teamide.db.DBDataSource;
import com.teamide.db.bean.Database;
import com.teamide.db.bean.Table;
import com.teamide.ide.plugin.IDEListener;
import com.teamide.ide.plugin.PluginParam;
import com.teamide.param.DataParam;
import com.teamide.util.RequestUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

public class AppListener implements IDEListener {

	@Override
	public void onServletEvent(PluginParam param, String event, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtil.isEmpty(event)) {
			return;
		}
		if (event.endsWith("toText")) {
			toText(request, response);
		} else if (event.endsWith("toModel")) {
			toModel(request, response);
		} else if (event.endsWith("doTest")) {
			doTest(request, response);
		} else if (event.endsWith("LOAD_APP")) {
			JSONObject value = new JSONObject();
			ProjectAppLoader loader = new ProjectAppLoader(param.getParam().getSourceFolder());
			try {
				value.put("app", loader.loadApp(param.getParam().getProjectPath(), param.getOption()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			Status status = Status.SUCCESS();
			status.setValue(value);
			ResponseUtil.outJSON(response, status);
		} else if (event.endsWith("GENERATE_SOURCE_CODE")) {

			JSONObject value = new JSONObject();
			RepositoryGenerateSourceCode generate = new RepositoryGenerateSourceCode(
					param.getParam().getSourceFolder());
			try {
				generate.generate(param.getParam().getProjectPath(), param.getOption());
			} catch (Exception e) {
				e.printStackTrace();
			}

			Status status = Status.SUCCESS();
			status.setValue(value);
			ResponseUtil.outJSON(response, status);
		}

	}

	public void toText(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.parseObject(RequestUtil.getBody(request));
		JSONObject model = json.getJSONObject("model");
		String type = json.getString("type");
		String filename = json.getString("filename");
		BeanModelType modelType = BeanModelType.get(type);
		String text = "";
		if (modelType != null) {
			try {
				String fileType = ModelFileUtil.getTypeByFileName(filename);
				Object bean = ModelFileUtil.jsonToBean(modelType.getBeanClass(), model);
				text = ModelFileUtil.beanToText(bean, fileType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ResponseUtil.outText(response, text);
		return;
	}

	public void toModel(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.parseObject(RequestUtil.getBody(request));
		String text = json.getString("text");
		String type = json.getString("type");
		String filename = json.getString("filename");
		BeanModelType modelType = BeanModelType.get(type);
		Status status = Status.SUCCESS();
		if (modelType != null) {
			try {
				String fileType = ModelFileUtil.getTypeByFileName(filename);
				Object bean = ModelFileUtil.textToBean(modelType.getBeanClass(), text, fileType);
				status.setErrcode(0);
				status.setValue(bean);
			} catch (Exception e) {
				e.printStackTrace();
				status.setErrcode(-1);
				status.setErrmsg(e.getMessage());
			}
		}
		ResponseUtil.outJSON(response, status);
		return;
	}

	public void doTest(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.parseObject(RequestUtil.getBody(request));
		String data = json.getString("data");
		String type = json.getString("type");
		String path = json.getString("path");
		String name = json.getString("name");
		Object result = null;
		Application application = null;
		try {
			application = ApplicationFactory.start(new File(path));
			List<DatabaseBean> databases = application.getContext().get(DatabaseBean.class);
			if (databases != null) {
				for (DatabaseBean database : databases) {
					database.setInitializeclass(null);
				}
			}
			if (application.getContext().getJdbc() != null) {
				application.getContext().getJdbc().setInitializeclass(null);
			}
			if (type.equalsIgnoreCase("DATABASE")) {
				application.installDatabases();
				application.installTables();
				result = Status.SUCCESS();
			} else if (type.equalsIgnoreCase("LOAD_DATABASE_TABLES")) {

				Status status = Status.SUCCESS();
				Database database = application.getDatabase(name);
				if (database != null) {
					DBDataSource dbDataSource = DBDataSource.create(database);

					String tablename = json.getString("tablename");
					if (StringUtil.isNotEmpty(tablename)) {
						Table table = dbDataSource.getDialect().getTable(dbDataSource.getDataSource(), tablename);
						if (table != null) {
							status.setValue(ApplicationDatabase.formatToTableBean(table));
						}
					} else {
						List<Table> tables = dbDataSource.getDialect().getTables(dbDataSource.getDataSource());
						List<TableBean> list = new ArrayList<TableBean>();
						for (Table table : tables) {
							list.add(ApplicationDatabase.formatToTableBean(table));
						}
						status.setValue(list);
					}
					dbDataSource.destroy();
				}

				result = status;
			} else if (type.equalsIgnoreCase("DAO")) {
				DataParam param = new DataParam((JSON) JSON.parse(data));
				DaoBean dao = application.getContext().get(DaoBean.class, name);
				result = application.invokeDao(dao, param);
				application.responseResult(dao, result, response, true);
				return;
			} else if (type.equalsIgnoreCase("SERVICE")) {
				DataParam param = new DataParam((JSON) JSON.parse(data));
				ServiceBean service = application.getContext().get(ServiceBean.class, name);
				result = application.invokeService(service, param);
				application.responseResult(service, result, response, true);
				return;
			}
		} catch (Exception e) {
			Status status = new Status();
			status.setErrcode(-1);
			status.setErrmsg(e.getMessage());
			result = status;
			e.printStackTrace();
		} finally {
			if (application != null) {
				try {
					application.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		ResponseUtil.outJSON(response, result);
		return;
	}

	@Override
	public Object onDoEvent(PluginParam param, String event, JSONObject data) {
		return null;
	}

	@Override
	public Object onLoadEvent(PluginParam param, String event, JSONObject data) {
		return null;
	}
}

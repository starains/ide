package com.teamide.ide.protect.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.process.DaoProcess;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.generater.dao.SQLDaoGenerater;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public class DaoGenerater extends SQLDaoGenerater {

	public DaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	public String getPackage() {
		return getDaoPackage();
	}

	@Override
	public void buildData() {
		DaoProcess daoProcess = dao.getProcess();
		if (daoProcess != null) {
			if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {
				buildSQLData();
			}
		}
		data.put("$requestmethod", null);
		if (StringUtil.isNotEmpty(dao.getRequestmethod())) {
			data.put("$requestmethod", dao.getRequestmethod());
		}

	}

	@Override
	public String getTemplate() throws Exception {

		DaoProcess daoProcess = dao.getProcess();

		if (daoProcess != null) {
			if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {

				DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
				if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {
					return "template/java/dao/sql/select";
				} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {
					return "template/java/dao/sql/insert";
				} else if (sqlProcess.getSqlType().indexOf("UPDATE") >= 0) {
					return "template/java/dao/sql/update";
				} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {
					return "template/java/dao/sql/delete";
				} else if (sqlProcess.getSqlType().indexOf("CUSTOM") >= 0) {
					return "template/java/dao/sql/custom";
				} else if (sqlProcess.getSqlType().indexOf("SAVE") >= 0) {
					return "template/java/dao/sql/save";
				} else {
					throw new Exception("sql type [" + sqlProcess.getSqlType() + "] template does not exist.");
				}
			} else if (DaoProcessType.HTTP.getValue().equals(daoProcess.getType())) {
				return "template/java/dao/http";
			} else if (DaoProcessType.CACHE.getValue().equals(daoProcess.getType())) {
				return "template/java/dao/cache";
			}
		}
		return "template/java/dao/default";
	}

}

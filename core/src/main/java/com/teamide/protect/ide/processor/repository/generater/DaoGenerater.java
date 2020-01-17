package com.teamide.protect.ide.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.process.DaoProcess;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.util.StringUtil;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.generater.dao.SQLDaoGenerater;
import com.teamide.protect.ide.processor.repository.project.AppBean;

public class DaoGenerater extends SQLDaoGenerater {

	public DaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		String pack = app.getOption().getDaopackage();
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".dao";
		}
		return pack;
	}

	@Override
	public void buildData() {
		DaoProcess daoProcess = dao.getProcess();

		if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {
			buildSQLData();
		}

	}

	@Override
	public String getTemplate() throws Exception {

		DaoProcess daoProcess = dao.getProcess();

		if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {

			DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
			if (sqlProcess.getSqlType().startsWith("SELECT")) {
				return "template/java/dao/sql/select";
			} else if (sqlProcess.getSqlType().startsWith("BATCH_CUSTOM")) {
				return "template/java/dao/sql/batchCustom";
			} else if (sqlProcess.getSqlType().startsWith("CUSTOM")) {
				return "template/java/dao/sql/custom";
			} else if (sqlProcess.getSqlType().startsWith("BATCH")) {
				return "template/java/dao/sql/batchExecute";
			}
			return "template/java/dao/sql/execute";
		} else if (DaoProcessType.HTTP.getValue().equals(daoProcess.getType())) {
			return "template/java/dao/http";
		} else if (DaoProcessType.CACHE.getValue().equals(daoProcess.getType())) {
			return "template/java/dao/cache";
		}
		return "template/java/dao/default";
	}

}

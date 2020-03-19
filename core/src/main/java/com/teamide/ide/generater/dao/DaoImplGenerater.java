package com.teamide.ide.generater.dao;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.process.DaoProcess;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class DaoImplGenerater extends DaoGenerater {

	public DaoImplGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	@Override
	public String getCodePackage() {
		String pack = super.getCodePackage();
		pack += ".impl";
		return pack;
	}

	@Override
	public String getClassName() {
		return super.getClassName().substring(1);
	}

	@Override
	public String getMergePackage() {
		return super.getMergePackage() + ".impl";
	}

	@Override
	public String getMergeClassName() {
		return super.getMergeClassName().substring(1);
	}

	@Override
	public void buildData() {
		super.buildData();
		data.put("$i_package", super.getCodePackage());
		data.put("$i_classname", "I" + this.getClassName());

	}

	@Override
	public String getTemplate() throws Exception {

		DaoProcess daoProcess = dao.getProcess();

		if (daoProcess != null) {
			if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {

				DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
				if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {
					if (isUsemybatis()) {
						return "template/java/dao/impl/mapper/select";
					}
					return "template/java/dao/impl/sql/select";
				} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {
					if (isUsemybatis()) {
						return "template/java/dao/impl/mapper/insert";
					}
					return "template/java/dao/impl/sql/insert";
				} else if (sqlProcess.getSqlType().indexOf("UPDATE") >= 0) {
					if (isUsemybatis()) {
						return "template/java/dao/impl/mapper/update";
					}
					return "template/java/dao/impl/sql/update";
				} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {
					if (isUsemybatis()) {
						return "template/java/dao/impl/mapper/delete";
					}
					return "template/java/dao/impl/sql/delete";
				} else if (sqlProcess.getSqlType().indexOf("CUSTOM") >= 0) {
					if (isUsemybatis()) {
						return "template/java/dao/impl/mapper/custom";
					}
					return "template/java/dao/impl/sql/custom";
				} else if (sqlProcess.getSqlType().indexOf("SAVE") >= 0) {
					if (isUsemybatis()) {
						return "template/java/dao/impl/mapper/save";
					}
					return "template/java/dao/impl/sql/save";
				} else {
					throw new Exception("sql type [" + sqlProcess.getSqlType() + "] template does not exist.");
				}
			} else if (DaoProcessType.HTTP.getValue().equals(daoProcess.getType())) {
				return "template/java/dao/impl/http";
			} else if (DaoProcessType.CACHE.getValue().equals(daoProcess.getType())) {
				return "template/java/dao/impl/cache";
			}
		}
		return "template/java/dao/impl/default";
	}

}

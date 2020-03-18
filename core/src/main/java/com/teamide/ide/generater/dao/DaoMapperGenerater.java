package com.teamide.ide.generater.dao;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.process.DaoProcess;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class DaoMapperGenerater extends DaoGenerater {

	public DaoMapperGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	@Override
	public File getFile() {

		File resourcesFolder = getResourcesFolder();
		String name = this.bean.getName();
		File file = new File(resourcesFolder, "mapper/" + name + ".xml");
		return file;
	}

	@Override
	public void buildData() {
		super.buildData();

	}

	@Override
	public String getTemplate() throws Exception {

		DaoProcess daoProcess = dao.getProcess();

		if (daoProcess != null) {
			if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {

				DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
				if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {
					return "template/java/mapper/select.xml";
				} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {
					return "template/java/mapper/insert.xml";
				} else if (sqlProcess.getSqlType().indexOf("UPDATE") >= 0) {
					return "template/java/mapper/update.xml";
				} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {
					return "template/java/mapper/delete.xml";
				} else if (sqlProcess.getSqlType().indexOf("CUSTOM") >= 0) {
					return "template/java/mapper/custom.xml";
				} else if (sqlProcess.getSqlType().indexOf("SAVE") >= 0) {
					return "template/java/mapper/save.xml";
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

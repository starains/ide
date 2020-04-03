package com.teamide.app.plugin;

import java.io.File;
import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.Application;
import com.teamide.app.ApplicationFactory;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.enums.BeanModelType;
import com.teamide.app.enums.ServiceProcessType;
import com.teamide.app.process.ServiceProcess;
import com.teamide.app.process.service.DaoProcess;
import com.teamide.app.process.service.SubServiceProcess;
import com.teamide.app.util.ModelFileUtil;
import com.teamide.util.FileUtil;

public class FileMoveService {
	public void modelRename(File appModelFolder, File modelFolder, BeanModelType model, File oldFile, File newFile)
			throws Exception {
		if (model == null) {
			return;
		}

		if (model == BeanModelType.DAO || model == BeanModelType.SERVICE) {
			String path = modelFolder.toURI().getPath();
			String oldPath = oldFile.toURI().getPath();
			String newPath = newFile.toURI().getPath();
			if (!oldPath.startsWith(path)) {
				return;
			}
			if (!newPath.startsWith(path)) {
				return;
			}
			String oldName = oldPath.substring(path.length());
			String newName = newPath.substring(path.length());
			try {

				Application application = ApplicationFactory.start(appModelFolder);
				if (application.getContext() != null) {
					AppContext context = application.getContext();

					List<ServiceBean> services = context.get(ServiceBean.class);
					for (ServiceBean service : services) {
						boolean change = false;
						if (service.getProcesss() != null) {
							for (ServiceProcess process : service.getProcesss()) {

								if (ServiceProcessType.DAO.getValue().equalsIgnoreCase(process.getType())
										&& "DAO".contentEquals(model.getValue())) {

									DaoProcess daoProcess = (DaoProcess) process;
									if (oldName.equals(daoProcess.getDaoname())) {
										daoProcess.setDaoname(newName);
										change = true;
									}
								} else if (ServiceProcessType.SUB_SERVICE.getValue().equalsIgnoreCase(process.getType())
										&& "SERVICE".contentEquals(model.getValue())) {
									SubServiceProcess subServiceProcess = (SubServiceProcess) process;
									if (oldName.equals(subServiceProcess.getServicename())) {
										subServiceProcess.setServicename(newName);
										change = true;
									}
								}
							}
						}
						if (change) {
							File file = new File(service.getLocalfilepath());
							if (file.exists()) {
								service.setLocalfilepath(null);
								String fileType = ModelFileUtil.getTypeByFileName(file.getName());
								String text = ModelFileUtil.beanToText(service, fileType);
								FileUtil.write(text.getBytes(), file);
							}
						}
					}
				}
				application.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}

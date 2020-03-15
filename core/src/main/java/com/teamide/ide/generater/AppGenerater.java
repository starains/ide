package com.teamide.ide.generater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.app.AppContext;
import com.teamide.app.bean.BeanBean;
import com.teamide.app.bean.ControlBean;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.generater.bean.BeanGenerater;
import com.teamide.ide.generater.controller.ControllerGenerater;
import com.teamide.ide.generater.dao.DaoControllerGenerater;
import com.teamide.ide.generater.dao.DaoGenerater;
import com.teamide.ide.generater.dao.merge.DaoMergeControllerGenerater;
import com.teamide.ide.generater.dao.merge.DaoMergeGenerater;
import com.teamide.ide.generater.dictionary.DictionaryControllerGenerater;
import com.teamide.ide.generater.dictionary.DictionaryGenerater;
import com.teamide.ide.generater.dictionary.merge.DictionaryMergeControllerGenerater;
import com.teamide.ide.generater.factory.FactoryGenerater;
import com.teamide.ide.generater.resources.ResourcesGenerater;
import com.teamide.ide.generater.service.ServiceControllerGenerater;
import com.teamide.ide.generater.service.ServiceGenerater;
import com.teamide.ide.generater.service.merge.ServiceMergeControllerGenerater;
import com.teamide.ide.generater.service.merge.ServiceMergeGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class AppGenerater extends Generater {

	public AppGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(param, app, context);
	}

	public void removeDirectory(File parent) throws Exception {
		if (parent.listFiles().length == 0) {
			parent.delete();
		} else {
			File[] fs = parent.listFiles();
			for (File f : fs) {
				if (f.isDirectory()) {
					removeDirectory(f);
				}
			}
		}
	}

	public void removeOld() throws Exception {
		File folder = getJavaFolder();
		List<File> files = FileUtil.loadAllFiles(folder.getAbsolutePath());
		for (File file : files) {
			if (file.isFile()) {
				if (file.getName().endsWith(".java")) {
					BufferedReader reader = null;
					InputStreamReader input = null;
					String line = null;
					try {
						input = new InputStreamReader(new FileInputStream(file), "UTF-8");
						reader = new BufferedReader(input);
						line = reader.readLine();
					} catch (Exception e) {
					} finally {
						if (input != null) {
							input.close();
						}
						if (reader != null) {
							reader.close();
						}
					}

					if (line != null) {
						if (line.indexOf(Generater.HEAD_NOTE) >= 0) {
							file.delete();
							removeDirectory(file.getParentFile());
						}
					}
				}
			}
		}
	}

	public void generate() throws Exception {
		removeOld();
		generateComponent();
		generateResource();
		generateFactory();
		generateDictionary();
		generateBean();
		generateDao();
		generateService();
		generateController();
	}

	public void generateResource() throws Exception {

		ResourcesGenerater generater = new ResourcesGenerater(param, app, context);
		generater.generate();

	}

	public void generateFactory() throws Exception {

		FactoryGenerater generater = new FactoryGenerater(param, app, context);
		generater.generate();

	}

	public void generateComponent() throws Exception {

		// Generater generater = new ComponentDaoGenerater(param, app, context);
		// generater.generate();
		//
		// generater = new ComponentTransactionGenerater(param, app, context);
		// generater.generate();

	}

	public void generateDictionary() throws Exception {

		if (isMergedirectory()) {
			List<DictionaryBean> dictionarys = context.get(DictionaryBean.class);
			Map<String, List<DictionaryBean>> map = new HashMap<String, List<DictionaryBean>>();
			for (DictionaryBean dictionary : dictionarys) {
				String name = dictionary.getName();
				String directory = "dictionary";
				if (name.indexOf("/") > 0) {
					directory = name.substring(0, name.lastIndexOf("/"));
				}
				List<DictionaryBean> directoryDictionarys = map.get(directory);
				if (directoryDictionarys == null) {
					directoryDictionarys = new ArrayList<DictionaryBean>();
					map.put(directory, directoryDictionarys);
				}
				directoryDictionarys.add(dictionary);

			}
			for (DictionaryBean dictionary : dictionarys) {
				DictionaryGenerater generater = new DictionaryGenerater(dictionary, param, app, context);
				generater.generate();
			}
			for (String directory : map.keySet()) {
				List<DictionaryBean> directoryDictionarys = map.get(directory);
				DictionaryMergeControllerGenerater controller = new DictionaryMergeControllerGenerater(directory,
						directoryDictionarys, param, app, context);
				controller.generate();
			}
		} else {

			List<DictionaryBean> dictionarys = context.get(DictionaryBean.class);
			for (DictionaryBean dictionary : dictionarys) {
				DictionaryGenerater generater = new DictionaryGenerater(dictionary, param, app, context);
				generater.generate();

				DictionaryControllerGenerater controller = new DictionaryControllerGenerater(dictionary, param, app,
						context);
				controller.generate();
			}
		}

	}

	public void generateDao() throws Exception {
		if (isMergedirectory()) {
			List<DaoBean> daos = context.get(DaoBean.class);
			Map<String, List<DaoBean>> map = new HashMap<String, List<DaoBean>>();
			for (DaoBean dao : daos) {
				String name = dao.getName();
				String directory = "base";
				if (name.indexOf("/") > 0) {
					directory = name.substring(0, name.lastIndexOf("/"));
				}
				List<DaoBean> directoryDaos = map.get(directory);
				if (directoryDaos == null) {
					directoryDaos = new ArrayList<DaoBean>();
					map.put(directory, directoryDaos);
				}
				directoryDaos.add(dao);

			}

			for (String directory : map.keySet()) {
				List<DaoBean> directoryDaos = map.get(directory);
				DaoMergeGenerater generater = new DaoMergeGenerater(directory, directoryDaos, param, app, context);
				generater.generate();

				List<DaoBean> controllerDaos = new ArrayList<DaoBean>();
				for (DaoBean directoryDao : directoryDaos) {
					if (StringUtil.isNotEmpty(directoryDao.getRequestmapping())) {
						controllerDaos.add(directoryDao);
					}
				}
				if (controllerDaos.size() > 0) {
					DaoMergeControllerGenerater controller = new DaoMergeControllerGenerater(directory, controllerDaos,
							param, app, context);
					controller.generate();
				}
			}

		} else {
			List<DaoBean> daos = context.get(DaoBean.class);
			for (DaoBean dao : daos) {
				DaoGenerater generater = new DaoGenerater(dao, param, app, context);
				generater.generate();

				DaoControllerGenerater controller = new DaoControllerGenerater(dao, param, app, context);
				controller.generate();
			}
		}

	}

	public void generateService() throws Exception {
		if (isMergedirectory()) {
			List<ServiceBean> services = context.get(ServiceBean.class);
			Map<String, List<ServiceBean>> map = new HashMap<String, List<ServiceBean>>();
			for (ServiceBean service : services) {
				String name = service.getName();
				String directory = "base";
				if (name.indexOf("/") > 0) {
					directory = name.substring(0, name.lastIndexOf("/"));
				}
				List<ServiceBean> directoryServices = map.get(directory);
				if (directoryServices == null) {
					directoryServices = new ArrayList<ServiceBean>();
					map.put(directory, directoryServices);
				}
				directoryServices.add(service);

			}

			for (String directory : map.keySet()) {
				List<ServiceBean> directoryServices = map.get(directory);
				ServiceMergeGenerater generater = new ServiceMergeGenerater(directory, directoryServices, param, app,
						context);
				generater.generate();

				List<ServiceBean> controllerServices = new ArrayList<ServiceBean>();
				for (ServiceBean directoryService : directoryServices) {
					if (StringUtil.isNotEmpty(directoryService.getRequestmapping())) {
						controllerServices.add(directoryService);
					}
				}
				if (controllerServices.size() > 0) {
					ServiceMergeControllerGenerater controller = new ServiceMergeControllerGenerater(directory,
							controllerServices, param, app, context);
					controller.generate();
				}

			}

		} else {
			List<ServiceBean> services = context.get(ServiceBean.class);
			for (ServiceBean service : services) {
				ServiceGenerater generater = new ServiceGenerater(service, param, app, context);
				generater.generate();

				ServiceControllerGenerater controller = new ServiceControllerGenerater(service, param, app, context);
				controller.generate();
			}
		}

	}

	public void generateController() throws Exception {
		List<ControlBean> controls = context.get(ControlBean.class);
		for (ControlBean control : controls) {
			ControllerGenerater generater = new ControllerGenerater(control, param, app, context);
			generater.generate();

		}
	}

	public void generateBean() throws Exception {
		List<BeanBean> beans = context.get(BeanBean.class);
		for (BeanBean bean : beans) {
			BeanGenerater generater = new BeanGenerater(bean, param, app, context);
			generater.generate();

		}
	}
}

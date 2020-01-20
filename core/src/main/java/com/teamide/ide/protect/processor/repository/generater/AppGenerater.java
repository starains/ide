package com.teamide.ide.protect.processor.repository.generater;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class AppGenerater extends Generater {

	public AppGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(param, app, context);
	}

	public void generate() throws Exception {
		generateResource();
		generateFactory();
		generateDictionary();
		generateDao();
		generateService();
	}

	public void generateResource() throws Exception {

		ResourceGenerater generater = new ResourceGenerater(param, app, context);
		generater.generate();

	}

	public void generateFactory() throws Exception {

		FactoryGenerater generater = new FactoryGenerater(param, app, context);
		generater.generate();

	}

	public void generateDictionary() throws Exception {

		List<DictionaryBean> dictionarys = context.get(DictionaryBean.class);
		for (DictionaryBean dictionary : dictionarys) {
			DictionaryGenerater generater = new DictionaryGenerater(dictionary, param, app, context);
			generater.generate();

			DictionaryControllerGenerater controller = new DictionaryControllerGenerater(dictionary, param, app,
					context);
			controller.generate();
		}

	}

	public void generateDao() throws Exception {
		List<DaoBean> daos = context.get(DaoBean.class);
		for (DaoBean dao : daos) {
			DaoGenerater generater = new DaoGenerater(dao, param, app, context);
			generater.generate();

			DaoControllerGenerater controller = new DaoControllerGenerater(dao, param, app, context);
			controller.generate();
		}

	}

	public void generateService() throws Exception {
		List<ServiceBean> services = context.get(ServiceBean.class);
		for (ServiceBean service : services) {
			ServiceGenerater generater = new ServiceGenerater(service, param, app, context);
			generater.generate();

			ServiceControllerGenerater controller = new ServiceControllerGenerater(service, param, app, context);
			controller.generate();
		}

	}

	public void generateController() throws Exception {

	}

}

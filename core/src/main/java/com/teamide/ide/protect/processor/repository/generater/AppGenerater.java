package com.teamide.ide.protect.processor.repository.generater;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.BeanBean;
import com.teamide.app.bean.ControlBean;
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

		ResourceGenerater generater = new ResourceGenerater(param, app, context);
		generater.generate();

	}

	public void generateFactory() throws Exception {

		FactoryGenerater generater = new FactoryGenerater(param, app, context);
		generater.generate();

	}

	public void generateComponent() throws Exception {

		Generater generater = new ComponentDaoGenerater(param, app, context);
		generater.generate();

		generater = new ComponentTransactionGenerater(param, app, context);
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

package com.teamide.app.generater.factory;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.generater.Generater;
import com.teamide.app.plugin.AppBean;

public class FactoryGenerater {

	public FactoryGenerater() {
	}

	public void generate(File sourceFolder, AppBean app, AppContext context) throws Exception {

		Generater generater = new AppFactoryGenerater(sourceFolder, app, context);
		generater.generate();

		generater = new DatabaseFactoryGenerater(sourceFolder, app, context);
		generater.generate();

		generater = new IDaoGenerater(sourceFolder, app, context);
		generater.generate();

		generater = new IDaoImplDaoGenerater(sourceFolder, app, context);
		generater.generate();
	}

}

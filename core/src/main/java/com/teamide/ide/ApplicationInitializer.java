package com.teamide.ide;

import java.sql.DriverManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.teamide.ide.handler.RemoteHandler;
import com.teamide.ide.handler.SpaceHandler;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.ide.handler.DeployHandler;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.impl.InstallService;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		IInstallService installService = new InstallService();
		boolean installed = installService.installed();

		if (installed) {
			System.out.println("plugin load start");
			PluginHandler.load();
			System.out.println("plugin load end");
			System.out.println("remote load start");
			RemoteHandler.loadRemotes();
			System.out.println("remote load end");
			System.out.println("space load start");
			SpaceHandler.loadSpaces();
			System.out.println("space load end");
			System.out.println("starter load start");
			DeployHandler.loadStarters();
			System.out.println("starter load end");
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		try {
			while (DriverManager.getDrivers().hasMoreElements()) {
				DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

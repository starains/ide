package com.teamide.deploer.server;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.Server;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.JreMemoryLeakPreventionListener;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.ThreadLocalLeakPreventionListener;
import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.apache.catalina.startup.Tomcat;

import com.teamide.deploer.IDEConstant;
import com.teamide.deploer.server.servlet.DeployerServlet;

public class ServerTomcat {

	private final ServerListener listener;

	public Tomcat tomcat;

	public Server server;

	public Context context;

	public ServerTomcat(ServerListener listener) throws Exception {
		this.listener = listener;
		startTomcat();
	}

	private void startTomcat() throws Exception {
		tomcat = new Tomcat();// 创建tomcat实例，用来启动tomcat

		tomcat.setHostname(listener.server.getHost());// 设置主机名

		tomcat.setPort(listener.server.getPort());

		tomcat.setBaseDir(IDEConstant.TOMCAT_FOLDER);

		StandardHost host = (StandardHost) tomcat.getHost();

		File workFolder = new File(IDEConstant.TOMCAT_WORK_FOLDER);
		if (!workFolder.exists()) {
			workFolder.mkdirs();
		}
		host.setWorkDir(workFolder.toURI().getPath());

		File webappsFolder = new File(IDEConstant.TOMCAT_WEBAPPS_FOLDER);
		if (!webappsFolder.exists()) {
			webappsFolder.mkdirs();
		}
		host.setAppBase(webappsFolder.toURI().getPath());

		// String DEFAULT_PROTOCOL =
		// "org.apache.coyote.http11.Http11NioProtocol";

		Connector connector = tomcat.getConnector();// 设置协议，默认就是这个协议
		connector.setURIEncoding("UTF-8");// 设置编码
		// connector.setPort(port);

		DeployerServlet servlet = new DeployerServlet(listener);
		String contextPath = listener.server.getContextPath();
		if (contextPath.equals("/")) {
			contextPath = "";
		}
		context = tomcat.addContext(contextPath, "");
		Wrapper wrapper = tomcat.addServlet(contextPath, servlet.getClass().getName(), servlet);
		wrapper.addMapping("/deployer/*");

		tomcat.start();// 启动tomcat

		server = tomcat.getServer();

		server.addLifecycleListener(new Tomcat.FixContextListener());
		server.addLifecycleListener(new AprLifecycleListener());
		server.addLifecycleListener(new JreMemoryLeakPreventionListener());
		server.addLifecycleListener(new GlobalResourcesLifecycleListener());
		server.addLifecycleListener(new ThreadLocalLeakPreventionListener());

	}

	public void await() {
		server.await();
	}

}

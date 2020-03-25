package com.teamide.deployer.server;

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

import com.teamide.deployer.IDEConstant;
import com.teamide.deployer.IDEDeployer;
import com.teamide.deployer.server.servlet.RemoteServlet;
import com.teamide.deployer.server.servlet.StarterServlet;
import com.teamide.util.StringUtil;

public class ServerTomcat {

	private final IDEDeployer deployer;

	public Tomcat tomcat;

	public Server server;

	public Context context;

	private final String host;

	private final int port;

	private final String contextPath;

	public ServerTomcat(IDEDeployer deployer) throws Exception {
		this.deployer = deployer;
		if (StringUtil.isEmpty(this.deployer.param.getString("server.host"))) {
			this.deployer.param.put("server.host", "0.0.0.0");
		}
		this.host = this.deployer.param.getString("server.host");

		if (StringUtil.isEmpty(this.deployer.param.getString("server.port"))) {
			this.deployer.param.put("server.port", "19001");
		}
		this.port = this.deployer.param.getIntValue("server.port");

		if (StringUtil.isEmpty(this.deployer.param.getString("server.contextPath"))) {
			this.deployer.param.put("server.contextPath", "/");
		}
		this.contextPath = this.deployer.param.getString("server.contextPath");

		startTomcat();
	}

	private void startTomcat() throws Exception {
		tomcat = new Tomcat();// 创建tomcat实例，用来启动tomcat

		tomcat.setHostname(host);// 设置主机名

		tomcat.setPort(port);

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

		String contextPath = this.contextPath;
		if (contextPath.equals("/")) {
			contextPath = "";
		}
		context = tomcat.addContext(contextPath, "");

		RemoteServlet remoteServlet = new RemoteServlet();
		Wrapper wrapper = tomcat.addServlet(contextPath, remoteServlet.getClass().getName(), remoteServlet);
		wrapper.addMapping("/remote/*");

		StarterServlet starterServlet = new StarterServlet();
		wrapper = tomcat.addServlet(contextPath, starterServlet.getClass().getName(), starterServlet);
		wrapper.addMapping("/starter/*");

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

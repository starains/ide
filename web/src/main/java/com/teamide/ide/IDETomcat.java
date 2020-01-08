package com.teamide.ide;

import java.io.File;
import java.net.URL;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.WebResourceRoot.ResourceSetType;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.JreMemoryLeakPreventionListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.ThreadLocalLeakPreventionListener;
import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;

import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.controller.RootController;
import com.teamide.util.StringUtil;

public class IDETomcat {

	private final com.teamide.ide.constant.IDEConf.Server server;

	private Tomcat tomcat;

	public IDETomcat(com.teamide.ide.constant.IDEConf.Server server) {
		this.server = server;
	}

	public Tomcat startTomcat() throws Exception {
		tomcat = new Tomcat();// 创建tomcat实例，用来启动tomcat

		if (StringUtil.isEmpty(server.getHostname())) {
			tomcat.setHostname("localhost");// 设置主机名
		} else {
			tomcat.setHostname(server.getHostname());// 设置主机名
		}

		File TOMCAT_FOLDER = new File(IDEConstant.WORKSPACE_TOMCAT_FOLDER);

		tomcat.setBaseDir(TOMCAT_FOLDER.toURI().getPath());// tomcat存储自身信息的目录，比如日志等信息，根目录
		tomcat.setPort(Integer.valueOf(server.getPort()));

		StandardHost host = (StandardHost) tomcat.getHost();
		// if (!new File(root, "webapps").exists()) {
		// new File(root, "webapps").mkdirs();
		// }
		//
		host.setWorkDir("work");

		// String DEFAULT_PROTOCOL =
		// "org.apache.coyote.http11.Http11NioProtocol";

		Connector connector = tomcat.getConnector();// 设置协议，默认就是这个协议
		connector.setURIEncoding("UTF-8");// 设置编码
		// connector.setPort(port);

		String contextpath = server.getContextPath();
		if (StringUtil.isEmpty(contextpath)) {
			contextpath = "/";
		}
		if (!contextpath.startsWith("/")) {
			contextpath = "/" + contextpath;
		}
		if (contextpath.equals("/")) {
			contextpath = "";
		}
		StandardContext context = (StandardContext) tomcat.addContext(contextpath, null);

		context.setSessionCookieName("session-cookie-name-" + contextpath.replaceAll("/", ""));

		// context.setReloadable(true);
		Class<?> rootClass = RootController.class;

		context.addLifecycleListener(new LifecycleListener() {
			public void lifecycleEvent(LifecycleEvent event) {

				if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
					context.getResources().setCachingAllowed(false);
					URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
					// System.out.println(location.getProtocol());
					// System.out.println(location);
					context.getResources().createWebResourceSet(ResourceSetType.RESOURCE_JAR, "/", location,
							"/META-INF/resources");

					context.getServletContext().addServlet(rootClass.getName(), RootController.class);
					context.addServletMappingDecoded("/*", rootClass.getName());
				}
			}
		});
		// tomcat.addServlet(contextpath, rootClass.getName(),
		// rootClass.getName());
		// context.addServletMappingDecoded("/*", rootClass.getName());

		// context.getResources().setCachingAllowed(false);

		// server.addLifecycleListener(new Tomcat.DefaultWebXmlListener());
		tomcat.getServer().addLifecycleListener(new FixContextListener());
		tomcat.getServer().addLifecycleListener(new AprLifecycleListener());
		tomcat.getServer().addLifecycleListener(new JreMemoryLeakPreventionListener());
		tomcat.getServer().addLifecycleListener(new GlobalResourcesLifecycleListener());
		tomcat.getServer().addLifecycleListener(new ThreadLocalLeakPreventionListener());

		tomcat.start();// 启动tomcat

		tomcat.getServer().await();
		return tomcat;
	}

	public void stopTomcat() {
		System.out.println("stop tomcat");
		try {

			tomcat.stop();
			tomcat.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

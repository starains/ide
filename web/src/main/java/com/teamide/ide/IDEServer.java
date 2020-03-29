package com.teamide.ide;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

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
import org.apache.tomcat.websocket.server.WsSci;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.constant.IDEConf.Server;
import com.teamide.ide.controller.RootController;
import com.teamide.ide.listener.SessionListener;
import com.teamide.ide.websocket.WebSocketServer;
import com.teamide.util.StringUtil;

public class IDEServer implements Runnable {

	private final String[] args;

	private final JSONObject param = new JSONObject();

	private final Server server;
	private final Tomcat tomcat;

	public IDEServer(String[] args) {
		this.args = args;
		resolveParam(this.args);
		if (StringUtil.isNotEmpty(param.getString("TEAMIDE_HOME"))) {
			System.setProperty("TEAMIDE_HOME", param.getString("TEAMIDE_HOME"));
		}
		server = IDEConstant.CONF.getServer();
		if (StringUtil.isNotEmpty(param.getString("port"))) {
			server.setPort(param.getString("port"));
		}
		tomcat = new Tomcat();
	}

	@Override
	public void run() {
		System.out.println("TEAMIDE_HOME:" + IDEConstant.HOME);
		System.out.println("server:" + JSON.toJSONString(server));

		try {
			startTomcat();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startTomcat() throws Exception {
		System.out.println("start tomcat");

		if (StringUtil.isEmpty(server.getHostname())) {
			tomcat.setHostname("localhost");// 设置主机名
		} else {
			tomcat.setHostname(server.getHostname());// 设置主机名
		}

		File TOMCAT_FOLDER = new File(IDEConstant.WORKSPACES_TOMCAT_FOLDER);

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

					context.getServletContext().addListener(new ApplicationInitializer());
					context.getServletContext().addListener(new SessionListener());

					URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
					// System.out.println(location.getProtocol());
					// System.out.println(location);
					context.getResources().createWebResourceSet(ResourceSetType.RESOURCE_JAR, "/", location,
							"/META-INF/resources");

					context.getServletContext().addServlet(rootClass.getName(), RootController.class);
					context.addServletMappingDecoded("/*", rootClass.getName());

					Set<Class<?>> clazzes = new HashSet<Class<?>>();
					clazzes.add(WebSocketServer.class);
					try {
						new WsSci().onStartup(clazzes, context.getServletContext());
					} catch (ServletException e) {
						e.printStackTrace();
					}

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

	private void resolveParam(String[] args) {
		if (args == null) {
			return;
		}
		for (String arg : args) {
			if (arg == null || !arg.startsWith("--")) {
				continue;
			}
			arg = arg.substring(2);
			String name = arg;
			String value = "";
			int index = arg.indexOf("=");
			if (index > 0) {
				name = arg.substring(0, index);
				if (arg.length() > index + 1) {
					value = arg.substring(index + 1);
					if (value.startsWith("\"") && value.endsWith("\"")) {
						value = value.substring(1, value.length() - 1);
					} else if (value.startsWith("'") && value.endsWith("'")) {
						value = value.substring(1, value.length() - 1);
					}
				}
			}
			param.put(name, value);
		}

	}

}

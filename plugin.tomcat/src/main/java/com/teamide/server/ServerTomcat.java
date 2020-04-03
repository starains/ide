package com.teamide.server;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

public class ServerTomcat {

	public final String root;

	private final int port;

	private final String hostname;

	private final String contextpath;

	private final String app;

	private final Tomcat tomcat;

	public ServerTomcat(String root, String hostname, int port, String app, String contextpath) {
		this.root = root;
		if (hostname == null || hostname.trim().length() == 0) {
			hostname = "localhost";
		}
		this.hostname = hostname;
		this.port = port;
		if (contextpath != null) {
			if (!contextpath.startsWith("/")) {
				contextpath = "/" + contextpath;
			}
		} else {
			contextpath = "";
		}

		this.contextpath = contextpath;
		this.app = app;
		this.tomcat = createTomcat();
	}

	private Tomcat createTomcat() {
		Tomcat tomcat = new Tomcat();// 创建tomcat实例，用来启动tomcat

		if (hostname == null || hostname.trim().length() == 0) {
			tomcat.setHostname("localhost");// 设置主机名
		} else {
			tomcat.setHostname(hostname);// 设置主机名
		}

		tomcat.setBaseDir(root);// tomcat存储自身信息的目录，比如日志等信息，根目录
		tomcat.setPort(port);

		StandardHost host = (StandardHost) tomcat.getHost();
		if (!new File(root, "webapps").exists()) {
			new File(root, "webapps").mkdirs();
		}

		host.setAppBase(new File(root, "webapps").toURI().getPath());
		host.setWorkDir(new File(root, "work").toURI().getPath());

		// String DEFAULT_PROTOCOL =
		// "org.apache.coyote.http11.Http11NioProtocol";

		Connector connector = tomcat.getConnector();// 设置协议，默认就是这个协议
		connector.setURIEncoding("UTF-8");// 设置编码
		// connector.setPort(port);

		try {
			tomcat.start();// 启动tomcat
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		final Server server = tomcat.getServer();

		// server.addLifecycleListener(new Tomcat.DefaultWebXmlListener());
		// 保证已经配置好了。
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Class<?> clazz = loader
					.loadClass("org.apache.catalina.startup.Tomcat.FixContextListener.FixContextListener");
			if (clazz != null) {
				server.addLifecycleListener((LifecycleListener) clazz.newInstance());
			}
		} catch (Exception e) {

		}
		try {
			Class<?> clazz = loader.loadClass("org.apache.catalina.core.AprLifecycleListener.AprLifecycleListener");
			if (clazz != null) {
				server.addLifecycleListener((LifecycleListener) clazz.newInstance());
			}
		} catch (Exception e) {

		}
		try {
			Class<?> clazz = loader.loadClass(
					"org.apache.catalina.core.JreMemoryLeakPreventionListener.JreMemoryLeakPreventionListener");
			if (clazz != null) {
				server.addLifecycleListener((LifecycleListener) clazz.newInstance());
			}
		} catch (Exception e) {

		}
		try {
			Class<?> clazz = loader.loadClass(
					"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener.GlobalResourcesLifecycleListener");
			if (clazz != null) {
				server.addLifecycleListener((LifecycleListener) clazz.newInstance());
			}
		} catch (Exception e) {

		}
		try {
			Class<?> clazz = loader.loadClass(
					"org.apache.catalina.core.ThreadLocalLeakPreventionListener.ThreadLocalLeakPreventionListener");
			if (clazz != null) {
				server.addLifecycleListener((LifecycleListener) clazz.newInstance());
			}
		} catch (Exception e) {

		}

		new Thread() {

			@Override
			public void run() {
				try {
					server.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
		return tomcat;
	}

	public void startTomcat() {
		System.out.println("start tomcat root:" + root);

	}

	public void stopTomcat() {
		System.out.println("stop tomcat root:" + root);
		try {

			tomcat.stop();
			tomcat.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private Context context;

	public void startApp() {
		System.out.println("start app:" + app);
		System.out.println("start app contextpath:" + contextpath);
		if (app == null || app.trim().length() == 0) {
			System.err.println("app is null.");
		}
		if (!new File(app).exists()) {
			System.err.println("app[" + app + "] is not exists.");
			return;
		}
		context = tomcat.addWebapp(contextpath, app);
		context.setSessionCookieName("session-cookie-name-" + contextpath.replaceAll("/", ""));

		context.setReloadable(true);

		try {

			Object resources = context.getResources();
			Method method = resources.getClass().getMethod("setCachingAllowed", boolean.class);
			if (method != null) {
				method.invoke(resources, false);
			}
		} catch (NoSuchMethodError e) {

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			context.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopApp() {
		System.out.println("stop app:" + app);
		System.out.println("stop app contextpath:" + contextpath);
		System.out.println("stop app context:" + context);
		if (context != null) {
			try {
				context.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				context.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void restartApp() {
		stopApp();
		startApp();
	}

	public void updateApp() {

	}

}

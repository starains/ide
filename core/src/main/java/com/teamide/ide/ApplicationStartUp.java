//package com.teamide.ide;
//
//import java.io.File;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.net.URL;
//import java.util.Properties;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.web.server.ErrorPage;
//import org.springframework.boot.web.server.ErrorPageRegistrar;
//import org.springframework.boot.web.server.ErrorPageRegistry;
//import org.springframework.boot.web.servlet.ServletComponentScan;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
//
//import com.teamide.ide.IDEShare;
//import com.teamide.ide.configure.IDEOptions;
//import com.teamide.ide.constant.IDEConstant;
//
//@SpringBootApplication
//@ServletComponentScan({ "com.coospro.ide", "com.coospro.web", "com.coospro.app" })
//public class ApplicationStartUp {
//
//	static Class<?> APPLICATION_CLASS = ApplicationStartUp.class;
//
//	public static void main(String[] args) {
//		boolean isStop = false;
//		if (args != null) {
//			for (String arg : args) {
//				if (arg == null) {
//					continue;
//				}
//				if (arg.equalsIgnoreCase("STOP")) {
//					isStop = true;
//				}
//			}
//		}
//		if (isStop) {
//			stop();
//		} else {
//			start();
//		}
//	}
//
//	public static void stop() {
//		Object context = IDEShare.get("IDE-APPLICATION-CONTEXT");
//		if (context != null) {
//			try {
//				Method method = context.getClass().getMethod("close", new Class[] {});
//				method.invoke(context, new Object[] {});
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public static void start() {
//		try {
//			final Field factoryField = URL.class.getDeclaredField("factory");
//			factoryField.setAccessible(true);
//			final Field lockField = URL.class.getDeclaredField("streamHandlerLock");
//			lockField.setAccessible(true);
//
//			factoryField.set(null, null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		SpringApplication application = new SpringApplication(APPLICATION_CLASS);
//		Properties properties = IDEOptions.loadConfig();
//		if (properties == null) {
//			properties = new Properties();
//		}
//		if (properties.getProperty("server.port") == null) {
//			properties.setProperty("server.port", "19000");
//			properties.setProperty("spring.devtools.restart.exclude", "**/*.js,**/*.css,**/*.html");
//		}
//		if (properties.getProperty("server.servlet.context-path") == null) {
//			properties.setProperty("server.servlet.context-path", "/");
//		}
//		if (properties.getProperty("server.tomcat.compression") == null) {
//			properties.setProperty("server.tomcat.compression", "on");
//		}
//
//		if (properties.getProperty("spring.application.name") == null) {
//			properties.setProperty("spring.application.name", "ide");
//		}
//		if (properties.getProperty("server.servlet.session.timeout") == null) {
//			properties.setProperty("server.servlet.session.timeout", "7200");
//		}
//		String path = IDEConstant.IDE_STATIC_FOLDER;
//		try {
//			path = new File(path).toURI().toURL().toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		properties.setProperty("spring.resources.static-locations", path + ",classpath:/static/");
//		application.setDefaultProperties(properties);
//		ConfigurableApplicationContext applicationContext = application.run(new String[] {});
//		IDEShare.put("IDE-APPLICATION-CONTEXT", applicationContext);
//	}
//
//	@Bean
//	public ErrorPageRegistrar errorPageRegistrar() {
//
//		return new ErrorPageRegistrar() {
//
//			@Override
//			public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
//
//				// 1、按错误的类型显示错误的网页
//				// 错误类型为404，找不到网页的，默认显示404.html网页
//				errorPageRegistry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/"));
//				// 错误类型为500，表示服务器响应错误，默认显示500.html网页
//				// errorPageRegistry.addErrorPages(new
//				// ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
//			}
//		};
//	}
//
//}

package com.teamide.ide.controller.handler;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.ide.plugin.IDEPlugin;
import com.teamide.ide.plugin.IDEResource;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.util.IOUtil;
import com.teamide.util.RequestUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

public class ResourcesHandler {

	private static Boolean isDev;

	private static final long VERSION = System.currentTimeMillis();

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {

		if (isDev == null) {
			File file = new File("ide.dev.properties");
			if (file.exists()) {
				isDev = true;
			} else {
				isDev = false;
			}
		}

		if (path.endsWith("/ide.root.js")) {
			List<String> js = new ArrayList<String>();

			List<String> css = new ArrayList<String>();

			StringBuffer content = new StringBuffer();
			String contextPath = request.getContextPath();
			if (StringUtil.isEmpty(contextPath)) {
				contextPath = "/";
			}
			if (!contextPath.startsWith("/")) {
				contextPath = "/" + contextPath;
			}
			if (!contextPath.endsWith("/")) {
				contextPath = contextPath + "/";
			}
			content.append("var _ROOT_URL = \"" + contextPath + "\";\n");
			content.append("var _SERVER_URL = \"" + contextPath + "\";\n");

			writelnCSS(request, content, css);
			writelnJS(request, content, js);
			ResponseUtil.outJS(response, content.toString());
			return;
		} else if (path.endsWith("/ide.resources.js")) {
			List<String> js = new ArrayList<String>();
			List<String> css = new ArrayList<String>();

			css.add("resources/font/iconfont.css");
			css.add("resources/plugins/element-ui/index.css");

			js.add("resources/plugins/vue/vue.min.js");
			js.add("resources/plugins/element-ui/index.js");

			js.add("resources/plugins/axios/axios.min.js");

			js.add("resources/plugins/jquery/jquery.js");

			js.add("resources/plugins/coos/coos.min.js?v=" + VERSION);

			List<IDEPlugin> plugins = PluginHandler.getPlugins();

			for (IDEPlugin plugin : plugins) {
				if (plugin == null || plugin.getResources() == null) {
					continue;
				}
				List<IDEResource> resources = plugin.getResources();
				for (IDEResource resource : resources) {
					if (resource == null || resource.getType() == null || StringUtil.isEmpty(resource.getName())) {
						continue;
					}
					String url = PluginResourcesHandler.PATH_PREFIX;
					url += plugin.getName() + "/" + plugin.getVersion() + "/";
					url += resource.getName();
					if (isDev) {
						url += "?isDev=true";
					} else {
						url += "?v=" + VERSION;
					}
					switch (resource.getType()) {
					case CSS:
						css.add(url);
						break;
					case JS:
						js.add(url);
						break;
					}
				}
			}
			StringBuffer content = new StringBuffer();
			writelnCSS(request, content, css);
			writelnJS(request, content, js);
			ResponseUtil.outJS(response, content.toString());
			return;
		} else if (path.endsWith("/ide.main.js")) {
			List<String> js = new ArrayList<String>();

			List<String> css = new ArrayList<String>();

			StringBuffer content = new StringBuffer();
			content.append("ide_main();\n");
			writelnCSS(request, content, css);
			writelnJS(request, content, js);
			ResponseUtil.outJS(response, content.toString());
			return;
		} else {
			InputStream stream = request.getServletContext().getResourceAsStream(path);
			if (stream != null) {
				try {

					if (path.endsWith(".js")) {
						ResponseUtil.outJS(response, IOUtil.readString(stream));
					} else if (path.endsWith(".css")) {
						ResponseUtil.outCSS(response, IOUtil.readString(stream));
					} else if (path.endsWith(".html")) {
						ResponseUtil.outHTML(response, IOUtil.readString(stream));
					} else {

						OutputStream outputStream = null;
						response.setCharacterEncoding("UTF-8");
						try {
							outputStream = response.getOutputStream();
							outputStream.write(IOUtil.read(stream));
							outputStream.flush();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							IOUtil.close(outputStream);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void writelnCSS(HttpServletRequest request, StringBuffer content, List<String> css) {

		if (css != null) {
			for (String path : css) {
				if (StringUtil.isEmpty(path)) {
					continue;
				}
				content.append("document.writeln(\"<link rel='stylesheet' type='text/css' href='");
				content.append(RequestUtil.getContextPathURL(request, path));
				content.append("' />\");");
				content.append("\n");
			}
		}
	}

	public static void writelnJS(HttpServletRequest request, StringBuffer content, List<String> js) {

		if (js != null) {
			for (String path : js) {
				if (StringUtil.isEmpty(path)) {
					continue;
				}
				content.append("document.writeln(\"<script type='text/javascript' src='");
				content.append(RequestUtil.getContextPathURL(request, path));
				content.append("' ></script>\");");
				content.append("\n");
			}
		}
	}

}

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

	private static Boolean hasUI;

	private static final long VERSION = System.currentTimeMillis();

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {

		if (hasUI == null) {
			File file = new File("ui/coos.ui.properties");
			if (file.exists()) {
				hasUI = true;
			} else {
				hasUI = false;
			}
		}

		if (path.endsWith("/app.root.js")) {
			List<String> js = new ArrayList<String>();

			List<String> css = new ArrayList<String>();

			StringBuffer content = new StringBuffer();
			content.append("var _ROOT_URL = \"" + RequestUtil.getServerUrl(request) + "\";\n");
			content.append("var _SERVER_URL = \"" + RequestUtil.getServerUrl(request) + "\";\n");

			writelnCSS(request, content, css);
			writelnJS(request, content, js);
			ResponseUtil.outJS(response, content.toString());
			return;
		} else if (path.endsWith("/app.resources.js")) {
			List<String> js = new ArrayList<String>();
			List<String> css = new ArrayList<String>();

			css.add("resources/coos/font/iconfont.css");
			css.add("resources/plugins/element-ui/2.12.0/index.css");

			css.add("resources/plugins/codemirror/lib/codemirror.css");
			css.add("resources/plugins/codemirror/theme/lesser-dark.css");

			js.add("resources/plugins/vue/vue.min.js");
			js.add("resources/plugins/element-ui/2.12.0/index.js");

			// keymap
			js.add("resources/plugins/codemirror/lib/codemirror.js");
			js.add("resources/plugins/codemirror/keymap/sublime.js");
			// js.add("resources/plugins/codemirror/keymap/vim.js");
			// js.add("resources/plugins/codemirror/keymap/emacs.js");

			// addon
			css.add("resources/plugins/codemirror/addon/hint/show-hint.css");
			js.add("resources/plugins/codemirror/addon/hint/show-hint.js");
			js.add("resources/plugins/codemirror/addon/hint/anyword-hint.js");
			js.add("resources/plugins/codemirror/addon/hint/css-hint.js");
			js.add("resources/plugins/codemirror/addon/hint/html-hint.js");
			js.add("resources/plugins/codemirror/addon/hint/javascript-hint.js");
			js.add("resources/plugins/codemirror/addon/hint/sql-hint.js");
			js.add("resources/plugins/codemirror/addon/hint/xml-hint.js");
			js.add("resources/plugins/codemirror/addon/comment/comment.js");
			js.add("resources/plugins/codemirror/addon/mode/overlay.js");
			js.add("resources/plugins/codemirror/addon/mode/simple.js");
			js.add("resources/plugins/codemirror/addon/selection/selection-pointer.js");
			// mode
			js.add("resources/plugins/codemirror/mode/css/css.js");
			js.add("resources/plugins/codemirror/mode/javascript/javascript.js");
			js.add("resources/plugins/codemirror/mode/htmlmixed/htmlmixed.js");
			js.add("resources/plugins/codemirror/mode/xml/xml.js");
			js.add("resources/plugins/codemirror/mode/clike/clike.js");
			js.add("resources/plugins/codemirror/mode/properties/properties.js");
			js.add("resources/plugins/codemirror/mode/markdown/markdown.js");
			js.add("resources/plugins/codemirror/mode/yaml/yaml.js");
			js.add("resources/plugins/codemirror/mode/vue/vue.js");
			js.add("resources/plugins/codemirror/mode/sql/sql.js");
			js.add("resources/plugins/codemirror/mode/shell/shell.js");
			js.add("resources/plugins/codemirror/mode/sass/sass.js");
			js.add("resources/plugins/codemirror/mode/python/python.js");
			js.add("resources/plugins/codemirror/mode/powershell/powershell.js");
			js.add("resources/plugins/codemirror/mode/php/php.js");
			js.add("resources/plugins/codemirror/mode/nginx/nginx.js");
			js.add("resources/plugins/codemirror/mode/http/http.js");
			js.add("resources/plugins/codemirror/mode/nginx/nginx.js");
			js.add("resources/plugins/codemirror/mode/go/go.js");

			js.add("resources/plugins/jsplumb/jsplumb.min.js");

			js.add("resources/plugins/jquery/jquery.js");
			css.add("resources/plugins/jquery/jquery-ui.css");
			js.add("resources/plugins/jquery/jquery-ui.js");

			css.add("resources/plugins/dropzone/basic.min.css");
			css.add("resources/plugins/dropzone/dropzone.min.css");
			js.add("resources/plugins/dropzone/dropzone.min.js");
			js.add("resources/plugins/dropzone/dropzone-amd-module.min.js");

			if (hasUI) {
				// css.add("resources/coos/merge/coos.css?v=" + VERSION);
				js.add("resources/coos/merge/coos.js");
				// css.add("resources/coos/merge/coos.layout.css?v=" + VERSION);
				// js.add("resources/coos/merge/coos.layout.js?v=" + VERSION);
			} else {
				// css.add("resources/coos/css/coos.css?v=" + VERSION);
				js.add("resources/coos/js/coos.js?v=" + VERSION);
				// css.add("resources/coos/css/coos.layout.css?v=" + VERSION);
				// js.add("resources/coos/js/coos.layout.js?v=" + VERSION);
			}

			List<IDEPlugin> plugins = PluginHandler.getPlugins();

			if (hasUI) {
				for (IDEPlugin plugin : plugins) {
					if (plugin == null || plugin.getResources() == null) {
						continue;
					}
					css.add("resources/plugin/merge/" + plugin.getName() + "/" + plugin.getVersion() + "/index.css");
					js.add("resources/plugin/merge/" + plugin.getName() + "/" + plugin.getVersion() + "/index.js");

				}
			} else {
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
						url += "?v=" + VERSION;
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
			}
			StringBuffer content = new StringBuffer();
			writelnCSS(request, content, css);
			writelnJS(request, content, js);
			ResponseUtil.outJS(response, content.toString());
			return;
		} else if (path.endsWith("/app.main.js")) {
			List<String> js = new ArrayList<String>();

			List<String> css = new ArrayList<String>();

			StringBuffer content = new StringBuffer();
			content.append("app_main();\n");
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

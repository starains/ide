package com.teamide.app.generater;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.Bean;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.plugin.AppBean;
import com.teamide.app.template.TemplateResolver;
import com.teamide.app.template.context.TemplateContext;
import com.teamide.util.FileUtil;
import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;

public abstract class BaseGenerater extends Generater {

	public final Bean bean;

	public BaseGenerater(Bean bean, File sourceFolder, AppBean app, AppContext context) {
		super(sourceFolder, app, context);
		this.bean = bean;

	}

	public File getFile() {

		File javaFolder = getJavaFolder();
		String pack = getCodePackage();
		String className = getClassName();
		String filePath = getFilePath(pack, className);
		File file = new File(javaFolder, filePath);
		return file;
	}

	public String getCodePackage() {

		String pack = getPackage();
		String codepath = getCodePath();
		String codepackage = getFolderPackage(codepath);
		if (!StringUtil.isEmpty(codepackage)) {
			pack += "." + codepackage;
		}
		return pack;
	}

	public String getFilePath(String pack, String name) {
		String path = packageToPath(pack);
		String filePath = path + "/" + name + ".java";
		return filePath;
	}

	public void init() {

		if (bean != null) {
			JSONObject json = (JSONObject) JSON.toJSON(bean);
			for (String key : json.keySet()) {
				data.put("$" + key, json.get(key));
			}
		}

		String pack = getCodePackage();
		data.put("$only_content", false);
		data.put("$package", pack);
		data.put("$classname", getClassName());
		data.put("$result_classname", "JSONObject");
		data.put("$propertyname", getPropertyname());

		String mergePackage = getMergePackage();
		String mergeClassname = getMergeClassName();
		if (!StringUtil.isEmpty(mergeClassname)) {
			data.put("$merge_package", mergePackage);
			data.put("$merge_classname", mergeClassname);
			data.put("$merge_propertyname", getMergePropertyname());

		}

		initBaseData();

		buildData();
	}

	public String getPropertyname() {
		String className = getClassName();
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}

	public String getMergePropertyname() {
		String mergeClassname = getMergeClassName();
		if (StringUtil.isEmpty(mergeClassname)) {
			return "";
		}
		return mergeClassname.substring(0, 1).toLowerCase() + mergeClassname.substring(1);
	}

	public String getMergePackage() {
		String pack = getPackage();
		if (bean != null) {
			String name = bean.getName();
			String folder = "base";
			if (name.indexOf("/") > 0) {
				folder = getFolderByName(name);
			}
			String codepackage = getFolderPackage(folder);
			if (!StringUtil.isEmpty(codepackage)) {
				pack += "." + codepackage;
			}
		}
		return pack;
	}

	public String getMergeClassName() {
		String classname = "";
		if (bean != null) {
			String name = bean.getName();
			String folder = "base";
			if (name.indexOf("/") > 0) {
				folder = getFolderByName(name);
			}
			String[] chars = folder.split("");
			String result = "";
			for (int i = 0; i < chars.length; i++) {
				if (chars[i].equals("/") || chars[i].equals("\\")) {
					continue;
				}
				if (i == 0) {
					result += chars[i].toUpperCase();
				} else {
					if (chars[i - 1].equals("/") || chars[i - 1].equals("\\")) {
						result += chars[i].toUpperCase();
					} else {
						result += chars[i];
					}
				}
			}
			classname = result;
			if (bean instanceof DaoBean) {
				classname += "Dao";
			}
			if (bean instanceof ServiceBean) {
				classname += "Service";
			}
		}

		return classname;
	}

	public void generate() throws Exception {
		// param.getLog().info("generate " + this.pack + "." + this.className +
		// " code.");

		init();
		String content = build();
		File file = getFile();
		if (!file.exists() && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		StringBuffer code = new StringBuffer();
		if (file.getName().endsWith(".xml")) {
			code.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append("\n");
			code.append("<!-- " + Generater.HEAD_NOTE + " -->").append("\n");
			code.append("<!-- " + Generater.HEAD_REMARK + " -->").append("\n");
		} else {
			code.append("/* " + Generater.HEAD_NOTE + " */").append("\n");
			code.append("/** " + Generater.HEAD_REMARK + " **/").append("\n");
		}
		code.append(content);

		FileUtil.write(code.toString().getBytes(), file);
	}

	public String build() throws Exception {
		String className = getClassName();
		String pack = getCodePackage();
		// param.getLog().info("build " + this.pack + "." + this.className + "
		// code.");
		String template = getTemplate();
		InputStream templateStream = loadTemplate(template);
		if (templateStream == null) {
			throw new Exception("build " + pack + "." + className + " error. template [" + template + "] is null.");
		}
		StringReader reader = new StringReader(IOUtil.readString(templateStream));
		TemplateResolver resolver = new TemplateResolver(reader);
		TemplateContext context = resolver.resolve();
		StringWriter writer = new StringWriter();
		try {
			context.write(writer, data);
		} catch (Exception e) {
			if (bean != null) {
				throw new Exception(bean.getName() + " build error," + e.getMessage());
			} else {
				throw new Exception(template + " build error," + e.getMessage());
			}
		}
		return writer.toString();
	}

	public abstract void buildData();

	public abstract String getTemplate() throws Exception;

	public abstract String getPackage();

	public String getCodePath() {
		String path = "";
		if (bean == null) {
			return path;
		}
		if (bean.getName().lastIndexOf("/") > 0) {
			path = bean.getName().substring(0, bean.getName().lastIndexOf("/"));
		}
		return path;
	}

	public String getClassName() {
		if (bean == null) {
			return null;
		}
		String classname = bean.getClassname();

		if (StringUtil.isEmpty(classname)) {
			String name = bean.getName();
			if (name.indexOf(".") >= 0) {
				name = name.substring(0, name.indexOf("."));
			}
			String[] chars = name.split("");
			String result = "";
			for (int i = 0; i < chars.length; i++) {
				if (chars[i].equals("/") || chars[i].equals("\\")) {
					continue;
				}
				if (i == 0) {
					result += chars[i].toUpperCase();
				} else {
					if (chars[i - 1].equals("/") || chars[i - 1].equals("\\")) {
						result += chars[i].toUpperCase();
					} else {
						result += chars[i];
					}
				}
			}
			classname = result;

			if (bean instanceof DaoBean) {
				classname += "Dao";
			}
			if (bean instanceof ServiceBean) {
				classname += "Service";
			}
		}

		return classname;
	}

}

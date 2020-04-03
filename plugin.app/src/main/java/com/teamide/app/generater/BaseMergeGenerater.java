package com.teamide.app.generater;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.generater.dao.DaoGenerater;
import com.teamide.app.generater.dictionary.DictionaryGenerater;
import com.teamide.app.generater.service.ServiceGenerater;
import com.teamide.app.plugin.AppBean;
import com.teamide.app.template.TemplateResolver;
import com.teamide.app.template.context.TemplateContext;
import com.teamide.util.FileUtil;
import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;

public abstract class BaseMergeGenerater extends Generater {

	public final String pack;

	public final String className;

	public final File file;

	public final String directory;

	public final List<?> beans;

	public BaseMergeGenerater(String directory, List<?> beans, File sourceFolder, AppBean app, AppContext context) {
		super(sourceFolder, app, context);
		this.directory = directory;
		this.beans = beans;

		String className = getClassName();
		String pack = getCodePackage();

		this.pack = pack;
		this.className = className;
		this.file = getFile();

	}

	public File getFile() {
		File javaFolder = getJavaFolder();
		String className = getClassName();
		String pack = getCodePackage();
		String filePath = getFilePath(pack, className);
		File file = new File(javaFolder, filePath);
		return file;
	}

	public String getCodePackage() {

		String pack = getPackage();
		String codepackage = getFolderPackage(directory);
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

		JSONArray $datas = new JSONArray();

		if (beans != null) {
			for (Object one : beans) {
				if (one instanceof DaoBean) {
					DaoGenerater generater = new DaoGenerater((DaoBean) one, sourceFolder, app, context);
					generater.init();
					$datas.add(generater.data);
				} else if (one instanceof ServiceBean) {
					ServiceGenerater generater = new ServiceGenerater((ServiceBean) one, sourceFolder, app, context);
					generater.init();
					$datas.add(generater.data);
				} else if (one instanceof DictionaryBean) {
					DictionaryGenerater generater = new DictionaryGenerater((DictionaryBean) one, sourceFolder, app,
							context);
					generater.init();
					$datas.add(generater.data);
				}
			}
		}
		data.put("$only_content", false);
		data.put("$datas", $datas);
		data.put("$package", this.pack);
		data.put("$classname", this.className);
		data.put("$propertyname", this.className.substring(0, 1).toLowerCase() + this.className.substring(1));

		initBaseData();

		buildData();
	}

	public void generate() throws Exception {
		if (beans == null || beans.size() == 0) {
			return;
		}
		// param.getLog().info("generate " + this.pack + "." + this.className +
		// " code.");
		init();
		String content = build();
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
		// param.getLog().info("build " + this.pack + "." + this.className + "
		// code.");
		String template = getTemplate();
		InputStream templateStream = loadTemplate(template);
		if (templateStream == null) {
			throw new Exception(
					"build " + this.pack + "." + this.className + " error. template [" + template + "] is null.");
		}
		StringReader reader = new StringReader(IOUtil.readString(templateStream));
		TemplateResolver resolver = new TemplateResolver(reader);
		TemplateContext context = resolver.resolve();
		StringWriter writer = new StringWriter();
		try {
			context.write(writer, data);
		} catch (Exception e) {
			throw new Exception(template + " build error," + e.getMessage());
		}
		return writer.toString();
	}

	public abstract void buildData();

	public abstract String getTemplate() throws Exception;

	public abstract String getPackage();

	public String getClassName() {
		String classname = "";

		String name = directory;
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

		if (beans.size() > 0) {
			if (beans.get(0) instanceof DaoBean) {
				classname += "Dao";
			}
			if (beans.get(0) instanceof ServiceBean) {
				classname += "Service";
			}
		}

		return classname;
	}

}

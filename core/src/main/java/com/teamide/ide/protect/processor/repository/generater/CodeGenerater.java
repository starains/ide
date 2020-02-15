package com.teamide.ide.protect.processor.repository.generater;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.Bean;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.FileUtil;
import com.teamide.util.IOUtil;
import com.teamide.util.ResourceUtil;
import com.teamide.util.StringUtil;
import com.teamide.template.TemplateResolver;
import com.teamide.template.context.TemplateContext;

public abstract class CodeGenerater extends Generater {

	public final String pack;

	public final String className;

	public final File file;

	protected final Bean bean;

	protected final JSONObject data = new JSONObject();

	public CodeGenerater(Bean bean, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(param, app, context);
		this.bean = bean;

		File javaFolder = getJavaFolder();

		String pack = getPackage();
		String codepath = getCodePath();
		String className = getClassName();
		if (!StringUtil.isEmpty(codepath)) {
			pack += "." + codepath.replaceAll("/", ".");
		}
		String path = packageToPath(pack);
		String filePath = path + "/" + className + ".java";
		File file = new File(javaFolder, filePath);

		this.pack = pack;
		this.className = className;
		this.file = file;

	}

	public void init() {

		if (bean != null) {
			JSONObject json = (JSONObject) JSON.toJSON(bean);
			for (String key : json.keySet()) {
				data.put("$" + key, json.get(key));
			}
		}
		data.put("$package", this.pack);
		data.put("$classname", this.className);
		data.put("$result_classname", "JSONObject");
		data.put("$propertyname", this.className.substring(0, 1).toLowerCase() + this.className.substring(1));

		JSONObject $app_factory = new JSONObject();

		$app_factory.put("$package", getAppFactoryPackage());
		$app_factory.put("$classname", getAppFactoryClassname());
		data.put("$app_factory", $app_factory);
		data.put("$imports", imports);
		data.put("$usespringannotation", isUsespringannotation());
		buildData();
	}

	protected List<String> imports = new ArrayList<String>();

	public void generate() throws Exception {
		param.getLog().info("generate " + this.pack + "." + this.className + " code.");
		String content = build();
		if (!file.exists() && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		FileUtil.write(content.getBytes(), file);
	}

	public String build() throws Exception {
		param.getLog().info("build " + this.pack + "." + this.className + " code.");
		init();
		String template = getTemplate();
		InputStream templateStream = ResourceUtil.load(template);
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
			if (bean instanceof DictionaryBean) {
				classname += "Dictionary";
			}
		}

		return classname;
	}

}

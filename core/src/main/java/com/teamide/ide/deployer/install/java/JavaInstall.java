package com.teamide.ide.deployer.install.java;

import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Model;

import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.deployer.DeployInstall;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.ide.maven.MavenUtil;
import com.teamide.ide.processor.repository.hanlder.RepositoryHanlder;
import com.teamide.ide.service.impl.EnvironmentService;

public abstract class JavaInstall extends DeployInstall {

	private final String java_home;

	private final String maven_home;

	protected File libFolder;

	protected File classesFolder;

	protected File jarFile;

	protected File warFile;

	protected File warFolder;

	public JavaInstall(DeployParam param) {
		super(param);
		String java_home = null;
		String maven_home = null;
		if (!StringUtil.isEmpty(param.option.getJavaenvironmentid())) {
			try {
				EnvironmentBean environment = new EnvironmentService().get(param.option.getJavaenvironmentid());
				if (environment != null) {
					java_home = environment.getPath();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!StringUtil.isEmpty(param.option.getMavenenvironmentid())) {
			try {
				EnvironmentBean environment = new EnvironmentService().get(param.option.getMavenenvironmentid());
				if (environment != null) {
					maven_home = environment.getPath();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.java_home = param.starter.formatToRoot(java_home);
		this.maven_home = param.starter.formatToRoot(maven_home);

		try {
			param.starter.starterJSON.put("java_home", java_home);
			param.starter.starterJSON.put("maven_home", maven_home);
			FileUtil.write(param.starter.starterJSON.toJSONString().getBytes(), param.starter.starterJSONFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getJavaEnvp() {
		return param.option.getJavaenvp();
	}

	public String getMavenEnvp() {
		return param.option.getMavenenvp();
	}

	public String getContextpath() {
		return param.option.getContextpath();
	}

	public String getJavaHome() {
		return java_home;
	}

	public String getMavenHome() {
		return maven_home;
	}

	public void compile() throws Exception {
		Model model = RepositoryHanlder.getPomModel(new File(param.projectFolder, "pom.xml"));
		if (model != null) {
			mavenCompileAndPackage(model);
		} else {
			classCompile();
		}

	}

	protected boolean mavenCompileAndPackage(Model model) throws Exception {

		libFolder = null;
		classesFolder = null;
		jarFile = null;
		warFile = null;
		warFolder = null;

		MavenUtil mavenUtil = new MavenUtil(getMavenHome());

		File libFolder = new File(param.projectFolder, "target/lib");
		mavenUtil.setLibPath(libFolder.getAbsolutePath());

		boolean flag = mavenUtil.doPackage(param.projectFolder, null, this.param.starter.getLog(), getMavenEnvp());
		if (!flag) {
			this.param.starter.getLog().error("maven package error.");
			throw new Exception("maven package error.");
		}

		if (model.getPackaging() != null && model.getPackaging().equals("war")) {
			if (!StringUtil.isEmpty(mavenUtil.getWebapp_folder())) {
				warFolder = new File(mavenUtil.getWebapp_folder());
			}
		} else {
			this.libFolder = libFolder;
			File classesFolder = new File(param.projectFolder, "target/classes");
			this.classesFolder = classesFolder;
		}
		File targetFolder = new File(param.projectFolder, "target");
		if (targetFolder.exists()) {
			File[] files = targetFolder.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					if (file.getName().endsWith(".jar")) {
						this.jarFile = file;
					} else if (file.getName().endsWith(".war")) {
						this.warFile = file;
					}
				}
			}
		}
		return flag;
	}

	protected void classCompile() throws Exception {

	}
}

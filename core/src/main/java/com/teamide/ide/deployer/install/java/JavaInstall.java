package com.teamide.ide.deployer.install.java;

import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Model;

import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.deployer.DeployInstall;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.ide.maven.MavenUtil;
import com.teamide.ide.processor.repository.hanlder.RepositoryHanlder;
import com.teamide.ide.service.impl.EnvironmentService;
import com.teamide.starter.bean.JavaOptionBean;

public abstract class JavaInstall extends DeployInstall {

	protected File libFolder;

	protected File classesFolder;

	protected File jarFile;

	protected File warFile;

	protected File warFolder;

	protected final JavaOptionBean option;

	public final EnvironmentBean environment;

	public JavaInstall(DeployParam param) {
		super(param);
		this.option = (JavaOptionBean) param.starter.option;
		String java_home = null;
		String maven_home = null;
		if (!StringUtil.isEmpty(this.option.getJavaenvironmentid())) {
			try {
				EnvironmentBean environment = new EnvironmentService().get(this.option.getJavaenvironmentid());
				if (environment != null) {
					java_home = environment.getPath();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!StringUtil.isEmpty(this.option.getMavenenvironmentid())) {
			try {
				EnvironmentBean environment = new EnvironmentService().get(this.option.getMavenenvironmentid());
				if (environment != null) {
					maven_home = environment.getPath();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		option.setJava_home(param.starter.formatToRoot(java_home));
		option.setMaven_home(param.starter.formatToRoot(maven_home));

		EnvironmentBean environment = null;

		String environmentid = this.option.getEnvironmentid();
		if (!StringUtil.isEmpty(environmentid)) {
			try {
				environment = new EnvironmentService().get(environmentid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.environment = environment;
		try {
			FileUtil.write(JSON.toJSONString(option).getBytes(), param.starter.starterJSONFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getJavaEnvp() {
		return option.getJavaenvp();
	}

	public String getMavenEnvp() {
		return option.getMavenenvp();
	}

	public String getContextpath() {
		return option.getContextpath();
	}

	public String getJavaHome() {
		return option.getJava_home();
	}

	public String getMavenHome() {
		return option.getMaven_home();
	}

	public void compile() throws Exception {
		Model model = RepositoryHanlder.getPomModel(new File(param.codeFolder, "pom.xml"));
		if (model != null) {
			mavenCompileAndPackage(model);
		} else {
			classCompile();
		}
		if (StringUtil.isNotEmpty(option.getRemoteid())) {

		}
	}

	public File getRemoteWorkFolder() {
		return new File(param.starter.starterFolder, "work");
	}

	protected abstract void readyRemoteWorkFolder() throws Exception;

	protected boolean mavenCompileAndPackage(Model model) throws Exception {

		libFolder = null;
		classesFolder = null;
		jarFile = null;
		warFile = null;
		warFolder = null;

		MavenUtil mavenUtil = new MavenUtil(getMavenHome());

		File libFolder = new File(param.codeFolder, "target/lib");
		mavenUtil.setLibPath(libFolder.getAbsolutePath());

		boolean flag = mavenUtil.doPackage(param.codeFolder, null, this.param.starter.getLog(), getMavenEnvp());
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
			File classesFolder = new File(param.codeFolder, "target/classes");
			this.classesFolder = classesFolder;
		}
		File targetFolder = new File(param.codeFolder, "target");
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

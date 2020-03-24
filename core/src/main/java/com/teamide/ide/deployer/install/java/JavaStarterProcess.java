package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.deployer.DeployInstall;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.ide.maven.MavenUtil;
import com.teamide.ide.processor.repository.hanlder.RepositoryHanlder;
import com.teamide.ide.service.impl.EnvironmentService;

public abstract class JavaStarterProcess extends DeployInstall {

	private final String java_home;

	private final String maven_home;

	public JavaStarterProcess(DeployParam param) {
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

	public File getAppFolder(File tomcatFolder, String name) {
		File webappsFolder = new File(tomcatFolder, "webapps");
		String contextpath = getContextpath();
		if (StringUtil.isEmpty(contextpath)) {
			contextpath = name;
		}
		String outName = contextpath;
		if (StringUtil.isEmpty(outName) || outName.equals("/")) {
			outName = "/ROOT";
		}
		File appFolder = new File(webappsFolder, outName);

		return appFolder;
	}

	protected void outWebapps(File tomcatFolder) throws Exception {
		File targetWebappFolder = new File(param.starter.workFolder, "webapp");
		if (targetWebappFolder.exists() && targetWebappFolder.listFiles().length > 0) {
			targetWebappFolder = targetWebappFolder.listFiles()[0];
		}
		if (targetWebappFolder != null && targetWebappFolder.exists()) {
			if (tomcatFolder == null || !tomcatFolder.exists()) {
				throw new Exception("tomcat[" + tomcatFolder.getAbsolutePath() + "] does not exist.");
			}

			File appFolder = getAppFolder(tomcatFolder, targetWebappFolder.getName());

			if (appFolder.exists()) {
				try {
					FileUtils.deleteDirectory(appFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!appFolder.exists()) {
				appFolder.mkdirs();
			}
			try {
				FileUtils.copyDirectoryStructure(targetWebappFolder, appFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
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
				File webapp_folder = new File(mavenUtil.getWebapp_folder());
				if (webapp_folder.exists()) {

					File targetWebappFolder = new File(param.starter.workFolder, "webapp");
					if (targetWebappFolder.exists()) {
						FileUtils.deleteDirectory(targetWebappFolder);
					}
					org.apache.commons.io.FileUtils.moveDirectory(webapp_folder,
							new File(targetWebappFolder, webapp_folder.getName()));

				}
			}
		} else {
			if (libFolder.exists()) {
				File targetLibFolder = new File(param.starter.workFolder, "lib");
				if (targetLibFolder.exists()) {
					FileUtils.deleteDirectory(targetLibFolder);
				}
				org.apache.commons.io.FileUtils.moveDirectory(libFolder, targetLibFolder);

			}

			File classesFolder = new File(param.projectFolder, "target/classes");
			if (classesFolder.exists()) {

				File targetClassesFolder = new File(param.starter.workFolder, "classes");
				if (targetClassesFolder.exists()) {
					FileUtils.deleteDirectory(targetClassesFolder);
				}
				org.apache.commons.io.FileUtils.moveDirectory(classesFolder, targetClassesFolder);

			}
		}
		return flag;
	}

	protected void classCompile() throws Exception {

	}
}

package com.teamide.ide.protect.processor.repository.starter.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.protect.maven.MavenUtil;
import com.teamide.ide.protect.processor.repository.hanlder.RepositoryHanlder;
import com.teamide.ide.protect.processor.repository.starter.StarterHandler;
import com.teamide.ide.protect.processor.repository.starter.StarterParam;
import com.teamide.ide.protect.processor.repository.starter.StarterProcess;
import com.teamide.ide.protect.service.EnvironmentService;

public abstract class JavaStarterProcess extends StarterProcess {

	public final List<File> class_folders = new ArrayList<File>();

	public final List<File> lib_folders = new ArrayList<File>();

	public File webapp_folder = null;

	private final String java_home;

	private final String maven_home;

	public JavaStarterProcess(StarterParam param) {
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
		this.java_home = param.formatToRoot(java_home);
		this.maven_home = param.formatToRoot(maven_home);
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

	public File getAppFolder(File tomcatFolder) {
		File webappsFolder = new File(tomcatFolder, "webapps");
		String contextpath = getContextpath();
		if (StringUtil.isEmpty(contextpath)) {
			contextpath = webapp_folder.getName();
		}
		String outName = contextpath;
		if (StringUtil.isEmpty(outName) || outName.equals("/")) {
			outName = "/ROOT";
		}
		File appFolder = new File(webappsFolder, outName);

		return appFolder;
	}

	protected void outWebapps(File tomcatFolder) throws Exception {
		if (webapp_folder != null && webapp_folder.exists()) {
			if (tomcatFolder == null || !tomcatFolder.exists()) {
				throw new Exception("tomcat[" + tomcatFolder.getAbsolutePath() + "] does not exist.");
			}

			File appFolder = getAppFolder(tomcatFolder);

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
				FileUtils.copyDirectoryStructure(webapp_folder, appFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void compile() throws Exception {
		class_folders.clear();
		lib_folders.clear();
		webapp_folder = null;
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
		lib_folders.add(libFolder);

		boolean flag = mavenUtil.doPackage(param.projectFolder, null, StarterHandler.getStarterLog(param.token),
				getMavenEnvp());
		if (!flag) {
			StarterHandler.getStarterLog(param.token).error("maven package error.");
			throw new Exception("maven package error.");
		}

		File classesFolder = new File(param.projectFolder, "target/classes");
		if (classesFolder.exists()) {
			class_folders.add(classesFolder);
		}

		if (model.getPackaging() != null && model.getPackaging().equals("war")) {
			if (!StringUtil.isEmpty(mavenUtil.getWebapp_folder())) {
				webapp_folder = new File(mavenUtil.getWebapp_folder());

			}
		}
		return flag;
	}

	protected void classCompile() throws Exception {

	}
}

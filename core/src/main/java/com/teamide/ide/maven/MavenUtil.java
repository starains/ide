package com.teamide.ide.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.teamide.LogTool;
import com.teamide.util.StringUtil;

public class MavenUtil {

	private final String maven_home;

	public MavenUtil(String maven_home) {
		this.maven_home = maven_home;
	}

	public boolean doClean(File root, Queue<String> infos, LogTool log) {

		return doClean(root, infos, log, null);
	}

	public boolean doClean(File root, Queue<String> infos, LogTool log, String envp) {

		List<String> list = new ArrayList<String>();
		list.add("clean");
		return todo(root, list, infos, log, envp);
	}

	public boolean doCompile(File root, Queue<String> infos, LogTool log) {

		return doCompile(root, infos, log, null);

	}

	public boolean doCompile(File root, Queue<String> infos, LogTool log, String envp) {

		List<String> list = new ArrayList<String>();
		list.add("clean compile");
		return todo(root, list, infos, log, envp);

	}

	public boolean doPackage(File root, Queue<String> infos, LogTool log) {

		return doPackage(root, infos, log, null);
	}

	public boolean doPackage(File root, Queue<String> infos, LogTool log, String envp) {

		List<String> list = new ArrayList<String>();
		list.add("clean package");
		return todo(root, list, infos, log, envp);
	}

	public boolean doInstall(File root, Queue<String> infos, LogTool log) {

		return doInstall(root, infos, log, null);
	}

	public boolean doInstall(File root, Queue<String> infos, LogTool log, String envp) {

		List<String> list = new ArrayList<String>();
		list.add("clean install");
		return todo(root, list, infos, log, envp);
	}

	public boolean doDeploy(File root, Queue<String> infos, LogTool log) {

		return doDeploy(root, infos, log, null);
	}

	public boolean doDeploy(File root, Queue<String> infos, LogTool log, String envp) {

		List<String> list = new ArrayList<String>();
		list.add("clean deploy");
		return todo(root, list, infos, log, envp);
	}

	private String webapp_folder;

	public String getWebapp_folder() {
		return webapp_folder;
	}

	public boolean todo(File root, List<String> list, Queue<String> infos, LogTool log, String envp) {
		webapp_folder = null;
		if (root == null) {
			if (log != null) {
				log.error("root is null.");
			}
			if (infos != null) {
				infos.add("root is null.");
			}
			return false;
		}
		if (!root.exists()) {
			if (log != null) {
				log.error(root + " not exists.");
			}
			if (infos != null) {
				infos.add(root + " not exists.");
			}
			return false;
		}
		if (StringUtil.isNotEmpty(maven_home)) {
			System.setProperty("maven.home", maven_home);
		} else {
			if (StringUtil.isNotEmpty(System.getenv("M3_HOME"))) {
				System.setProperty("maven.home", System.getenv("M3_HOME"));
			} else if (StringUtil.isNotEmpty(System.getenv("M2_HOME"))) {
				System.setProperty("maven.home", System.getenv("M2_HOME"));
			} else if (StringUtil.isNotEmpty(System.getenv("MAVEN_HOME"))) {
				System.setProperty("maven.home", System.getenv("MAVEN_HOME"));
			}
		}

		InvocationRequest request = new DefaultInvocationRequest();
		request.setBaseDirectory(root);
		if (!StringUtil.isEmpty(libPath)) {
			File libFolder = new File(libPath);
			if (libFolder.exists()) {
				try {
					FileUtils.deleteDirectory(libFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!libFolder.exists()) {
				libFolder.mkdirs();
			}
			list.add("dependency:copy-dependencies -DoutputDirectory=" + libPath + " ");
		}
		// -DincludeScope=compile
		if (envp != null) {
			list.add(envp);
		}
		list.add(" -Dfile.encoding=UTF-8 ");

		request.setGoals(list);

		request.setOutputHandler(new InvocationOutputHandler() {

			@Override
			public void consumeLine(String line) throws IOException {

				if (line != null && line.indexOf("Assembling webapp [") > 0) {
					if (line.indexOf("] in [") > 0) {
						String webapp = line.split("\\] in \\[")[1];
						webapp = webapp.substring(0, webapp.indexOf("]"));
						webapp_folder = webapp;
					}
				}
				if (infos != null) {
					infos.add(line);
				}
				if (log != null) {
					if (line.startsWith("[INFO]")) {
						log.info(line.substring("[INFO]".length()));
					} else if (line.startsWith("[WARNING]")) {
						log.warn(line.substring("[WARNING]".length()));
					} else if (line.startsWith("[ERROR]")) {
						log.error(line.substring("[ERROR]".length()));
					} else if (line.startsWith("[DEBUG]")) {
						log.debug(line.substring("[DEBUG]".length()));
					} else {
						log.info(line);
					}
				}
				if (infos == null && log == null) {
					System.out.println(line);
				}
			}
		});
		DefaultInvoker invoker = new DefaultInvoker();
		boolean flag = true;
		try {
			InvocationResult result = invoker.execute(request);
			if (result.getExitCode() != 0) {
				flag = result.getExecutionException() == null;
			}

		} catch (MavenInvocationException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private String libPath;

	public String getLibPath() {
		return libPath;
	}

	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}

}

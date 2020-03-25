package com.teamide.deploer.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teamide.deploer.shell.Shell;
import com.teamide.util.StringUtil;

public class JavaShell extends Shell {

	public JavaShell(File starterFolder) {
		super(starterFolder);
	}

	private String java_home;

	private String java_envp;

	private List<File> lib_folders;

	private List<File> class_folders;

	private File jar_file;

	private String xms = "128m";

	private String xmx = "256m";

	private String encoding = "UTF-8";

	private String main;

	private Map<String, String> envps;

	@Override
	public List<String> getWindowShell() {
		List<String> shell = new ArrayList<String>();
		// shell.add("@echo off");
		// shell.add("cmd");
		StringBuffer run = new StringBuffer();
		// run.append("start /b ");
		if (StringUtil.isEmpty(java_home)) {
		} else {
			run.append(java_home + "/bin/");
		}
		run.append("java");
		if (!StringUtil.isEmpty(xms)) {
			run.append(" -Xms" + xms);
		}
		if (!StringUtil.isEmpty(xmx)) {
			run.append(" -Xmx" + xmx);
		}
		if (!StringUtil.isEmpty(encoding)) {
			run.append(" -Dfile.encoding=" + encoding);
		}
		if (envps != null) {
			for (String key : envps.keySet()) {
				String value = envps.get(key);
				if (StringUtil.isEmpty(value)) {
					value = "";
				}
				run.append(" -D" + key + "=" + value);
			}
		}

		if (!StringUtil.isEmpty(java_envp)) {
			run.append(" ");
			run.append(java_envp);
			run.append(" ");
		}

		if ((lib_folders != null && lib_folders.size() > 0) || (class_folders != null && class_folders.size() > 0)) {
			run.append(" -classpath ");
			if (lib_folders != null) {
				for (File lib_folder : lib_folders) {
					run.append("\"" + lib_folder.getAbsolutePath() + "/*\"");
					run.append(";");
				}
			}
			if (class_folders != null) {
				for (File class_folder : class_folders) {
					run.append("\"" + class_folder.getAbsolutePath() + "\"");
					run.append(";");
				}
			}
		}

		if (run.toString().endsWith(";")) {
			run.setLength(run.length() - 1);
		}

		if (jar_file != null) {
			run.append(" -jar ");
			run.append(jar_file.getAbsolutePath());
			run.append(" ");
		}
		if (!StringUtil.isEmpty(main)) {
			run.append(" ");
			run.append(main);
			run.append(" ");
		}

		// if (logFile != null) {
		// run.append(" > " + logFile.getAbsolutePath());
		// }
		run.append(" ");

		shell.add(run.toString());

		return shell;
	}

	@Override
	public List<String> getLinuxShell() {
		List<String> shell = new ArrayList<String>();
		// shell.add("/bin/sh");
		// shell.add("source /etc/profile");
		// shell.add("cd `dirname $0`");
		StringBuffer run = new StringBuffer();
		run.append("nohup ");
		if (StringUtil.isEmpty(java_home)) {
		} else {
			run.append(java_home + "/bin/");
		}
		run.append("java");

		if (!StringUtil.isEmpty(xms)) {
			run.append(" -Xms" + xms);
		}
		if (!StringUtil.isEmpty(xmx)) {
			run.append(" -Xmx" + xmx);
		}
		if (!StringUtil.isEmpty(encoding)) {
			run.append(" -Dfile.encoding=" + encoding);
		}
		if (envps != null) {
			for (String key : envps.keySet()) {
				String value = envps.get(key);
				if (StringUtil.isEmpty(value)) {
					value = "";
				}
				run.append(" -D" + key + "=" + value);
			}
		}

		if (!StringUtil.isEmpty(java_envp)) {
			run.append(" ");
			run.append(java_envp);
			run.append(" ");
		}

		if ((lib_folders != null && lib_folders.size() > 0) || (class_folders != null && class_folders.size() > 0)) {
			run.append(" -classpath ");
			if (lib_folders != null) {
				for (File lib_folder : lib_folders) {
					run.append("\"" + lib_folder.getAbsolutePath() + "/*\"");
					run.append(":");
				}
			}
			if (class_folders != null) {
				for (File class_folder : class_folders) {
					run.append("\"" + class_folder.getAbsolutePath() + "\"");
					run.append(":");
				}
			}
		}

		if (run.toString().endsWith(":")) {
			run.setLength(run.length() - 1);
		}

		if (jar_file != null) {
			run.append(" -jar ");
			run.append(jar_file.getAbsolutePath());
			run.append(" ");
		}
		if (!StringUtil.isEmpty(main)) {
			run.append(" ");
			run.append(main);
			run.append(" ");
		}

		if (starterFolder != null) {
			run.append(" >> " + new File(starterFolder, "log/starter.log").getAbsolutePath());
			run.append(" 2>&1 ");
		}
		run.append(" & ");

		shell.add(run.toString());

		File pidFile = getPIDFile();
		if (pidFile != null) {
			shell.add("echo $! > " + pidFile.getAbsolutePath());
		}

		return shell;
	}

	public String getJava_home() {
		return java_home;
	}

	public void setJava_home(String java_home) {
		this.java_home = java_home;
	}

	public String getJava_envp() {
		return java_envp;
	}

	public void setJava_envp(String java_envp) {
		this.java_envp = java_envp;
	}

	public List<File> getLib_folders() {
		return lib_folders;
	}

	public void setLib_folders(List<File> lib_folders) {
		this.lib_folders = lib_folders;
	}

	public File getJar_file() {
		return jar_file;
	}

	public void setJar_file(File jar_file) {
		this.jar_file = jar_file;
	}

	public List<File> getClass_folders() {
		return class_folders;
	}

	public void setClass_folders(List<File> class_folders) {
		this.class_folders = class_folders;
	}

	public String getXms() {
		return xms;
	}

	public void setXms(String xms) {
		this.xms = xms;
	}

	public String getXmx() {
		return xmx;
	}

	public void setXmx(String xmx) {
		this.xmx = xmx;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public Map<String, String> getEnvps() {
		return envps;
	}

	public void setEnvps(Map<String, String> envps) {
		this.envps = envps;
	}

}

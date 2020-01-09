package com.teamide.starter;

import java.io.File;

import com.teamide.terminal.TerminalParam;
import com.teamide.terminal.TerminalServer;
import com.teamide.terminal.util.StringUtil;

public class IDEMain {

	public static void main(String[] args) throws Exception {
		run(args);
	}

	public static void run(String[] args) throws Exception {

		String STARTER_HOME = System.getProperty("STARTER_HOME");
		String WORK_HOME = System.getProperty("WORK_HOME");
		String PID_FILE = System.getProperty("PID_FILE");
		String BACKSTAGE = System.getProperty("BACKSTAGE");
		if (StringUtil.isEmpty(STARTER_HOME)) {
			throw new Exception("${STARTER_HOME} is null.");
		}
		if (STARTER_HOME.startsWith("\"")) {
			STARTER_HOME = STARTER_HOME.substring(1);
		}
		if (STARTER_HOME.endsWith("\"")) {
			STARTER_HOME = STARTER_HOME.substring(0, STARTER_HOME.length() - 1);
		}
		File starterFolder = new File(STARTER_HOME);
		if (!starterFolder.exists()) {
			throw new Exception(STARTER_HOME + " does not exist.");
		}

		System.out.println("starter home " + STARTER_HOME);
		System.out.println("work home " + WORK_HOME);

		File statusFile = new File(starterFolder, "starter.status");
		File startShellFile = new File(starterFolder, "starter.start.shell");
		File stopShellFile = new File(starterFolder, "starter.stop.shell");
		File eventFile = new File(starterFolder, "starter.event");
		File startShellPidFile = new File(starterFolder, "starter.start.pid");
		File logFile = new File(starterFolder, "log/starter.log");
		File timestampFile = new File(starterFolder, "starter.timestamp");

		TerminalParam param = new TerminalParam();

		if (!StringUtil.isEmpty(WORK_HOME)) {
			if (WORK_HOME.startsWith("\"")) {
				WORK_HOME = WORK_HOME.substring(1);
			}
			if (WORK_HOME.endsWith("\"")) {
				WORK_HOME = WORK_HOME.substring(0, WORK_HOME.length() - 1);
			}
			File workFolder = new File(WORK_HOME);
			param.setWorkFolder(workFolder);
		}
		if (!StringUtil.isEmpty(PID_FILE)) {
			if (PID_FILE.startsWith("\"")) {
				PID_FILE = PID_FILE.substring(1);
			}
			if (PID_FILE.endsWith("\"")) {
				PID_FILE = PID_FILE.substring(0, PID_FILE.length() - 1);
			}
			File pidFile = new File(PID_FILE);
			param.setPidFile(pidFile);
		}
		if (StringUtil.isEmpty(BACKSTAGE) || BACKSTAGE.equals("0")) {
			param.setBackstage(false);
		} else {
			param.setBackstage(true);
		}

		param.setEventFile(eventFile);
		param.setLogFile(logFile);
		param.setStartShellFile(startShellFile);
		param.setStopShellFile(stopShellFile);
		param.setStartShellPidFile(startShellPidFile);
		param.setStatusFile(statusFile);
		param.setTimestampFile(timestampFile);

		TerminalServer server = new TerminalServer(param);
		new Thread(server).start();

	}

}

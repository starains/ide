package com.teamide.starter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.terminal.TerminalParam;
import com.teamide.terminal.TerminalServer;
import com.teamide.terminal.TerminalServerTimestamp;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterServer implements Runnable {

	public final File starterRootFolder;

	public final File timestampFile;

	public final Map<String, TerminalServer> cache = new HashMap<String, TerminalServer>();

	public StarterServer(File starterRootFolder) {
		this.starterRootFolder = starterRootFolder;
		this.timestampFile = new File(starterRootFolder, "starter.timestamp");
	}

	@Override
	public void run() {

		if (timestampFile != null) {
			TerminalServerTimestamp timestamp = new TerminalServerTimestamp(timestampFile);
			new Thread(timestamp).start();
		}
		while (true) {

			if (starterRootFolder.exists() && starterRootFolder.isDirectory()) {
				File[] folders = starterRootFolder.listFiles();
				for (File folder : folders) {
					String name = folder.getName();
					if (cache.get(name) == null) {
						TerminalServer server = createTerminalServer(folder);
						if (server != null) {
							cache.put(name, server);
						}
					}
				}
			}
			for (String key : cache.keySet()) {
				File folder = new File(starterRootFolder, key);
				if (folder.exists() && folder.isDirectory()) {

				} else {
					cache.remove(key);
				}
			}

			try {
				Thread.sleep(1000 * 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public TerminalServer createTerminalServer(File starterFolder) {

		try {
			File eventFile = new File(starterFolder, "starter.event");
			if (!eventFile.exists()) {
				return null;
			}
			String content = new String(FileUtil.read(eventFile));
			if (StringUtil.isEmpty(content)) {
				return null;
			}
			File starterJSONFile = new File(starterFolder, "starter.json");
			if (!starterJSONFile.exists()) {
				return null;
			}
			content = new String(FileUtil.read(starterJSONFile));
			if (StringUtil.isEmpty(content)) {
				return null;
			}
			JSONObject starterJSON = JSONObject.parseObject(content);

			if (!starterJSON.getBooleanValue("READYED")) {
				return null;
			}

			String WORK_HOME = starterJSON.getString("WORK_HOME");
			String PID_FILE = starterJSON.getString("PID_FILE");
			String BACKSTAGE = starterJSON.getString("BACKSTAGE");

			File statusFile = new File(starterFolder, "starter.status");
			File startShellFile = new File(starterFolder, "starter.start.shell");
			File stopShellFile = new File(starterFolder, "starter.stop.shell");
			File startShellPidFile = new File(starterFolder, "starter.start.pid");
			File logFile = new File(starterFolder, "log/starter.log");
			File timestampFile = new File(starterFolder, "starter.timestamp");

			TerminalParam param = new TerminalParam();

			if (!StringUtil.isEmpty(WORK_HOME)) {
				File workFolder = new File(WORK_HOME);
				param.setWorkFolder(workFolder);
			}
			if (!StringUtil.isEmpty(PID_FILE)) {
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
			return server;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}

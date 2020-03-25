package com.teamide.starter;

import java.io.File;

import com.teamide.util.StringUtil;

public class IDEMain {

	public static void main(String[] args) throws Exception {
		run(args);
	}

	public static void run(String[] args) throws Exception {

		String STARTER_ROOT = System.getProperty("STARTER_ROOT");
		if (StringUtil.isEmpty(STARTER_ROOT)) {
			throw new Exception("${STARTER_ROOT} is null.");
		}
		if (STARTER_ROOT.startsWith("\"")) {
			STARTER_ROOT = STARTER_ROOT.substring(1);
		}
		if (STARTER_ROOT.endsWith("\"")) {
			STARTER_ROOT = STARTER_ROOT.substring(0, STARTER_ROOT.length() - 1);
		}
		File starterRootFolder = new File(STARTER_ROOT);
		if (!starterRootFolder.exists()) {
			throw new Exception(STARTER_ROOT + " does not exist.");
		}

		StarterServer server = new StarterServer(starterRootFolder);
		new Thread(server).start();

	}

}

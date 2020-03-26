package com.teamide.starter;

import java.io.File;

import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterServer implements Runnable {

	public final File starterRootFolder;

	public StarterServer(File starterRootFolder) {
		this.starterRootFolder = starterRootFolder;
	}

	@Override
	public void run() {

		while (true) {

			if (starterRootFolder.exists() && starterRootFolder.isDirectory()) {
				File[] folders = starterRootFolder.listFiles();
				for (File folder : folders) {
					if (folder.isDirectory()) {
						try {
							File lockFile = new File(folder, "starter.lock");
							if (lockFile.exists()) {
								continue;
							}
							File eventFile = new File(folder, "starter.event");
							if (!eventFile.exists()) {
								continue;
							}
							String content = new String(FileUtil.read(eventFile));
							if (StringUtil.isEmpty(content)) {
								continue;
							}
							File starterJSONFile = new File(folder, "starter.json");
							if (!starterJSONFile.exists()) {
								continue;
							}
							content = new String(FileUtil.read(starterJSONFile));
							if (StringUtil.isEmpty(content)) {
								continue;
							}
							StarterEventProcessor processor = new StarterEventProcessor(folder);

							processor.process();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			try {
				Thread.sleep(1000 * 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

package com.teamide.deployer;

import java.io.File;

import com.teamide.deployer.processor.TaskBean;
import com.teamide.deployer.processor.TaskProcessor;

public abstract class Listener implements Runnable {

	private final Long CONNECT_SLEEP = 1000 * 1L;

	private final Long RECONNECT_SLEEP = 1000 * 5L;

	private final Long RECONNECT_TOLONG_SLEEP = 1000 * 30L;

	private Long RECONNECT_COUNT = 0L;

	@Override
	public final void run() {
		Long wait = CONNECT_SLEEP;

		File starterRootFolder = new File(IDEConstant.WORKSPACES_STARTER_FOLDER);
		File tempStarterJarFile = new File(IDEConstant.PLUGIN_STARTER_JAR);
		StarterTerminal starterTerminal = new StarterTerminal(starterRootFolder, tempStarterJarFile);
		new Thread(starterTerminal).start();
		while (true) {

			boolean flag = true;
			try {
				listen();
			} catch (Exception e) {
				e.printStackTrace();
				flag = false;
			}
			if (flag) {
				RECONNECT_COUNT = 0L;
				wait = CONNECT_SLEEP;
			} else {
				RECONNECT_COUNT++;
				if (RECONNECT_COUNT > 100L) {
					RECONNECT_COUNT = 100L;
				}
				if (RECONNECT_COUNT >= 5) {
					wait = RECONNECT_TOLONG_SLEEP;
				} else {
					wait = RECONNECT_SLEEP;
				}
				System.out.println("start error, wait for " + wait + " seconds and restart.");
			}
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void listen() throws Exception;

	public final void onTask(TaskBean task) {
		if (task == null) {
			return;
		}
		TaskProcessor processor = new TaskProcessor(task);
		new Thread(processor).start();
	}
}

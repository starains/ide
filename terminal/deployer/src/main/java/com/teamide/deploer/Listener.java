package com.teamide.deploer;

import com.teamide.deploer.processor.TaskBean;
import com.teamide.deploer.processor.TaskProcessor;
import com.teamide.deploer.util.StringUtil;

public abstract class Listener implements Runnable {

	private final Long CONNECT_SLEEP = 1000 * 1L;

	private final Long RECONNECT_SLEEP = 1000 * 5L;

	private final Long RECONNECT_TOLONG_SLEEP = 1000 * 30L;

	private Long RECONNECT_COUNT = 0L;

	public final String name;
	public final String token;

	public Listener(String name, String token) throws Exception {
		this.name = name;
		this.token = token;

		if (StringUtil.isEmpty(name)) {
			throw new Exception("name is null.");
		}
		if (StringUtil.isEmpty(token)) {
			throw new Exception("token is null.");
		}

	}

	@Override
	public final void run() {
		Long wait = CONNECT_SLEEP;
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

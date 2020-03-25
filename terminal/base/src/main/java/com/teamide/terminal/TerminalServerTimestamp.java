package com.teamide.terminal;

import java.io.File;

import com.teamide.util.FileUtil;

public class TerminalServerTimestamp implements Runnable {

	private final Long WAIT = 1000 * 1L / 2;

	public final File timestampFile;

	public TerminalServerTimestamp(File timestampFile) {
		this.timestampFile = timestampFile;
	}

	@Override
	public final void run() {
		Long wait = WAIT;
		while (true) {
			write();
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void write() {
		try {
			FileUtil.write(String.valueOf(System.currentTimeMillis()).getBytes(), this.timestampFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.teamide.deployer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.teamide.terminal.TerminalUtil;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterTerminal implements Runnable {

	public final File starterRootFolder;

	public final File starterJarFile;

	public final File tempStarterJarFile;

	public final File timestampFile;

	public StarterTerminal(File starterRootFolder, File tempStarterJarFile) {
		this.starterRootFolder = starterRootFolder;
		this.tempStarterJarFile = tempStarterJarFile;
		this.starterJarFile = new File(starterRootFolder, "starter.jar");
		this.timestampFile = new File(starterRootFolder, "starter.timestamp");
	}

	@Override
	public void run() {
		while (true) {
			if (!isRunning()) {
				startStarter();
			}
			try {
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	Process process;

	private void startStarter() {

		try {
			if (!starterJarFile.exists()) {
				if (!starterJarFile.getParentFile().exists()) {
					starterJarFile.getParentFile().mkdirs();
				}
			}
			if (tempStarterJarFile.exists()) {
				if (starterJarFile.exists()) {
					starterJarFile.delete();
				}
				FileUtils.copyFile(tempStarterJarFile, starterJarFile);
			}
			if (!starterJarFile.exists()) {
				return;
			}
			StringBuffer shell = new StringBuffer();

			shell.append(" java");

			shell.append(" -Xms128m -Xmx128m");

			shell.append(" -Dfile.encoding=UTF-8 ");
			shell.append(" -DSTARTER_ROOT=\"").append(starterRootFolder.getAbsolutePath() + "\"");

			shell.append(" -jar ");
			shell.append(starterJarFile.getAbsolutePath());
			shell.append(" ");
			System.out.println("starter start shell:" + shell);

			if (IDEConstant.IS_OS_WINDOW) {
				process = Runtime.getRuntime().exec("cmd.exe /c " + shell.toString() + " ");
			} else {
				process = Runtime.getRuntime().exec("" + shell.toString() + " ");
			}

			if (process != null) {
				new Thread() {
					@Override
					public void run() {
						read(process.getInputStream(), false);
					}

				}.start();

				new Thread() {
					@Override
					public void run() {
						read(process.getErrorStream(), true);
					}

				}.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}

	}

	public void read(InputStream stream, boolean isError) {
		try {
			String charset = "UTF-8";
			// if (IDEConstant.IS_OS_WINDOW) {
			// charset = "GBK";
			// }
			InputStreamReader inputStreamReader = new InputStreamReader(stream, charset);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (isError) {
					System.err.println(line);
				} else {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRunning() {

		try {
			if (IDEConstant.IS_OS_LINUX) {

				List<String> infos = TerminalUtil.getRunningInfoForLinux(starterJarFile.getAbsolutePath());
				for (String info : infos) {
					if (info.indexOf(" java ") >= 0 && info.indexOf(" -jar ") >= 0) {
						return true;
					}
				}
			}
			if (timestampFile.exists()) {
				String value = new String(FileUtil.read(timestampFile));
				if (StringUtil.isNotEmpty(value)) {
					boolean flag = Long.valueOf(value) >= System.currentTimeMillis() - (1000 * 5);
					if (flag) {
						return true;
					}
				}
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}
}

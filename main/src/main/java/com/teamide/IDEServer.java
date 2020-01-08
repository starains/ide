package com.teamide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class IDEServer {

	public IDEServer() {

	}

	private URLClassLoader loader;

	public void restartServer() {
		stopServer();
		startServer();
	}

	public void startServer() {
		updateFile();
		try {
			File jar = new File(IDEConstant.IDE_JAR);
			if (!jar.exists()) {
				System.err.println("file " + IDEConstant.IDE_JAR + " lost.");
				return;
			}

			URL[] urls = new URL[] { jar.toURI().toURL() };
			loader = new URLClassLoader(urls);
			Class<?> mainClass = loader.loadClass("org.springframework.boot.loader.JarLauncher");
			Method method = mainClass.getMethod("main", new Class[] { String[].class });
			method.invoke(null, new Object[] { new String[] {} });
			try {
				Thread.sleep(1000 * 3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			if (loader == null) {
				System.err.println("server not started.");
				return;
			}
			Class<?> mainClass = loader.loadClass("org.springframework.boot.loader.JarLauncher");
			Method method = mainClass.getMethod("main", new Class[] { String[].class });
			method.invoke(null, new Object[] { new String[] { "stop" } });

			loader.clearAssertionStatus();
			loader.close();
			try {
				Thread.sleep(1000 * 3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void updateFile() {
		File rootFolder = new File(IDEConstant.HOME);
		File updateFolder = new File(IDEConstant.UPDATE_FOLDER);
		if (!updateFolder.exists()) {
			return;
		}
		// 先删除
		File delete = new File(updateFolder, "delete");
		if (delete.exists() && delete.isFile()) {
			InputStream stream = null;
			BufferedReader reader = null;
			try {
				stream = new FileInputStream(delete);//
				reader = new BufferedReader(new InputStreamReader(stream));
				String line = null;
				while ((line = reader.readLine()) != null) {// 一行一行读
					line = line.trim();
					if (line.length() > 0) {
						deleteFile(new File(rootFolder, line));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			delete.delete();
		}
		updateFile(updateFolder);
	}

	public void deleteFile(File out) {
		if (!out.exists()) {
			return;
		}
		System.out.println("delete path:" + out.toURI().getPath());
		if (out.isDirectory()) {
			File[] files = out.listFiles();
			for (File file : files) {
				deleteFile(file);
			}
			out.delete();
			return;
		}
		try {
			out.delete();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void updateFile(File in) {
		File rootFolder = new File(IDEConstant.HOME);
		File updateFolder = new File(IDEConstant.UPDATE_FOLDER);
		if (!in.exists()) {
			return;
		}
		String path = in.toURI().getPath().substring(updateFolder.toURI().getPath().length());
		System.out.println("update path:" + path);
		if (in.isDirectory()) {
			File[] files = in.listFiles();
			for (File file : files) {
				updateFile(file);
			}
			in.delete();
			return;
		}
		File out = new File(rootFolder, path);
		if (!out.exists()) {
			if (in.isFile()) {
				if (!out.getParentFile().exists()) {
					out.getParentFile().mkdirs();
				}
				try {
					boolean res = out.createNewFile();
					System.out.println("craete file:" + path + " " + res);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (in.isDirectory()) {
				out.mkdirs();
			}
		}
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		boolean flag = false;
		try {
			inputStream = new FileInputStream(in);
			outputStream = new FileOutputStream(out);
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			outputStream.write(bytes);
			outputStream.flush();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
				}
			}
		}
		if (flag) {
			in.delete();
		} else {
			System.err.println("升级失败，请重启系统！");
		}
	}
}

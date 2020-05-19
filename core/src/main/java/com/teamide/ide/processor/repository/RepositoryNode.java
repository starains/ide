package com.teamide.ide.processor.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Platform;
import com.teamide.ide.param.RepositoryProcessorParam;

public class RepositoryNode extends RepositoryBase {

	public RepositoryNode(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONObject install(String path) throws Exception {

		this.getLog().info("node install,  path:" + path);
		File folder = this.param.getFile(path);

		String[] cmd = null;
		if (Platform.isWindows()) {
			cmd = new String[] { "cmd" };
		} else {
			cmd = new String[] { "/bin/sh" };
		}
		Process process = Runtime.getRuntime().exec(cmd);

		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();

		PrintWriter writer = new PrintWriter(process.getOutputStream());
		if (folder != null && folder.exists()) {
			writer.println("cd " + folder.getAbsolutePath());
			writer.flush();
		}
		writer.println("npm install");
		writer.flush();
		writer.close();
		read(inputStream, false);
		read(errorStream, true);

		return null;
	}

	public void read(InputStream stream, boolean isError) {
		BufferedReader read = null;
		try {
			String charset = "UTF-8";
			InputStreamReader inputStreamReader = new InputStreamReader(stream, charset);
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				if (isError) {
					this.getLog().error(line);
				} else {
					this.getLog().info(line);
				}
			}
		} catch (Exception e) {
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (Exception e) {
				}
			}
		}
	}

}

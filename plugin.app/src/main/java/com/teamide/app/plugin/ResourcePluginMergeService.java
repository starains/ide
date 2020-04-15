package com.teamide.app.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Resource;

import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;

@Resource
public class ResourcePluginMergeService {

	private static String SRC_PATH = "/TeamIDE/code/designer/src/";

	public StringBuffer getJS() {

		return getJSContent("js");
	}

	public StringBuffer getCSS() {

		return getCSSContent("css");
	}

	public StringBuffer getCSSContent(String folder) {

		StringBuffer buffer = new StringBuffer();

		appendFolderSubFile(buffer, SRC_PATH + folder);

		return buffer;
	}

	public StringBuffer getJSContent(String folder) {

		StringBuffer buffer = new StringBuffer("(function(window) {");
		buffer.append("\n");
		buffer.append("\t'use strict';");
		buffer.append("\n");

		appendFolderSubFile(buffer, SRC_PATH + folder);

		buffer.append("\n");
		buffer.append("})(window);");

		return buffer;
	}

	public void appendFolderSubFile(StringBuffer buffer, String folderPath) {

		InputStream in = null;
		BufferedReader reader = null;
		try {
			String path = folderPath + "/files";
			if (new File(path).exists() && new File(path).isFile()) {
				in = new FileInputStream(new File(path));
			} else {
				in = this.getClass().getClassLoader().getResourceAsStream(path);
			}

			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			// 最好在将字节流转换为字符流的时候 进行转码
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (!StringUtil.isEmpty(line)) {
					String file = folderPath + "/" + line;
					InputStream stream = null;
					if (new File(file).exists() && new File(file).isFile()) {
						stream = new FileInputStream(new File(file));
					} else {
						stream = this.getClass().getClassLoader().getResourceAsStream(file);
					}
					if (stream != null) {
						buffer.append(IOUtil.readString(stream));
						buffer.append("\n");
					}
				}
			}
		} catch (Exception e) {
		} finally {
			IOUtil.close(reader, in);
		}
	}
}

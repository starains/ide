package com.teamide.terminal.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class FileUtil {
	public static byte[] read(File file) throws IOException {

		if (file == null || !file.isFile()) {
			return null;
		}
		FileInputStream stream = new FileInputStream(file);
		return read(stream);
	}

	public static boolean write(byte[] bytes, File file) throws IOException {

		boolean flag = false;
		FileOutputStream o = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			o = new FileOutputStream(file);
			o.write(bytes);
			flag = true;
		} finally {
			close(o);
		}
		return flag;
	}

	public static void close(AutoCloseable... closeables) {

		if (closeables == null) {
			return;
		}
		for (AutoCloseable closeable : closeables) {
			if (closeable == null) {
				continue;
			}
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] read(InputStream stream) throws IOException {

		if (stream == null) {
			return null;
		}
		ByteArrayOutputStream result = null;
		try {
			result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = stream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toByteArray();
		} finally {
			close(result, stream);
		}
	}

	public static void append(String line, File file) throws IOException {
		OutputStreamWriter osw = null;
		try {
			FileOutputStream fos = null;
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();// 如果文件不存在，就创建该文件
				fos = new FileOutputStream(file);// 首次写入获取
			} else {
				// 如果文件已存在，那么就在文件末尾追加写入
				fos = new FileOutputStream(file, true);// 这里构造方法多了一个参数true,表示在文件末尾追加写入
			}

			osw = new OutputStreamWriter(fos, "UTF-8");// 指定以UTF-8格式写入文件

			osw.write(line);

		} finally {
			// 写入完成关闭流
			osw.close();
		}
	}
}

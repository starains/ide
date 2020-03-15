package com.teamide.ide.processor.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.util.FileUtil;
import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;

public class RepositoryLog {

	private final String name;

	private final String folder;

	private boolean removed = false;

	public synchronized static RepositoryLog get(String name, File folder) {
		return get(name, folder.getAbsolutePath());
	}

	public synchronized static RepositoryLog get(String name, String folder) {
		return new RepositoryLog(name, folder);
	}

	private RepositoryLog(String name, String folder) {

		this.name = name;
		this.folder = folder;
	}

	public RepositoryLog debug(String message) {
		if (removed) {
			return this;
		}
		append(message);
		return this;
	}

	public RepositoryLog info(String message) {
		if (removed) {
			return this;
		}
		append(message);
		return this;
	}

	public RepositoryLog warn(String message) {
		if (removed) {
			return this;
		}
		append(message);
		return this;
	}

	public RepositoryLog error(String message) {
		if (removed) {
			return this;
		}
		append(message);
		return this;
	}

	public void remove() {
		removed = true;
		destroy();
	}

	private void destroy() {
		delete();
		removed = true;
	}

	private void delete() {
		File file = getFile();
		if (file.exists()) {
			file.delete();
		}
		file = getTimestampFile();
		if (file.exists()) {
			file.delete();
		}
	}

	public void append(String line) {
		try {
			FileUtil.append(line + "\r\n", getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clean() {
		File file = getFile();
		if (file.exists()) {
			try {
				FileUtil.write("".getBytes(), file);
				writeTimestamp();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public File getFile() {
		String path = folder;
		File file = new File(path, name + ".log");
		return file;
	}

	public File getTimestampFile() {
		String path = folder;
		File file = new File(path, name + ".log.timestamp");
		return file;
	}

	public synchronized void writeTimestamp() throws IOException {
		File file = getTimestampFile();
		FileUtil.write(String.valueOf(System.currentTimeMillis()).getBytes(), file);
	}

	public synchronized String getTimestamp() throws IOException {
		File file = getTimestampFile();
		if (!file.exists()) {
			try {
				writeTimestamp();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String(FileUtil.read(file));
	}

	public JSONObject read(int start, int end, String timestamp) {
		JSONObject result = new JSONObject();
		JSONArray logs = new JSONArray();
		result.put("logs", logs);
		if (removed) {
			return result;
		}
		File file = getFile();
		if (file.exists()) {
			BufferedReader br = null;
			try {
				String nowtimestamp = getTimestamp();
				result.put("timestamp", nowtimestamp);
				if (StringUtil.isEmpty(timestamp)) {
					timestamp = nowtimestamp;
				}
				if (!nowtimestamp.equals(timestamp)) {
					return result;
				}
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(fis);
				br = new BufferedReader(ir);
				String line = null;
				if (start == 0 && end == 0) {
					while ((line = br.readLine()) != null) {
						end++;
					}
					IOUtil.close(br);
					fis = new FileInputStream(file);
					ir = new InputStreamReader(fis);
					br = new BufferedReader(ir);
					start = end - 100;
				}

				int index = 0;

				while ((line = br.readLine()) != null) {
					if (index >= start && index <= end) {
						JSONObject log = new JSONObject();
						log.put("line", line);
						log.put("index", index);
						logs.add(log);
					}
					index++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtil.close(br);
				if (removed) {
					delete();
				}
			}
		}

		return result;
	}
}

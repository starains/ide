package com.teamide.ide.protect.processor.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.FileUtil;
import com.teamide.util.IOUtil;

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
		File file = getFile();
		if (file.exists()) {
			file.delete();
		}
		removed = true;
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

	public JSONObject read(int lastIndex) {
		JSONObject result = new JSONObject();
		List<String> lines = new ArrayList<String>();
		result.put("lines", lines);
		if (removed) {
			return result;
		}
		File file = getFile();
		if (file.exists()) {
			BufferedReader br = null;
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(fis);
				br = new BufferedReader(ir);

				String line = null;
				int start = 0;
				if (lastIndex > 0) {
					start = lastIndex + 1;
				}
				int max = 50;
				int end = start + max;
				IOUtil.close(br);
				fis = new FileInputStream(file);
				ir = new InputStreamReader(fis);
				br = new BufferedReader(ir);
				int index = 0;

				int outLastIndex = 0;
				if (lastIndex > 0) {
					outLastIndex = lastIndex;
				}
				boolean hasNext = false;
				while ((line = br.readLine()) != null) {

					if (index >= start) {
						outLastIndex = index;
						lines.add(line);
					}
					if (index >= end) {
						if (br.readLine() != null) {
							hasNext = true;
						}
						break;
					}
					index++;
				}
				result.put("lastIndex", outLastIndex);
				result.put("hasNext", hasNext);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtil.close(br);
				if (removed) {
					file.delete();
				}
			}
		}

		return result;
	}
}

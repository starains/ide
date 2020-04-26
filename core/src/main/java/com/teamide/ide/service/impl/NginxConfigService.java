package com.teamide.ide.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.annotation.Resource;

import com.teamide.client.ClientSession;
import com.teamide.ide.bean.NginxConfigBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.service.INginxConfigService;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

@Resource
public class NginxConfigService extends BaseService<NginxConfigBean> implements INginxConfigService {

	@Override
	public NginxConfigBean insert(ClientSession session, NginxConfigBean t) throws Exception {
		t = super.insert(session, t);
		createConfig(t);
		reload();
		return t;
	}

	@Override
	public NginxConfigBean update(ClientSession session, NginxConfigBean t) throws Exception {
		NginxConfigBean old = get(t.getId());
		deleteConfig(old);
		t = super.update(session, t);
		createConfig(t);
		reload();
		return t;
	}

	@Override
	public NginxConfigBean delete(ClientSession session, NginxConfigBean t) throws Exception {
		t = get(t.getId());

		super.delete(session, t);
		deleteConfig(t);
		reload();
		return t;
	}

	public void createConfig(NginxConfigBean nginxConfig) {
		File file = getFile(nginxConfig);
		if (file == null) {
			return;
		}

		try {
			if (nginxConfig.getContent() == null) {
				FileUtil.write("".getBytes(), file);
			} else {
				FileUtil.write(nginxConfig.getContent().getBytes(), file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteConfig(NginxConfigBean nginxConfig) {
		File file = getFile(nginxConfig);
		if (file == null) {
			return;
		}
		if (file.exists()) {
			file.delete();
		}
	}

	public File getFile(NginxConfigBean nginxConfig) {
		if (nginxConfig == null || StringUtil.isEmpty(nginxConfig.getName())
				|| StringUtil.isEmpty(nginxConfig.getType())) {
			return null;
		}
		File folder = null;
		switch (nginxConfig.getType()) {
		case "SERVER_ROUTE":
			folder = new File(IDEConstant.NGINX_SERVER_ROUTE_CONFIG_FOLDER);
			break;
		case "SERVER":
			folder = new File(IDEConstant.NGINX_SERVER_CONFIG_FOLDER);
			break;
		case "CONFIG":
			folder = new File(IDEConstant.NGINX_CONFIG_FOLDER);
			break;
		}
		if (folder == null) {
			return null;
		}

		return new File(folder, nginxConfig.getName() + ".conf");
	}

	public void reload() {
		IDEConfigure configure = IDEConfigure.get();
		if (configure == null || configure.getNginx() == null) {
			return;
		}
		if (StringUtil.isEmpty(configure.getNginx().getReloadcommand())) {
			return;
		}
		String[] cmd = null;
		if (IDEConstant.IS_OS_WINDOW) {
			cmd = new String[] { "cmd" };
		} else {
			cmd = new String[] { "/bin/sh" };
		}
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();
			PrintWriter writer = new PrintWriter(process.getOutputStream());
			writer.println(configure.getNginx().getReloadcommand());
			writer.flush();
			writer.close();

			// 读取控制台信息
			read(inputStream, false);
			// 读取控制台异常
			read(errorStream, true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	};

	private void read(InputStream stream, boolean isError) {
		BufferedReader read = null;
		try {
			String cs = "UTF-8";
			if (IDEConstant.IS_OS_WINDOW) {
				cs = "GBK";
			}
			InputStreamReader inputStreamReader = new InputStreamReader(stream, cs);
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				if (isError) {
					System.err.println(line);
				} else {
					System.out.println(line);
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

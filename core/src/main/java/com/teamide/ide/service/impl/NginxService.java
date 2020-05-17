package com.teamide.ide.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.NginxBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

@Resource
public class NginxService extends BaseService<NginxBean> {

	public List<NginxBean> query(String userid) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userid", userid);
		List<NginxBean> list = queryList(param);
		return list;
	}

	@Override
	public NginxBean insert(ClientSession session, NginxBean t) throws Exception {
		t = super.insert(session, t);
		createConfig(t);
		if (StringUtil.isNotEmpty(t.getParentid())) {
			NginxBean p = get(t.getParentid());
			createConfig(p);
		}
		reload();
		return t;
	}

	@Override
	public NginxBean update(ClientSession session, NginxBean t) throws Exception {
		NginxBean old = get(t.getId());
		deleteConfig(old);
		t = super.update(session, t);
		createConfig(t);
		if (StringUtil.isNotEmpty(t.getParentid())) {
			NginxBean p = get(t.getParentid());
			createConfig(p);
		}
		reload();
		return t;
	}

	@Override
	public NginxBean delete(ClientSession session, NginxBean t) throws Exception {
		t = get(t.getId());

		super.delete(session, t);
		deleteConfig(t);
		if (StringUtil.isNotEmpty(t.getParentid())) {
			NginxBean p = get(t.getParentid());
			createConfig(p);
		}
		reload();
		return t;
	}

	public void createConfig(NginxBean nginxConfig) {
		if (StringUtil.isNotEmpty(nginxConfig.getParentid())) {
			return;
		}
		File file = getFile(nginxConfig);
		if (file == null) {
			return;
		}

		try {
			if (StringUtil.isEmpty(nginxConfig.getContent())) {

				if ("SERVER".equalsIgnoreCase(nginxConfig.getType())
						&& StringUtil.isNotEmpty(nginxConfig.getDomainprefix())) {
					String domainprefix = nginxConfig.getDomainprefix();
					String server = domainprefix + ".localhost";
					if (IDEConfigure.get().getNginx() != null
							&& StringUtil.isNotEmpty(IDEConfigure.get().getNginx().getWildcarddomain())) {
						server = IDEConfigure.get().getNginx().getWildcarddomain();
						if (server.indexOf("*") >= 0) {
							server = server.replace("*", domainprefix);
						} else {
							server = domainprefix + "." + server;
						}
					}

					Map<String, Object> param = new HashMap<String, Object>();
					param.put("parentid", nginxConfig.getId());
					List<NginxBean> nginxs = new ArrayList<NginxBean>();
					try {
						nginxs = queryList(param);
					} catch (Exception e) {
						e.printStackTrace();
					}
					StringBuffer content = new StringBuffer();
					content.append("server {").append("\n");
					content.append("listen 80;").append("\n");
					content.append("server_name " + server + ";").append("\n");
					content.append("client_max_body_size 100M;").append("\n");
					content.append("proxy_set_header Host $host;").append("\n");
					content.append("proxy_set_header X-Real-IP $remote_addr;").append("\n");
					content.append("proxy_set_header REMOTE-HOST $remote_addr;").append("\n");
					content.append("proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;").append("\n");

					for (NginxBean nginx : nginxs) {
						if (StringUtil.isEmpty(nginx.getOption())) {
							continue;
						}
						JSONObject option = JSONObject.parseObject(nginx.getOption());
						String port = option.getString("port");
						String contextpath = option.getString("contextpath");
						if (StringUtil.isEmpty(contextpath)) {
							contextpath = "/";
						}
						if (!contextpath.startsWith("/")) {
							contextpath = "/" + contextpath;
						}
						if (!contextpath.endsWith("/")) {
							contextpath = contextpath + "/";
						}
						String s = contextpath;
						if (s.endsWith("/") && s.length() > 1) {
							s = s.substring(0, s.length() - 1);
						}
						if (StringUtil.isNotEmpty(port)) {
							content.append("location " + s + " {").append("\n");
							content.append("proxy_pass http://127.0.0.1:" + port + contextpath + ";").append("\n");
							content.append("proxy_set_header Host $host;").append("\n");
							content.append("proxy_set_header X-Real-IP $remote_addr;").append("\n");
							content.append("proxy_set_header REMOTE-HOST $remote_addr;").append("\n");
							content.append("proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;").append("\n");
							content.append("proxy_set_header Upgrade $http_upgrade;").append("\n");
							content.append("proxy_set_header Connection \"Upgrade\";").append("\n");
							content.append("}").append("\n");
						}

					}

					content.append("}").append("\n");
					FileUtil.write(content.toString().getBytes(), file);

				} else {
					FileUtil.write("".getBytes(), file);
				}

			} else {
				FileUtil.write(nginxConfig.getContent().getBytes(), file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteConfig(NginxBean nginxConfig) {
		File file = getFile(nginxConfig);
		if (file == null) {
			return;
		}
		if (file.exists()) {
			file.delete();
		}
	}

	public File getFile(NginxBean nginxConfig) {
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

		return new File(folder, nginxConfig.getId() + ".conf");
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

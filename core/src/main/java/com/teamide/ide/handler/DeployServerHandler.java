package com.teamide.ide.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.DeployServerBean;
import com.teamide.ide.service.IDeployServerService;
import com.teamide.ide.service.impl.DeployServerService;

public class DeployServerHandler {

	static final Map<String, DeployServerThread> CACHE = new HashMap<String, DeployServerThread>();

	static final Object LOAD_SPACES_LOCK = new Object();

	public static void reloadServers() {
		CACHE.clear();
		loadServers();

	}

	public static void loadServers() {
		if (CACHE.size() > 0) {
			return;
		}
		synchronized (LOAD_SPACES_LOCK) {
			if (CACHE.size() > 0) {
				return;
			}
			try {
				IDeployServerService deployServerService = new DeployServerService();
				List<DeployServerBean> servers = deployServerService.queryList(new HashMap<String, Object>());
				for (DeployServerBean server : servers) {
					loadServer(server.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void remove(String id) {
		if (CACHE.get(id) != null) {
			CACHE.get(id).destroy();
		}
		CACHE.remove(id);
	}

	public static DeployServerBean get(String id) {
		if (StringUtil.isEmpty(id)) {
			return null;
		}

		loadServers();

		if (CACHE.get(id) == null) {
			loadServer(id);
		}
		if (CACHE.get(id) != null) {
			return CACHE.get(id).getServer();
		}
		return null;
	}

	public static void loadServer(String id) {
		if (CACHE.get(id) != null) {
			return;
		}
		synchronized (CACHE) {
			if (CACHE.get(id) != null) {
				return;
			}
		}

		try {
			IDeployServerService deployServerService = new DeployServerService();
			DeployServerBean server = deployServerService.get(id);
			if (server != null) {
				CACHE.put(server.getId(), new DeployServerThread(server));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getCheckUrl(DeployServerBean server) {
		String url = getServerUrl(server);
		return url + "/deploy/check";
	}

	public static String getServerUrl(DeployServerBean server) {
		String url = server.getServer();
		return url;
	}
}

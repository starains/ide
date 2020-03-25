package com.teamide.ide.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.RemoteBean;
import com.teamide.ide.service.IRemoteService;
import com.teamide.ide.service.impl.RemoteService;

public class RemoteHandler {

	static final Map<String, RemoteThread> CACHE = new HashMap<String, RemoteThread>();

	static final Object LOAD_LOCK = new Object();

	public static void reloadRemotes() {
		CACHE.clear();
		loadRemotes();

	}

	public static void loadRemotes() {
		if (CACHE.size() > 0) {
			return;
		}
		synchronized (LOAD_LOCK) {
			if (CACHE.size() > 0) {
				return;
			}
			try {
				IRemoteService service = new RemoteService();
				List<RemoteBean> remotes = service.queryList(new HashMap<String, Object>());
				for (RemoteBean remote : remotes) {
					loadRemote(remote.getId());
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

	public static RemoteBean get(String id) {
		if (StringUtil.isEmpty(id)) {
			return null;
		}

		loadRemotes();

		if (CACHE.get(id) == null) {
			loadRemote(id);
		}
		if (CACHE.get(id) != null) {
			return CACHE.get(id).getRemote();
		}
		return null;
	}

	public static void loadRemote(String id) {
		if (CACHE.get(id) != null) {
			return;
		}
		synchronized (CACHE) {
			if (CACHE.get(id) != null) {
				return;
			}
		}

		try {
			IRemoteService service = new RemoteService();
			RemoteBean remote = service.get(id);
			if (remote != null) {
				CACHE.put(remote.getId(), new RemoteThread(remote));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getCheckUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/remote/check";
	}

	public static String getPluginsUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/remote/plugins";
	}

	public static String getStarterDeployUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/deploy";
	}

	public static String getStarterStartUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/start";
	}

	public static String getStarterStopUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/stop";
	}

	public static String getStarterRemoveUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/remove";
	}

	public static String getStarterLogUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/log";
	}

	public static String getStarterCleanLogUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/cleanLog";
	}

	public static String getStarterStatusUrl(RemoteBean remote) {
		String url = getServerUrl(remote);
		return url + "/starter/status";
	}

	public static String getServerUrl(RemoteBean remote) {
		String url = remote.getServer();
		return url;
	}
}

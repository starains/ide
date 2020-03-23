package com.teamide.ide.handler;

import com.alibaba.fastjson.JSONObject;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.ide.bean.RemoteBean;
import com.teamide.ide.enums.DeployServerMode;
import com.teamide.ide.enums.DeployServerStatus;
import com.teamide.util.StringUtil;

public class RemoteThread extends Thread {

	private final RemoteBean remote;

	private final long WAIT = 5 * 1000;

	private boolean stop = false;

	public RemoteThread(RemoteBean remote) {
		this.remote = remote;
		this.start();
	}

	public void destroy() {
		this.stop = true;
	}

	@Override
	public void run() {

		while (true) {
			if (stop) {
				break;
			}

			check();

			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void check() {
		try {
			if (!DeployServerMode.SERVER.getValue().equalsIgnoreCase(this.remote.getMode())) {
				return;
			}

			String url = RemoteHandler.getCheckUrl(this.remote);
			HttpRequest request = HttpRequest.get(url);
			HttpResponse response = request.execute();
			String res = response.body();
			if (StringUtil.isNotEmpty(res)) {
				JSONObject json = JSONObject.parseObject(res);
				if (json.getIntValue("errcode") != 0) {
					throw new Exception(json.getString("errmsg"));
				}
			} else {
				throw new Exception("response is null.");
			}
			this.remote.setErrmsg(null);
			this.remote.setStatus(DeployServerStatus.ONLINE);
		} catch (Exception e) {
			this.remote.setStatus(DeployServerStatus.OFFLINE);
			this.remote.setErrmsg(e.getMessage());
		}
	}

	public RemoteBean getRemote() {
		return remote;
	}
}

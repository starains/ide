package com.teamide.protect.ide.service;

import javax.annotation.Resource;

import com.teamide.ide.bean.ConfigureBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.service.IConfigureService;
import com.teamide.ide.service.impl.BaseService;

@Resource
public class ConfigureService extends BaseService<ConfigureBean> implements IConfigureService {

	@Override
	public ConfigureBean get() {

		try {
			return get("0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConfigureBean update(Client client, ConfigureBean t) throws Exception {
		ConfigureBean res = super.update(client, t);
		IDEConfigure.loadConfigure();
		return res;
	}

}

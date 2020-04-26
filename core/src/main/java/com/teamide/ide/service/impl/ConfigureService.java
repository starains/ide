package com.teamide.ide.service.impl;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.ConfigureBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.configure.IDEConfigureAccount;
import com.teamide.ide.configure.IDEConfigureLogin;
import com.teamide.ide.configure.IDEConfigureMailbox;
import com.teamide.ide.configure.IDEConfigureNginx;
import com.teamide.ide.configure.IDEConfigureRepository;
import com.teamide.ide.configure.IDEConfigureSpace;
import com.teamide.ide.service.IConfigureService;
import com.teamide.util.StringUtil;

@Resource
public class ConfigureService extends BaseService<ConfigureBean> implements IConfigureService {

	@Override
	public IDEConfigure get() {

		try {
			ConfigureBean bean = get("0");
			if (bean != null) {
				IDEConfigure configure = new IDEConfigure();

				if (StringUtil.isNotEmpty(bean.getAccountconfigure())) {
					configure.setAccount(JSONObject.parseObject(bean.getAccountconfigure(), IDEConfigureAccount.class));
				}
				if (StringUtil.isNotEmpty(bean.getLoginconfigure())) {
					configure.setLogin(JSONObject.parseObject(bean.getLoginconfigure(), IDEConfigureLogin.class));
				}
				if (StringUtil.isNotEmpty(bean.getMailboxconfigure())) {
					configure.setMailbox(JSONObject.parseObject(bean.getMailboxconfigure(), IDEConfigureMailbox.class));
				}
				if (StringUtil.isNotEmpty(bean.getSpaceconfigure())) {
					configure.setSpace(JSONObject.parseObject(bean.getSpaceconfigure(), IDEConfigureSpace.class));
				}
				if (StringUtil.isNotEmpty(bean.getRepositoryconfigure())) {
					configure.setRepository(
							JSONObject.parseObject(bean.getRepositoryconfigure(), IDEConfigureRepository.class));
				}
				if (StringUtil.isNotEmpty(bean.getNginxconfigure())) {
					configure.setNginx(JSONObject.parseObject(bean.getNginxconfigure(), IDEConfigureNginx.class));
				}

				return configure;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IDEConfigure save(ClientSession session, IDEConfigure configure) throws Exception {
		ConfigureBean t = new ConfigureBean();
		t.setId("0");
		if (configure != null) {
			if (configure.getAccount() != null) {
				t.setAccountconfigure(JSON.toJSONString(configure.getAccount()));
			}
			if (configure.getLogin() != null) {
				t.setLoginconfigure(JSON.toJSONString(configure.getLogin()));
			}
			if (configure.getMailbox() != null) {
				t.setMailboxconfigure(JSON.toJSONString(configure.getMailbox()));
			}
			if (configure.getSpace() != null) {
				t.setSpaceconfigure(JSON.toJSONString(configure.getSpace()));
			}
			if (configure.getRepository() != null) {
				t.setRepositoryconfigure(JSON.toJSONString(configure.getRepository()));
			}
			if (configure.getNginx() != null) {
				t.setNginxconfigure(JSON.toJSONString(configure.getNginx()));
			}
		}
		super.save(session, t);

		IDEConfigure.loadConfigure();
		return get();
	}

}

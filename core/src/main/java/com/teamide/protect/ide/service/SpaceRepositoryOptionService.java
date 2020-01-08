package com.teamide.protect.ide.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceRepositoryOptionBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.service.impl.BaseService;
import com.teamide.protect.ide.enums.OptionType;

@Resource
public class SpaceRepositoryOptionService extends BaseService<SpaceRepositoryOptionBean> {

	public List<SpaceRepositoryOptionBean> query(Client client, SpaceBean space, String branch, String path,
			String name, OptionType type) throws Exception {

		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", space.getId());
		param.put("type", type.name());
		String userid = null;
		if (client != null && client.getUser() != null) {
			userid = client.getUser().getId();
		}
		if (!StringUtil.isEmpty(path)) {
			param.put("path", path);
		}
		if (!StringUtil.isEmpty(name)) {
			param.put("name", name);
		}
		if (!StringUtil.isEmpty(branch)) {
			param.put("branch", branch);
		}
		if (type.equals(OptionType.GIT_CERTIFICATE)) {
			if (!StringUtil.isEmpty(userid)) {
				param.put("userid", userid);
			} else {
				param.put("userid", "0");
			}
		}
		List<SpaceRepositoryOptionBean> options = service.queryList(param);
		return options;
	}

}

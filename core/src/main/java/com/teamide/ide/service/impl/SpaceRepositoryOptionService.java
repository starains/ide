package com.teamide.ide.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceRepositoryOptionBean;
import com.teamide.ide.enums.OptionType;

@Resource
public class SpaceRepositoryOptionService extends BaseService<SpaceRepositoryOptionBean> {

	public List<SpaceRepositoryOptionBean> query(String spaceid, String branch, String path, String name,
			OptionType type) throws Exception {
		return query(spaceid, branch, path, name, type.name());
	}

	public List<SpaceRepositoryOptionBean> query(String spaceid, String branch, String path, String name, String type)
			throws Exception {

		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", spaceid);
		if (type != null) {
			param.put("type", type);
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
		List<SpaceRepositoryOptionBean> options = service.queryList(param);
		return options;
	}

}

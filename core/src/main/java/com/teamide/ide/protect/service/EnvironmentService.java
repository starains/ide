package com.teamide.ide.protect.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.enums.EnvironmentType;
import com.teamide.ide.service.IEnvironmentService;
import com.teamide.ide.service.impl.BaseService;

@Resource
public class EnvironmentService extends BaseService<EnvironmentBean> implements IEnvironmentService {

	@Override
	public EnvironmentBean getByVersion(EnvironmentType type, String version) throws Exception {
		if (StringUtil.isEmpty(version)) {
			throw new Exception("version is null.");
		}
		if (type == null) {
			throw new Exception("type is null.");
		}
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("version", version);
		param.put("type", type.getValue());
		List<EnvironmentBean> list = queryList(param);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

}

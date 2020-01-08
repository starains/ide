package com.teamide.protect.ide.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceRepositoryOpenBean;
import com.teamide.ide.service.impl.BaseService;

@Resource
public class SpaceRepositoryOpenService extends BaseService<SpaceRepositoryOpenBean> {
	@Override
	public List<SpaceRepositoryOpenBean> queryList(Map<String, Object> param) throws Exception {

		String completetablename = getTablename(getTClass(), param);
		String sql = "SELECT * FROM " + completetablename + getWhereSql(getTClass(), param);
		sql += " ORDER BY opentime ASC";
		return queryList(getTClass(), sql, param);
	}

	public List<SpaceRepositoryOpenBean> queryOpens(String userid, String spaceid, String branch) throws Exception {
		if (StringUtil.isEmpty(userid) || StringUtil.isEmpty(spaceid) || StringUtil.isEmpty(branch)) {
			return new ArrayList<SpaceRepositoryOpenBean>();
		}
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("spaceid", spaceid);
		p.put("userid", userid);
		p.put("branch", branch);

		return queryList(p);
	}
}

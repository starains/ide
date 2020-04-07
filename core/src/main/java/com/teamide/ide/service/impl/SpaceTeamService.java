package com.teamide.ide.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceTeamBean;
import com.teamide.ide.handler.SpaceHandler;
import com.teamide.ide.service.ISpaceTeamService;
import com.teamide.util.StringUtil;

@Resource
public class SpaceTeamService extends BaseService<SpaceTeamBean> implements ISpaceTeamService {

	@Override
	public SpaceTeamBean insert(ClientSession client, SpaceTeamBean t) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", t.getSpaceid());
		param.put("recordid", t.getRecordid());
		param.put("type", t.getType());

		int count = queryCount(SpaceTeamBean.class, param);
		if (count > 0) {
			throw new Exception("该成员已加入！");
		}

		SpaceHandler.remove(t.getSpaceid());
		SpaceTeamBean res = super.insert(client, t);
		SpaceHandler.get(t.getSpaceid());
		return res;
	}

	@Override
	public SpaceTeamBean update(ClientSession client, SpaceTeamBean t) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", t.getSpaceid());
		param.put("recordid", t.getRecordid());
		param.put("type", t.getType());

		List<SpaceTeamBean> list = queryList(param);
		if (list != null && list.size() > 0) {
			for (SpaceTeamBean one : list) {
				if (!one.getId().equals(t.getId())) {
					throw new Exception("该成员已加入！");
				}
			}
		}

		SpaceHandler.remove(t.getSpaceid());
		SpaceTeamBean res = super.update(client, t);
		SpaceHandler.get(t.getSpaceid());
		return res;
	}

	@Override
	public SpaceTeamBean delete(ClientSession client, SpaceTeamBean t) throws Exception {
		if (!StringUtil.isEmpty(t.getId())) {
			t = get(t.getId());
			if (t != null) {
				SpaceHandler.remove(t.getSpaceid());
				super.delete(client, t);
				SpaceHandler.get(t.getSpaceid());
			}
		}
		return t;
	}

	@Override
	public int delete(Serializable id) throws Exception {
		SpaceTeamBean t = get(id);
		if (t != null) {
			SpaceHandler.remove(t.getSpaceid());
		}
		return super.delete(id);
	}

}

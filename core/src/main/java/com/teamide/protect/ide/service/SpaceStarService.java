package com.teamide.protect.ide.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceStarBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.service.ISpaceStarService;
import com.teamide.ide.service.impl.BaseService;

@Resource
public class SpaceStarService extends BaseService<SpaceStarBean> implements ISpaceStarService {

	@Override
	public SpaceStarBean insert(Client client, SpaceStarBean t) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", t.getSpaceid());
		param.put("userid", t.getUserid());

		int count = queryCount(SpaceStarBean.class, param);
		if (count > 0) {
			throw new Exception("该仓库已收藏！");
		}

		SpaceStarBean res = super.insert(client, t);
		return res;
	}

	@Override
	public SpaceStarBean update(Client client, SpaceStarBean t) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", t.getSpaceid());
		param.put("userid", t.getUserid());

		List<SpaceStarBean> list = queryList(param);
		if (list != null && list.size() > 0) {
			for (SpaceStarBean one : list) {
				if (!one.getId().equals(t.getId())) {
					throw new Exception("该仓库已收藏！");
				}
			}
		}

		SpaceStarBean res = super.update(client, t);
		return res;
	}

	@Override
	public SpaceStarBean delete(Client client, SpaceStarBean t) throws Exception {
		if (t != null) {
			if (StringUtil.isEmpty(t.getId())) {
				if (!StringUtil.isEmpty(t.getSpaceid()) && !StringUtil.isEmpty(t.getUserid())) {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("spaceid", t.getSpaceid());
					param.put("userid", t.getUserid());

					List<SpaceStarBean> list = queryList(param);
					if (list != null && list.size() > 0) {
						for (SpaceStarBean one : list) {
							this.delete(client, one);
						}
						return list.get(0);
					}
				}
				return null;
			}
		}
		return super.delete(client, t);
	}

	@Override
	public int delete(Serializable id) throws Exception {
		return super.delete(id);
	}

}

package com.teamide.ide.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.RemoteBean;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.handler.RemoteHandler;
import com.teamide.ide.service.IRemoteService;
import com.teamide.util.StringUtil;

@Resource
public class RemoteService extends BaseService<RemoteBean> implements IRemoteService {

	@Override
	public RemoteBean save(ClientSession session, RemoteBean remote) throws Exception {
		if (find(remote, null)) {
			return update(session, remote);
		}
		return insert(session, remote);

	}

	@Override
	public RemoteBean insert(ClientSession session, RemoteBean remote) throws Exception {
		validateToken(remote);
		remote = super.insert(session, remote);

		return RemoteHandler.get(remote.getId());
	}

	public void validateToken(RemoteBean remote) throws Exception {
		String tablename = IDEFactory.getRealtablename(RemoteBean.class, (JSONObject) JSON.toJSON(remote));
		String sql = "SELECT COUNT(0) FROM " + tablename + " WHERE 1=1 ";

		sql += " AND token = :token ";
		if (!StringUtil.isEmpty(remote.getId())) {
			sql += " AND id != :id ";
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", remote.getId());
		param.put("token", remote.getToken());
		if (super.queryCount(sql, param) > 0) {
			throw new Exception("令牌“" + remote.getToken() + "”已存在！");
		}

	}

	@Override
	public RemoteBean update(ClientSession session, RemoteBean remote) throws Exception {
		if (StringUtil.isNotEmpty(remote.getToken())) {
			validateToken(remote);
		}
		RemoteHandler.remove(remote.getId());
		super.update(session, remote);
		return RemoteHandler.get(remote.getId());
	}

	@Override
	public RemoteBean getByToken(String token) throws Exception {
		if (StringUtil.isEmpty(token)) {
			throw new Exception("token is null.");
		}
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("token", token);
		List<RemoteBean> list = queryList(param);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

}

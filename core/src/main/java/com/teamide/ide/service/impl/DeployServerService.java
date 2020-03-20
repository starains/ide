package com.teamide.ide.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.DeployServerBean;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.handler.DeployServerHandler;
import com.teamide.ide.service.IDeployServerService;
import com.teamide.util.StringUtil;

@Resource
public class DeployServerService extends BaseService<DeployServerBean> implements IDeployServerService {

	@Override
	public DeployServerBean save(ClientSession session, DeployServerBean server) throws Exception {
		if (find(server, null)) {
			return update(session, server);
		}
		return insert(session, server);

	}

	@Override
	public DeployServerBean insert(ClientSession session, DeployServerBean server) throws Exception {
		validateToken(server);
		server = super.insert(session, server);

		return DeployServerHandler.get(server.getId());
	}

	public void validateToken(DeployServerBean server) throws Exception {
		String tablename = IDEFactory.getRealtablename(DeployServerBean.class, (JSONObject) JSON.toJSON(server));
		String sql = "SELECT COUNT(0) FROM " + tablename + " WHERE 1=1 ";

		sql += " AND token = :token ";
		if (!StringUtil.isEmpty(server.getId())) {
			sql += " AND id != :id ";
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", server.getId());
		param.put("token", server.getToken());
		if (super.queryCount(sql, param) > 0) {
			throw new Exception("令牌“" + server.getToken() + "”已存在！");
		}

	}

	@Override
	public DeployServerBean update(ClientSession session, DeployServerBean server) throws Exception {
		if (StringUtil.isNotEmpty(server.getToken())) {
			validateToken(server);
		}
		DeployServerHandler.remove(server.getId());
		super.update(session, server);
		return DeployServerHandler.get(server.getId());
	}

	@Override
	public DeployServerBean getByToken(String token) throws Exception {
		if (StringUtil.isEmpty(token)) {
			throw new Exception("token is null.");
		}
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("token", token);
		List<DeployServerBean> list = queryList(param);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

}

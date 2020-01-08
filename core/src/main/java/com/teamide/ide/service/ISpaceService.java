package com.teamide.ide.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.PageResultBean;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.enums.SpaceType;

public interface ISpaceService extends IBaseService<SpaceBean> {

	public SpaceBean getByName(String name, SpaceType spaceType) throws Exception;

	public SpaceBean getByName(String name, String parentid) throws Exception;

	public List<SpaceBean> getChilds(String id) throws Exception;

	public List<JSONObject> queryParents(Client client, String spaceid) throws Exception;

	public PageResultBean<Map<String, Object>> queryJoins(Client client, String spaceid, int pageindex, int pagesize)
			throws Exception;

	public PageResultBean<Map<String, Object>> queryVisibles(Client client, String parentid, int pageindex,
			int pagesize) throws Exception;

	public PageResultBean<Map<String, Object>> queryStars(Client client, String userid, int pageindex, int pagesize)
			throws Exception;

	public PageResultBean<Map<String, Object>> queryMasters(Client client, int pageindex, int pagesize)
			throws Exception;

	public PageResultBean<Map<String, Object>> querySearch(Client client, String searchText, int pageindex,
			int pagesize) throws Exception;

	public boolean isRepositorys(SpaceBean space);
}

package com.teamide.protect.ide.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.PageResultBean;
import com.teamide.db.bean.PageSqlParam;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceStarBean;
import com.teamide.ide.bean.SpaceTeamBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.enums.SpaceTeamType;
import com.teamide.ide.enums.SpaceType;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.service.ISpaceService;
import com.teamide.ide.service.impl.BaseService;
import com.teamide.protect.ide.handler.SpaceHandler;

@Resource
public class SpaceService extends BaseService<SpaceBean> implements ISpaceService {

	@Override
	public SpaceBean save(Client client, SpaceBean space) throws Exception {
		if (find(space, null)) {
			return update(client, space);
		}
		return insert(client, space);

	}

	@Override
	public SpaceBean insert(Client client, SpaceBean space) throws Exception {
		validateName(space);
		space = super.insert(client, space);
		if (client != null && client.isLogin()) {
			SpaceTeamBean spaceTeam = new SpaceTeamBean();
			spaceTeam.setPermission(SpacePermission.MASTER.getValue());
			spaceTeam.setRecordid(client.getUser().getId());
			spaceTeam.setSpaceid(space.getId());
			spaceTeam.setType(SpaceTeamType.USERS.getValue());
			BaseService<SpaceTeamBean> spaceTeamService = new BaseService<SpaceTeamBean>();
			spaceTeamService.insert(client, spaceTeam);

		}
		return SpaceHandler.get(space.getId());
	}

	public void validateName(SpaceBean space) throws Exception {
		String tablename = IDEFactory.getRealtablename(SpaceBean.class, (JSONObject) JSON.toJSON(space));
		String sql = "SELECT COUNT(0) FROM " + tablename + " WHERE 1=1 ";
		sql += " AND ( ";
		if (!StringUtil.isEmpty(space.getParentid())) {
			sql += " parentid = :parentid ";
		} else {
			sql += " parentid = '' OR parentid IS NULL ";
		}

		sql += " ) ";
		sql += " AND name = :name ";
		if (!StringUtil.isEmpty(space.getId())) {
			sql += " AND id != :id ";
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", space.getId());
		param.put("parentid", space.getParentid());
		param.put("name", space.getName());
		if (super.queryCount(sql, param) > 0) {
			throw new Exception("名称为“" + space.getName() + "”的库已存在！");
		}

	}

	@Override
	public SpaceBean update(Client client, SpaceBean space) throws Exception {

		validateName(space);
		SpaceHandler.remove(space.getId());
		super.update(client, space);
		return SpaceHandler.get(space.getId());
	}

	@Override
	public SpaceBean getByName(String name, SpaceType spaceType) throws Exception {
		if (StringUtil.isEmpty(name)) {
			throw new Exception("name is null.");
		}
		if (spaceType == null) {
			throw new Exception("space type is null.");
		}
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("name", name);
		param.put("type", spaceType.getValue());
		List<SpaceBean> list = queryList(param);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public SpaceBean getByName(String name, String parentid) throws Exception {
		if (StringUtil.isEmpty(name)) {
			throw new Exception("name is null.");
		}
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("name", name);
		param.put("parentid", parentid);
		List<SpaceBean> list = queryList(param);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public List<SpaceBean> getChilds(String id) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("parentid", id);
		return queryList(param);
	}

	@Override
	public PageResultBean<Map<String, Object>> queryVisibles(Client client, String parentid, int pageindex,
			int pagesize) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		String space_tablename = IDEFactory.getRealtablename(SpaceBean.class, param);
		String space_team_tablename = IDEFactory.getRealtablename(SpaceTeamBean.class, param);
		if (client == null || StringUtil.isEmpty(space_tablename) || StringUtil.isEmpty(space_team_tablename)) {
			return null;
		}
		if (client.isLogin()) {
			param.put("recordid", client.getUser().getId());
		}
		String whereSql = " WHERE 1=1 ";
		if (!StringUtil.isEmpty(parentid)) {
			SpaceBean parent = get(parentid);
			param.put("parentid", parent.getId());
			whereSql += " AND ( ";
			whereSql += "  parentid = :parentid ";
			if (SpaceHandler.isUsers(parent)) {
				UserBean user = new UserService().getBySpaceid(parent.getId());
				if (user != null) {
					whereSql += " OR ( ";
					whereSql += " (parentid IS NULL || parentid = '') ";
					whereSql += " AND ";
					whereSql += " id IN (SELECT spaceid FROM " + space_team_tablename
							+ " WHERE recordid=:parentspaceuserid AND type='" + SpaceTeamType.USERS.getValue() + "' ) ";
					whereSql += " ) ";
					param.put("parentspaceuserid", user.getId());

				}
			}
			whereSql += " ) ";
		} else {
			whereSql += " AND 1=2 ";
		}

		whereSql += " AND ";
		whereSql += " 	( ";
		whereSql += "	publictype='OPEN' ";
		whereSql += "	OR ";
		whereSql += "	id IN (SELECT spaceid FROM " + space_team_tablename + " WHERE recordid=:recordid AND type='"
				+ SpaceTeamType.USERS.getValue() + "') ";
		whereSql += "	) ";

		String sql = "SELECT * FROM " + space_tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + space_tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		foramt(client, page.getValue());
		return page;
	}

	@Override
	public PageResultBean<Map<String, Object>> queryMasters(Client client, int pageindex, int pagesize)
			throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		String space_tablename = IDEFactory.getRealtablename(SpaceBean.class, param);
		String space_team_tablename = IDEFactory.getRealtablename(SpaceTeamBean.class, param);
		if (client == null || StringUtil.isEmpty(space_tablename) || StringUtil.isEmpty(space_team_tablename)) {
			return null;
		}
		if (!client.isLogin()) {
			return null;
		}
		param.put("recordid", client.getUser().getId());
		param.put("userspaceid", client.getUser().getSpaceid());

		String whereSql = " WHERE 1=1 ";

		whereSql += " AND id IN ";
		whereSql += "  (SELECT spaceid FROM " + space_team_tablename + " ";
		whereSql += "       WHERE recordid=:recordid AND permission='MASTER' AND type='"
				+ SpaceTeamType.USERS.getValue() + "' )";
		whereSql += " ";
		whereSql += " OR id = :userspaceid ";

		String sql = "SELECT *  FROM " + space_tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + space_tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		foramt(client, page.getValue());
		return page;
	}

	@Override
	public PageResultBean<Map<String, Object>> queryStars(Client client, String userid, int pageindex, int pagesize)
			throws Exception {
		if (StringUtil.isEmpty(userid)) {
			return null;
		}

		Map<String, Object> param = new HashMap<String, Object>();
		String space_tablename = IDEFactory.getRealtablename(SpaceBean.class, param);
		String space_star_tablename = IDEFactory.getRealtablename(SpaceStarBean.class, param);
		if (client == null || StringUtil.isEmpty(space_tablename) || StringUtil.isEmpty(space_star_tablename)) {
			return null;
		}
		if (!client.isLogin()) {
			return null;
		}
		param.put("userid", userid);

		String whereSql = " WHERE 1=1 ";

		whereSql += " AND id IN ";
		whereSql += "  (SELECT spaceid FROM " + space_star_tablename + " ";
		whereSql += "       WHERE userid=:userid )";
		whereSql += " ";

		String sql = "SELECT *  FROM " + space_tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + space_tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		foramt(client, page.getValue());
		return page;
	}

	@Override
	public PageResultBean<Map<String, Object>> querySearch(Client client, String searchText, int pageindex,
			int pagesize) throws Exception {
		if (client == null || StringUtil.isEmpty(searchText)) {
			return null;
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("searchText", searchText);
		param.put("likeSearchText", "%" + searchText + "%");
		String space_tablename = IDEFactory.getRealtablename(SpaceBean.class, param);
		String space_team_tablename = IDEFactory.getRealtablename(SpaceTeamBean.class, param);
		if (client == null || StringUtil.isEmpty(space_tablename) || StringUtil.isEmpty(space_team_tablename)) {
			return null;
		}
		String whereSql = " WHERE 1=1 ";

		whereSql += " AND name like :likeSearchText ";

		String sql = "SELECT * FROM " + space_tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + space_tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		foramt(client, page.getValue());
		return page;
	}

	@Override
	public PageResultBean<Map<String, Object>> queryJoins(Client client, String spaceid, int pageindex, int pagesize)
			throws Exception {
		if (client == null || StringUtil.isEmpty(spaceid)) {
			return null;
		}

		String recordid = null;
		SpaceTeamType spaceTeamType = null;
		SpaceBean space = SpaceHandler.get(spaceid);
		if (space != null) {
			SpaceType spaceType = SpaceType.get(space.getType());
			if (spaceType != null) {
				switch (spaceType) {
				case USERS:
					UserBean user = new UserService().getBySpaceid(space.getId());
					if (user != null) {
						recordid = user.getId();
					}
					spaceTeamType = SpaceTeamType.USERS;
					break;
				default:
					break;
				}
			}
		}
		if (spaceTeamType == null || StringUtil.isEmpty(recordid)) {
			return null;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("recordid", recordid);
		String space_tablename = IDEFactory.getRealtablename(SpaceBean.class, param);
		String space_team_tablename = IDEFactory.getRealtablename(SpaceTeamBean.class, param);
		if (client == null || StringUtil.isEmpty(space_tablename) || StringUtil.isEmpty(space_team_tablename)) {
			return null;
		}
		String whereSql = " WHERE 1=1 ";

		whereSql += " AND id IN";
		whereSql += "	(SELECT spaceid FROM " + space_team_tablename + " WHERE recordid=:recordid AND type='"
				+ spaceTeamType.getValue() + "' ) ";

		String sql = "SELECT * FROM " + space_tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + space_tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		foramt(client, page.getValue());
		return page;
	}

	public void foramt(Client client, List<Map<String, Object>> spaces) throws Exception {
		if (spaces == null) {
			return;
		}
		for (Map<String, Object> one : spaces) {
			SpaceBean space = SpaceHandler.get(String.valueOf(one.get("id")));
			JSONObject json = SpaceHandler.getFormat(space.getId());
			SpacePermission permission = SpaceHandler.getPermission(space, client);
			json.put("permission", permission);

			json.put("login_user_star", false);
			if (client.getUser() != null) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("spaceid", space.getId());
				param.put("userid", client.getUser().getId());

				int count = queryCount(SpaceStarBean.class, param);
				if (count > 0) {
					json.put("login_user_star", true);
				}
			}

			one.putAll(json);
		}
	}

	@Override
	public List<JSONObject> queryParents(Client client, String spaceid) throws Exception {
		List<JSONObject> parents = new ArrayList<JSONObject>();
		if (StringUtil.isEmpty(spaceid)) {
			return parents;
		}
		SpaceBean space = SpaceHandler.get(spaceid);
		if (space != null) {
			String parentid = space.getParentid();
			while (!StringUtil.isEmpty(parentid)) {
				SpaceBean parent = SpaceHandler.get(parentid);
				if (parent == null) {
					break;
				}

				JSONObject parent_format = SpaceHandler.getFormat(parent.getId());

				List<SpaceBean> childs = getChilds(parent.getId());

				List<JSONObject> child_formats = new ArrayList<JSONObject>();
				for (SpaceBean child : childs) {
					JSONObject child_format = SpaceHandler.getFormat(child.getId());
					child_formats.add(child_format);
				}
				parent_format.put("childs", child_formats);
				parents.add(parent_format);
				parentid = parent.getParentid();
			}
		}
		List<JSONObject> res = new ArrayList<JSONObject>();
		for (int i = parents.size() - 1; i >= 0; i--) {
			res.add(parents.get(i));
		}
		return res;

	}

	@Override
	public int delete(Serializable id) throws Exception {
		SpaceHandler.remove(String.valueOf(id));
		return super.delete(id);
	}

	@Override
	public SpaceBean delete(Client client, SpaceBean t) throws Exception {
		if (t != null) {
			SpaceHandler.remove(t.getId());
		}
		return super.delete(client, t);
	}

	@Override
	public boolean isRepositorys(SpaceBean space) {
		return SpaceHandler.isRepositorys(space);
	}

}

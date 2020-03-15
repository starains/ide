package com.teamide.ide.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.PageResultBean;
import com.teamide.client.ClientSession;
import com.teamide.db.bean.PageSqlParam;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.enums.PublicType;
import com.teamide.ide.enums.SpaceType;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.handler.UserHandler;
import com.teamide.ide.service.IUserService;
import com.teamide.ide.util.PasswordMD5Tool;

@Resource
public class UserService extends BaseService<UserBean> implements IUserService {

	@Override
	public UserBean register(UserBean user) throws Exception {

		return save(null, user);
	}

	@Override
	public UserBean save(ClientSession session, UserBean user) throws Exception {

		if (user == null) {
			return null;
		}
		String tablename = IDEFactory.getRealtablename(UserBean.class, (JSONObject) JSONObject.toJSON(user));
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}
		boolean isInsert = false;
		if (StringUtil.isEmpty(user.getId())) {
			if (StringUtil.isEmpty(user.getPassword())) {
				user.setPassword(PasswordMD5Tool.getPasswordMD5(IDEFactory.getDefaultPassword()));
			}
			String id = IDGenerateUtil.generate();
			user.setId(id);
			user.setStatus(1);
			isInsert = true;

			if (StringUtil.isEmpty(user.getLoginname()) && StringUtil.isNotEmpty(user.getName())) {
				user.setLoginname(user.getName());
			}
			if (StringUtil.isEmpty(user.getName()) && StringUtil.isNotEmpty(user.getLoginname())) {
				user.setName(user.getLoginname());
			}

		}

		if (StringUtil.isEmpty(user.getEmail())) {
			throw new Exception("邮箱不能为空！");
		}
		if (StringUtil.isEmpty(user.getName())) {
			throw new Exception("名称不能为空！");
		}
		if (StringUtil.isEmpty(user.getLoginname())) {
			throw new Exception("登录名称不能为空！");
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loginname", user.getLoginname());
		param.put("id", user.getId());
		param.put("email", user.getEmail());

		String sql = "SELECT COUNT(1) FROM " + tablename + " WHERE 1=1 ";
		sql += " AND loginname=:loginname ";
		sql += " AND id!=:id ";

		if (queryCount(sql, param) > 0) {
			throw new Exception("登录名称已经存在！");
		}

		sql = "SELECT COUNT(1) FROM " + tablename + " WHERE 1=1 ";
		sql += " AND email=:email ";
		sql += " AND id!=:id ";

		if (queryCount(sql, param) > 0) {
			throw new Exception("邮箱已经存在！");
		}

		if (isInsert) {

			SpaceBean space = new SpaceBean();
			space.setName(user.getLoginname());
			space.setPublictype(PublicType.OPEN.getValue());
			space.setType(SpaceType.USERS.getValue());
			space = new SpaceService().insert(session, space);

			user.setSpaceid(space.getId());
			user = super.insert(session, user);
		} else {
			user = super.update(session, user);
		}

		return user;
	}

	@Override
	public UserBean getByLogin(String loginname, String password) throws Exception {

		if (StringUtil.isEmpty(loginname)) {
			return null;
		}
		if (StringUtil.isEmpty(password)) {
			return null;

		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loginname", loginname);
		param.put("password", PasswordMD5Tool.getPasswordMD5(password));
		String tablename = IDEFactory.getRealtablename(UserBean.class, param);

		if (!StringUtil.isEmpty(tablename)) {
			String sql = "SELECT * FROM " + tablename + " WHERE 1=1 ";
			sql += " AND loginname=:loginname AND password=:password ";
			List<UserBean> users = queryList(UserBean.class, sql, param);
			if (users == null || users.size() == 0) {
				return null;
			}
			if (users.size() > 1) {
				throw new Exception("系统异常，包含多个用户！");
			}
			return users.get(0);
		}
		return null;
	}

	@Override
	public UserBean getBySpaceid(String spaceid) throws Exception {

		if (StringUtil.isEmpty(spaceid)) {
			return null;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("spaceid", spaceid);
		String tablename = IDEFactory.getRealtablename(UserBean.class, param);

		if (!StringUtil.isEmpty(tablename)) {
			String sql = "SELECT * FROM " + tablename + " WHERE 1=1 ";
			sql += " AND spaceid=:spaceid ";
			List<UserBean> users = queryList(UserBean.class, sql, param);
			if (users == null || users.size() == 0) {
				return null;
			}
			if (users.size() > 1) {
				throw new Exception("系统异常，包含多个用户！");
			}
			return users.get(0);
		}
		return null;
	}

	@Override
	public PageResultBean<Map<String, Object>> querySearch(String searchText, int pageindex, int pagesize)
			throws Exception {
		if (StringUtil.isEmpty(searchText)) {
			return null;
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("searchText", searchText);
		param.put("likeSearchText", "%" + searchText + "%");
		String tablename = IDEFactory.getRealtablename(UserBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}
		String whereSql = " WHERE 1=1 ";

		whereSql += " AND (name like :likeSearchText OR loginname like :likeSearchText)";

		String sql = "SELECT * FROM " + tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		for (Map<String, Object> one : page.getValue()) {
			JSONObject user_json = (JSONObject) JSONObject.toJSON(one);
			JSONObject json = UserHandler.getFormat(user_json.toJavaObject(UserBean.class));
			one.putAll(json);
		}
		return page;
	}

	public PageResultBean<Map<String, Object>> queryPage(JSONObject param, int pageindex, int pagesize)
			throws Exception {

		String tablename = IDEFactory.getRealtablename(UserBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}
		String whereSql = " WHERE 1=1 ";

		whereSql += "";

		String sql = "SELECT * FROM " + tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + tablename + "  " + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		PageResultBean<Map<String, Object>> page = queryPageResult(sqlParam);
		for (Map<String, Object> one : page.getValue()) {
			JSONObject user_json = (JSONObject) JSONObject.toJSON(one);
			JSONObject json = UserHandler.getFormat(user_json.toJavaObject(UserBean.class));
			one.putAll(json);
		}
		return page;
	}
}

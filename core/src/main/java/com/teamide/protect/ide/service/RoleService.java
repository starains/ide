package com.teamide.protect.ide.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.bean.PageResultBean;
import com.teamide.db.bean.PageSqlParam;
import com.teamide.db.bean.SqlParam;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.PermissionBean;
import com.teamide.ide.bean.RoleBean;
import com.teamide.ide.bean.RolePermissionBean;
import com.teamide.ide.bean.RoleUserBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.service.IRoleService;
import com.teamide.ide.service.impl.BaseService;

@Resource
public class RoleService extends BaseService<RoleBean> implements IRoleService {

	@Override
	public void addUserRole(String userid, String... roleids) throws Exception {

		if (StringUtil.isEmpty(userid) || roleids == null) {
			return;
		}
		for (String roleid : roleids) {
			if (queryRoleUserCount(roleid, userid) == 0) {
				RoleUserBean roleUser = new RoleUserBean();
				roleUser.setUserid(userid);
				roleUser.setRoleid(roleid);
				roleUser.setId(IDGenerateUtil.generate());
				insert(roleUser);
			}
		}

	}

	@Override
	public void addRoleUser(String roleid, String... userids) throws Exception {

		if (StringUtil.isEmpty(roleid) || userids == null) {
			return;
		}
		for (String userid : userids) {
			if (queryRoleUserCount(roleid, userid) == 0) {
				RoleUserBean roleUser = new RoleUserBean();
				roleUser.setUserid(userid);
				roleUser.setRoleid(roleid);
				roleUser.setId(IDGenerateUtil.generate());
				insert(roleUser);
			}
		}

	}

	@Override
	public void addRolePermission(String roleid, String... permissionids) throws Exception {

		if (StringUtil.isEmpty(roleid) || permissionids == null) {
			return;
		}
		for (String permissionid : permissionids) {
			if (queryRolePermissionCount(roleid, permissionid) == 0) {
				RolePermissionBean rolePermission = new RolePermissionBean();
				rolePermission.setPermissionid(permissionid);
				rolePermission.setRoleid(roleid);
				rolePermission.setId(IDGenerateUtil.generate());
				insert(rolePermission);
			}
		}

	}

	public int queryRoleUserCount(String roleid, String userid) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("roleid", roleid);
		param.put("userid", userid);
		String tablename = IDEFactory.getRealtablename(RoleUserBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return 0;
		}
		String sql = "SELECT COUNT(1) FROM " + tablename + " WHERE roleid=:roleid AND userid=:userid ";
		return queryCount(sql, param);
	}

	public int queryRolePermissionCount(String roleid, String permissionid) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("roleid", roleid);
		param.put("permissionid", permissionid);
		String tablename = IDEFactory.getRealtablename(RolePermissionBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return 0;
		}
		String sql = "SELECT COUNT(1) FROM " + tablename + " WHERE roleid=:roleid AND permissionid=:permissionid ";
		return queryCount(sql, param);
	}

	@Override
	public void deleteRoleUser(String roleid, String... userids) throws Exception {

		if (StringUtil.isEmpty(roleid) || userids == null) {
			return;
		}
		Map<String, Object> param = new HashMap<String, Object>();

		String tablename = IDEFactory.getRealtablename(RoleUserBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return;
		}
		for (String userid : userids) {
			param.put("roleid", roleid);
			param.put("userid", userid);
			if (!StringUtil.isEmpty(userid)) {
				String sql = "DELETE FROM " + tablename + " WHERE roleid=:roleid AND userid=:userid ";
				execute(sql, param);
			}
		}

	}

	@Override
	public void deleteUserRole(String userid, String... roleids) throws Exception {

		if (StringUtil.isEmpty(userid) || roleids == null) {
			return;
		}
		Map<String, Object> param = new HashMap<String, Object>();

		String tablename = IDEFactory.getRealtablename(RoleUserBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return;
		}
		for (String roleid : roleids) {
			param.put("roleid", roleid);
			param.put("userid", userid);
			if (!StringUtil.isEmpty(userid)) {
				String sql = "DELETE FROM " + tablename + " WHERE roleid=:roleid AND userid=:userid ";
				execute(sql, param);
			}
		}

	}

	@Override
	public void deleteRolePermission(String roleid, String... permissionids) throws Exception {

		if (StringUtil.isEmpty(roleid) || permissionids == null) {
			return;
		}
		Map<String, Object> param = new HashMap<String, Object>();

		String tablename = IDEFactory.getRealtablename(RolePermissionBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return;
		}
		for (String permissionid : permissionids) {
			param.put("roleid", roleid);
			param.put("permissionid", permissionid);
			if (!StringUtil.isEmpty(permissionid)) {
				String sql = "DELETE FROM " + tablename + " WHERE roleid=:roleid AND permissionid=:permissionid ";
				execute(sql, param);
			}
		}

	}

	public PageSqlParam getUserSqlParam(String roleid, Map<String, Object> param) throws Exception {

		if (param == null) {
			param = new HashMap<String, Object>();
		}

		String tablename = IDEFactory.getRealtablename(UserBean.class, param);
		String roleuser_tablename = IDEFactory.getRealtablename(RoleUserBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}

		String whereSql = getWhereSql(UserBean.class, param);
		if (!StringUtil.isEmpty(roleuser_tablename) && !StringUtil.isEmpty(roleid)) {
			param.put("roleid", roleid);
			whereSql += " AND id in (SELECT userid FROM " + roleuser_tablename + " WHERE roleid=:roleid) ";
		}
		String sql = "SELECT * FROM " + tablename + whereSql;
		String countSql = "SELECT COUNT(1) FROM " + tablename + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		return sqlParam;
	}

	public PageSqlParam getPermissionSqlParam(String roleid, Map<String, Object> param) throws Exception {

		if (param == null) {
			param = new HashMap<String, Object>();
		}

		String tablename = IDEFactory.getRealtablename(PermissionBean.class, param);
		String rolepermission_tablename = IDEFactory.getRealtablename(RolePermissionBean.class, param);
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}

		String whereSql = getWhereSql(PermissionBean.class, param);
		if (!StringUtil.isEmpty(rolepermission_tablename) && !StringUtil.isEmpty(roleid)) {
			param.put("roleid", roleid);
			whereSql += " AND id IN (SELECT permissionid FROM " + rolepermission_tablename + " WHERE roleid=:roleid) ";
		}
		String sql = "SELECT * FROM " + tablename + whereSql;
		String countSql = "SELECT COUNT(1) FROM " + tablename + whereSql;

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		return sqlParam;
	}

	@Override
	public PageResultBean<UserBean> queryUser(String roleid, Map<String, Object> param, int pageidex, int pagesize)
			throws Exception {

		PageSqlParam sqlParam = getUserSqlParam(roleid, param);

		if (sqlParam == null) {
			return new PageResultBean<UserBean>();
		}
		sqlParam.setPageindex(pageidex);
		sqlParam.setPagesize(pagesize);
		PageResultBean<UserBean> result = queryPageResult(UserBean.class, sqlParam);
		return result;
	}

	@Override
	public List<PermissionBean> queryPermission(String roleid, Map<String, Object> param) throws Exception {

		SqlParam sqlParam = getPermissionSqlParam(roleid, param);

		if (sqlParam != null) {
			return queryList(PermissionBean.class, sqlParam.getSql(), sqlParam.getParam());
		}
		return new ArrayList<PermissionBean>();
	}

	@Override
	public List<RoleBean> queryUserVisibleRoles(String userid) throws Exception {

		if (StringUtil.isEmpty(userid)) {
			return new ArrayList<RoleBean>();
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userid", userid);

		String role_tablename = IDEFactory.getRealtablename(RoleBean.class, param);
		String role_user_tablename = IDEFactory.getRealtablename(RoleUserBean.class, param);
		if (StringUtil.isEmpty(role_tablename) || StringUtil.isEmpty(role_user_tablename)) {
			return new ArrayList<RoleBean>();
		}
		String sql = "SELECT * FROM " + role_tablename + " WHERE 1=2 ";
		sql += " OR id IN ( SELECT roleid FROM " + role_user_tablename + " WHERE userid=:userid ) ";
		sql += " OR fordefault = 1 ";

		return queryList(RoleBean.class, sql, param);
	}

	@Override
	public void addUserSuperRole(String userid) throws Exception {

		if (StringUtil.isEmpty(userid)) {
			return;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userid", userid);

		String role_tablename = IDEFactory.getRealtablename(RoleBean.class, param);
		String role_user_tablename = IDEFactory.getRealtablename(RoleUserBean.class, param);
		if (StringUtil.isEmpty(role_tablename) || StringUtil.isEmpty(role_user_tablename)) {
			return;
		}

		String sql = "SELECT COUNT(1) FROM " + role_tablename + " WHERE ";
		sql += " id IN ( SELECT roleid FROM " + role_user_tablename + " WHERE userid=:userid ) ";
		sql += " AND forsuper = 1 ";

		if (queryCount(sql, param) == 0) {
			RoleBean role = new RoleBean();
			role.setForsuper(true);
			role.setName("超级管理员");
			role.setId(IDGenerateUtil.generate());
			role.setStatus(1);
			role.setSequence(1);
			role.setCreateuserid(userid);
			save(null, role);

			addUserRole(userid, role.getId());
		}

	}

}

package com.teamide.ide.service;

import java.util.List;
import java.util.Map;

import com.teamide.bean.PageResultBean;
import com.teamide.ide.bean.PermissionBean;
import com.teamide.ide.bean.RoleBean;
import com.teamide.ide.bean.UserBean;

public interface IRoleService extends IBaseService<RoleBean> {

	public void addUserRole(String userid, String... roleids) throws Exception;

	public void addRoleUser(String roleid, String... userids) throws Exception;

	public void deleteRoleUser(String roleid, String... userids) throws Exception;

	public void deleteUserRole(String userid, String... roleids) throws Exception;

	public void addRolePermission(String roleid, String... permissionids) throws Exception;

	public void addUserSuperRole(String userid) throws Exception;

	public void deleteRolePermission(String roleid, String... permissionids) throws Exception;

	public List<RoleBean> queryUserVisibleRoles(String userid) throws Exception;

	public PageResultBean<UserBean> queryUser(String roleid, Map<String, Object> param, int pageindex, int pagesize)
			throws Exception;

	public List<PermissionBean> queryPermission(String roleid, Map<String, Object> param) throws Exception;

}

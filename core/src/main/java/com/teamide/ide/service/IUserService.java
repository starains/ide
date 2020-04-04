package com.teamide.ide.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.PageResultBean;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.UserBean;

public interface IUserService extends IBaseService<UserBean> {

	public UserBean register(UserBean user) throws Exception;

	public UserBean getByLogin(String loginname, String password) throws Exception;

	public UserBean getBySpaceid(String spaceid) throws Exception;

	public PageResultBean<Map<String, Object>> querySearch(ClientSession session, String searchText, int pageindex,
			int pagesize) throws Exception;

	public PageResultBean<Map<String, Object>> queryPage(ClientSession session, JSONObject param, int pageindex,
			int pagesize) throws Exception;

}

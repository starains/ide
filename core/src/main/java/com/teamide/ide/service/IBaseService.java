package com.teamide.ide.service;

import java.util.Map;

import com.teamide.service.ITService;
import com.teamide.bean.PageResultBean;
import com.teamide.client.ClientSession;

public interface IBaseService<T> extends ITService<T> {

	public T save(ClientSession session, T t) throws Exception;

	public T insert(ClientSession session, T t) throws Exception;

	public T update(ClientSession session, T t) throws Exception;

	public T delete(ClientSession session, T t) throws Exception;

	public PageResultBean<T> queryPage(Map<String, Object> param, int pageindex, int pagesize) throws Exception;

	public boolean findIgnoreId(String id, Map<String, Object> param) throws Exception;
}

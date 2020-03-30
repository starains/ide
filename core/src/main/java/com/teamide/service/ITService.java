package com.teamide.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.teamide.bean.PageResultBean;
import com.teamide.param.PageSqlParam;

public interface ITService<T> extends IService {

	public Class<T> getTClass();

	public T get(Serializable id) throws Exception;

	public int delete(Serializable id) throws Exception;

	public int delete(Map<String, Object> param) throws Exception;

	public List<T> queryList(Map<String, Object> param) throws Exception;

	public int queryCount(Map<String, Object> param) throws Exception;

	public PageResultBean<T> queryPage(PageSqlParam pageSqlParam) throws Exception;
}

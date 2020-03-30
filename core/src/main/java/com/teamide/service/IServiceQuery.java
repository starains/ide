package com.teamide.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.teamide.bean.PageResultBean;
import com.teamide.param.PageSqlParam;
import com.teamide.param.SqlParam;

public interface IServiceQuery {

	public <T> T get(Class<T> clazz, Serializable id) throws Exception;

	public <T> T get(Class<T> clazz, String tablename, Serializable id) throws Exception;

	public <T> T get(Class<T> clazz, String tablename, String key, Serializable id) throws Exception;

	public Map<String, Object> get(String tablename, String key, Serializable id) throws Exception;

	public Map<String, Object> queryOne(String sql, Map<String, Object> param) throws Exception;

	public <T> T queryOne(Class<T> clazz, Map<String, Object> param) throws Exception;

	public <T> T queryOne(Class<T> clazz, String sql, Map<String, Object> param) throws Exception;

	public <T> List<T> queryList(Class<T> clazz, Map<String, Object> param) throws Exception;

	public <T> List<T> queryList(Class<T> clazz, String sql, Map<String, Object> param) throws Exception;

	public List<Map<String, Object>> queryList(SqlParam sqlParam) throws Exception;

	public List<Map<String, Object>> queryList(String sql, Map<String, Object> param) throws Exception;

	public PageResultBean<Map<String, Object>> queryPageResult(PageSqlParam pageSqlParam) throws Exception;

	public <T> PageResultBean<T> queryPageResult(Class<T> clazz, PageSqlParam pageSqlParam) throws Exception;

}

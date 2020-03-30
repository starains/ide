package com.teamide.service;

import java.io.Serializable;
import java.util.Map;

public interface IServiceDelete {

	public <T> int delete(Class<T> clazz, Serializable id, Object object) throws Exception;

	public int delete(String tablename, String key, Serializable id) throws Exception;

	public <T> int delete(Class<T> clazz, Map<String, Object> param) throws Exception;
}

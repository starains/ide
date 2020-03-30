package com.teamide.service;

import java.util.List;
import java.util.Map;

public interface IServiceSave {

	public <T> T insert(Object object) throws Exception;

	public <T> T insert(Object object, String tablename) throws Exception;

	public <T> T update(Object object) throws Exception;

	public <T> T update(Object object, String tablename) throws Exception;

	public <T> T insertOrUpdate(Object object) throws Exception;

	public <T> T insertOrUpdate(Object object, String tablename) throws Exception;

	public int executeUpdate(Object object) throws Exception;

	public int executeUpdate(Object object, String tablename) throws Exception;

	public int executeUpdates(List<?> objects) throws Exception;

	public int executeUpdates(List<?> objects, String tablename) throws Exception;

	public int executeUpdate(String tablename, Map<String, Object> keyValues, Map<String, Object> param)
			throws Exception;

	public int executeInsert(Object object) throws Exception;

	public int executeInsert(Object object, String tablename) throws Exception;

	public int executeInserts(List<?> objects) throws Exception;

	public int executeInserts(List<?> objects, String tablename) throws Exception;

	public int executeInsert(String tablename, Map<String, Object> param) throws Exception;

	public int executeInsertOrUpdates(List<?> objects) throws Exception;

	public int executeInsertOrUpdates(List<?> objects, String tablename) throws Exception;

}

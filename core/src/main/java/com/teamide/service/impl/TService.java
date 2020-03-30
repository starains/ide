package com.teamide.service.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.teamide.bean.PageResultBean;
import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.param.PageSqlParam;
import com.teamide.service.ITService;

public class TService<T> extends Service implements ITService<T> {

	private final Class<T> tClass;

	@Override
	public Class<T> getTClass() {

		return tClass;
	}

	@SuppressWarnings("unchecked")
	public TService(DBDataSource dbDataSource, IDao dao) {
		super(dbDataSource, dao);
		tClass = (Class<T>) getSuperClassGenricType(this.getClass());
	}

	@SuppressWarnings("unchecked")
	public TService(DBDataSource dbDataSource) {

		super(dbDataSource);
		tClass = (Class<T>) getSuperClassGenricType(this.getClass());
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
	 * GenricManager<Book>
	 * 
	 * @param clazz
	 *            The class to introspect
	 * @return the first generic declaration, or <code>Object.class</code> if
	 *         cannot be determined
	 */
	public Class<?> getSuperClassGenricType(Class<?> clazz) {

		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
	 * GenricManager<Book>
	 * 
	 * @param clazz
	 *            clazz The class to introspect
	 * @param index
	 *            the Index of the generic ddeclaration,start from 0.
	 */
	public Class<?> getSuperClassGenricType(Class<?> clazz, int index) throws IndexOutOfBoundsException {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class<?>) params[index];
	}

	@Override
	public T get(Serializable id) throws Exception {

		return get(tClass, id);
	}

	@Override
	public int delete(Serializable id) throws Exception {

		return delete(tClass, id, null);
	}

	@Override
	public int delete(Map<String, Object> param) throws Exception {

		return delete(tClass, param);
	}

	@Override
	public List<T> queryList(Map<String, Object> param) throws Exception {

		return queryList(tClass, param);
	}

	@Override
	public int queryCount(Map<String, Object> param) throws Exception {

		return queryCount(tClass, param);
	}

	@Override
	public PageResultBean<T> queryPage(PageSqlParam pageSqlParam) throws Exception {

		return queryPageResult(tClass, pageSqlParam);
	}

}

package com.teamide.ide.service.impl;

import javax.annotation.Resource;

import com.teamide.bean.PageResultBean;
import com.teamide.db.bean.PageSqlParam;
import com.teamide.ide.bean.SpaceOptionBean;
import com.teamide.ide.service.ISpaceOptionService;

@Resource
public class SpaceOptionService extends BaseService<SpaceOptionBean> implements ISpaceOptionService {

	@Override
	public <T> PageResultBean<T> queryPageResult(Class<T> clazz, PageSqlParam pageSqlParam) throws Exception {

		String completetablename = getTablename(clazz, pageSqlParam.getParam());
		String countsql = "SELECT COUNT(1) FROM " + completetablename + " ";
		String sql = "SELECT * FROM " + completetablename + " ";
		String wheresql = getWhereSql(clazz, pageSqlParam.getParam());
		countsql += wheresql;
		sql += wheresql;
		sql += " ORDER BY createtime DESC ";
		PageSqlParam sqlParam = new PageSqlParam(sql, countsql, pageSqlParam.getParam());
		sqlParam.setPageindex(pageSqlParam.getPageindex());
		sqlParam.setPagesize(pageSqlParam.getPagesize());

		return super.queryPageResult(clazz, sqlParam);
	}
}

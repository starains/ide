package com.teamide.protect.ide.service;

import javax.annotation.Resource;

import com.teamide.bean.PageResultBean;
import com.teamide.client.ClientSession;
import com.teamide.db.bean.PageSqlParam;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.enums.WorkspaceControl;
import com.teamide.ide.service.ISpaceEventService;
import com.teamide.ide.service.impl.BaseService;

@Resource
public class SpaceEventService extends BaseService<SpaceEventBean> implements ISpaceEventService {

	@Override
	public SpaceEventBean append(ClientSession client, WorkspaceControl control, String name, SpaceBean space)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

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

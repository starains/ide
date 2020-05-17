package com.teamide.ide.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import com.teamide.bean.PageResultBean;
import com.teamide.ide.bean.UserLoginBean;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.param.PageSqlParam;
import com.teamide.util.StringUtil;

@Resource
public class UserLoginService extends BaseService<UserLoginBean> {

	public PageResultBean<UserLoginBean> queryPage(Map<String, Object> param, int pageindex, int pagesize)
			throws Exception {
		String tablename = IDEFactory.getRealtablename(getTClass(), param);
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}

		String whereSql = getWhereSql(getTClass(), param);

		String sql = "SELECT *  FROM " + tablename + "  " + whereSql;
		String countSql = "SELECT count(1) FROM " + tablename + "  " + whereSql;

		sql += " ORDER BY createtime DESC ";

		PageSqlParam sqlParam = new PageSqlParam(sql, countSql, param);
		sqlParam.setPageindex(pageindex);
		sqlParam.setPagesize(pagesize);

		return queryPageResult(getTClass(), sqlParam);
	}
}

package com.teamide.ide.service.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.service.impl.TService;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.BaseBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.service.IBaseService;

public class BaseService<T> extends TService<T> implements IBaseService<T> {

	/** 标准日期格式：yyyyMMddHHmmss */
	public final static String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
	/** 标准日期格式 {@link SimpleDateFormat}：yyyyMMddHHmmss */
	public final static SimpleDateFormat PURE_DATETIME_FORMAT = new SimpleDateFormat(PURE_DATETIME_PATTERN);

	public BaseService() {

		super(IDEFactory.getService().getDao().getDataSourceFactory());
	}

	public T save(Client client, T t) throws Exception {
		if (find(t, null)) {
			return update(client, t);
		}
		return insert(client, t);
	}

	public T insert(Client client, T t) throws Exception {
		if (t != null && t instanceof BaseBean) {
			BaseBean base = (BaseBean) t;
			base.setCreatetime(PURE_DATETIME_FORMAT.format(new Date()));
			if (client != null && client.getUser() != null) {
				base.setCreateuserid(client.getUser().getId());
			}
		}
		return super.insert(t);
	}

	public T update(Client client, T t) throws Exception {
		if (t != null && t instanceof BaseBean) {
			BaseBean base = (BaseBean) t;
			if (StringUtil.isEmpty(base.getId())) {
				throw new Exception("编号丢失！");
			}
			base.setUpdatetime(PURE_DATETIME_FORMAT.format(new Date()));
			if (client != null && client.getUser() != null) {
				base.setUpdateuserid(client.getUser().getId());
			}
		}
		return super.update(t);
	}

	public boolean findIgnoreId(String id, Map<String, Object> param) throws Exception {

		String tablename = IDEFactory.getRealtablename(getTClass(), (JSONObject) JSONObject.toJSON(param));
		String whereSql = getWhereSql(getTClass(), param);
		String sql = "SELECT COUNT(1) FROM " + tablename;
		sql += " " + whereSql;
		if (!StringUtil.isEmpty(id)) {
			sql += " AND id!=:id ";
			param.put("id", id);
		}
		return queryCount(sql, param) > 0;

	}

	@Override
	public T delete(Client client, T t) throws Exception {
		if (t == null) {
			return t;
		}
		if (t instanceof BaseBean) {
			BaseBean bean = (BaseBean) t;
			t = get((Serializable) bean.getId());
			super.delete((Serializable) bean.getId());
		}
		return t;
	}
}

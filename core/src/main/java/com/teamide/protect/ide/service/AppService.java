package com.teamide.protect.ide.service;
//package com.coospro.ide.code.service;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Resource;
//
//import com.coospro.bean.PageResultBean;
//import com.coospro.bean.SqlParam;
//import com.coospro.ide.bean.AppBean;
//import com.coospro.ide.bean.AppTeamBean;
//import com.coospro.ide.client.Client;
//import com.coospro.ide.enums.AppTeamPermission;
//import com.coospro.ide.enums.AppPublicType;
//import com.coospro.ide.factory.IDEFactory;
//import com.coospro.ide.service.IAppService;
//import com.coospro.ide.service.impl.BaseService;
//import com.coospro.util.StringUtil;
//
//@Resource
//public class AppService extends BaseService<AppBean> implements IAppService {
//
//	@Override
//	public AppBean get(String appid) throws Exception {
//
//		if (StringUtil.isEmpty(appid)) {
//			return null;
//		}
//		return get(AppBean.class, (Serializable) appid);
//
//	}
//
//	@Override
//	public AppBean getByAccountidAndPath(String accountid, String path) throws Exception {
//
//		if (StringUtil.isEmpty(accountid) || StringUtil.isEmpty(path)) {
//			return null;
//		}
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("accountid", accountid);
//		param.put("path", path);
//		String tablename = IDEFactory.getRealtablename(AppBean.class, param);
//		if (StringUtil.isEmpty(tablename)) {
//			return null;
//		}
//		List<AppBean> list = queryList(AppBean.class, param);
//		if (list == null || list.size() == 0) {
//			return null;
//		}
//		return list.get(0);
//
//	}
//
//	@Override
//	public List<AppBean> getListByAccountid(String accountid) throws Exception {
//
//		if (StringUtil.isEmpty(accountid)) {
//			return null;
//		}
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("accountid", accountid);
//		String tablename = IDEFactory.getRealtablename(AppBean.class, param);
//		if (StringUtil.isEmpty(tablename)) {
//			return null;
//		}
//		List<AppBean> list = queryList(AppBean.class, param);
//		return list;
//
//	}
//
//	@Override
//	public AppTeamPermission getPermission(Client client, AppBean app) throws Exception {
//
//		AppTeamPermission permission = null;
//		if (client.isLogin()) {
//			String accountid = client.getLoginAccount().getId();
//			String appid = app.getId();
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("appid", appid);
//			param.put("accountid", accountid);
//
//			List<AppTeamBean> temp = queryList(AppTeamBean.class, param);
//			if (temp != null && temp.size() > 0) {
//				try {
//					permission = AppTeamPermission.valueOf(temp.get(0).getPermission());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		if (permission == null) {
//			if (String.valueOf(AppPublicType.OPEN.getValue()).equalsIgnoreCase(app.getPublictype())) {
//				permission = AppTeamPermission.VIEWER;
//			}
//		}
//		return permission;
//	}
//
//	@Override
//	public PageResultBean<AppBean> queryTeamApp(String accountid, Map<String, Object> param, int pageindex,
//			int pagesize)
//			throws Exception {
//
//		if (StringUtil.isEmpty(accountid)) {
//			throw new Exception("accountid is null.");
//		}
//		String app_tablename = IDEFactory.getRealtablename(AppBean.class, param);
//		if (StringUtil.isEmpty(app_tablename)) {
//			throw new Exception("app table name is null.");
//		}
//		String appteam_tablename = IDEFactory.getRealtablename(AppTeamBean.class, param);
//		if (StringUtil.isEmpty(appteam_tablename)) {
//			throw new Exception("app team table name is null.");
//		}
//		param.put("accountid", accountid);
//		String whereSql = getWhereSql(AppBean.class, param);
//		whereSql += " AND id IN (SELECT appid FROM " + appteam_tablename + " WHERE accountid=:accountid ) ";
//		String sql = "SELECT * FROM " + app_tablename + " " + whereSql;
//		String countSql = "SELECT COUNT(1) FROM " + app_tablename + " " + whereSql;
//		SqlParam sqlParam = new SqlParam(sql, param);
//		sqlParam.setCountSql(countSql);
//		sqlParam.set_pageindex(pageindex);
//		sqlParam.set_pagesize(pagesize);
//		return queryPageResult(AppBean.class, sqlParam);
//	}
//
//	@Override
//	public PageResultBean<AppBean> queryAccountApp(String accountid, Map<String, Object> param, int pageindex,
//			int pagesize)
//			throws Exception {
//
//		if (StringUtil.isEmpty(accountid)) {
//			throw new Exception("accountid is null.");
//		}
//		param.put("createaccountid", accountid);
//		return queryPageResult(AppBean.class, param, pageindex, pagesize);
//	}
//}

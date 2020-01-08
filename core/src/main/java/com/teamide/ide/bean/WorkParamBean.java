package com.teamide.ide.bean;
//package com.coospro.ide.bean;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import com.alibaba.fastjson.JSONObject;
//import com.coospro.ide.cache.SpaceCache;
//import com.coospro.ide.client.Client;
//import com.coospro.ide.enums.SpaceType;
//import com.coospro.ide.enums.WorkspaceControl;
//import com.coospro.util.StringUtil;
//
//public class WorkParamBean {
//
//	private final String spaceid;
//
//	private final String folder;
//
//	private final String servletpath;
//
//	private final String branch;
//
//	private final SpaceBean space;
//
//	private final WorkspaceControl control;
//
//	private final Client client;
//
//	private int pagesize;
//
//	private int pageindex;
//
//	private JSONObject param;
//
//	private final List<SpaceBean> parents = new ArrayList<SpaceBean>();
//
//	private final Set<String> searched_spaceids = new HashSet<String>();
//
//	public WorkParamBean(String spaceid, String branch, Client client, WorkspaceControl control) {
//		this.spaceid = spaceid;
//		this.control = control;
//		this.client = client;
//		if (StringUtil.isEmpty(branch)) {
//			branch = "master";
//		}
//		this.branch = branch;
//		SpaceBean space = null;
//		String folder = "";
//		String servletpath = "";
//
//		if (StringUtil.isNotEmpty(spaceid)) {
//			try {
//				searched_spaceids.add(spaceid);
//				space = SpaceCache.get(spaceid);
//				if (space != null) {
//					folder = space.getName() + "/";
//					servletpath = space.getName();
//
//					String parentid = space.getParentid();
//					SpaceBean parent = null;
//					while (StringUtil.isNotEmpty(parentid)) {
//						searched_spaceids.add(parentid);
//						parent = SpaceCache.get(parentid);
//						parentid = null;
//						if (parent != null) {
//							parents.add(parent);
//							folder = parent.getName() + "/" + folder;
//							servletpath = parent.getName() + "/" + servletpath;
//							if (StringUtil.isNotEmpty(parent.getParentid())) {
//								if (searched_spaceids.contains(parent.getParentid())) {
//									break;
//								}
//								parentid = parent.getParentid();
//							}
//						}
//					}
//					SpaceBean s = parent;
//					if (s == null) {
//						s = space;
//					}
//					if (s != null) {
//						SpaceType parentType = SpaceType.valueOf(s.getType());
//						if (parentType != SpaceType.USERS) {
//							servletpath = parentType.getValue().toLowerCase() + "/" + servletpath;
//							folder = parentType.getValue().toLowerCase() + "/" + folder;
//						}
//						servletpath = "/" + servletpath;
//					}
//
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		this.folder = folder;
//		this.servletpath = servletpath;
//		this.space = space;
//	}
//
//	public Client getClient() {
//		return client;
//	}
//
//	public String getSpaceid() {
//		return spaceid;
//	}
//
//	public String getFolder() {
//		return folder;
//	}
//
//	public SpaceBean getSpace() {
//		return space;
//	}
//
//	public List<SpaceBean> getParents() {
//		return parents;
//	}
//
//	public String getBranch() {
//		return branch;
//	}
//
//	public int getPagesize() {
//		return pagesize;
//	}
//
//	public void setPagesize(int pagesize) {
//		this.pagesize = pagesize;
//	}
//
//	public int getPageindex() {
//		return pageindex;
//	}
//
//	public void setPageindex(int pageindex) {
//		this.pageindex = pageindex;
//	}
//
//	public JSONObject getParam() {
//		return param;
//	}
//
//	public void setParam(JSONObject param) {
//		this.param = param;
//	}
//
//	public WorkspaceControl getControl() {
//		return control;
//	}
//
//	public String getServletpath() {
//		return servletpath;
//	}
//
//}

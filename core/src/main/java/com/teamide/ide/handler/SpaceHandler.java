package com.teamide.ide.handler;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceTeamBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.enums.PublicType;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.enums.SpaceTeamType;
import com.teamide.ide.enums.SpaceType;
import com.teamide.ide.param.SpaceFormatParam;
import com.teamide.ide.service.ISpaceService;
import com.teamide.ide.service.impl.SpaceService;

public class SpaceHandler {

	static final Map<String, SpaceBean> SPACE_CACHE = new HashMap<String, SpaceBean>();

	static final Map<String, List<SpaceTeamBean>> SPACE_TEAMS_CACHE = new HashMap<String, List<SpaceTeamBean>>();

	static final Object LOAD_SPACES_LOCK = new Object();

	public static void reloadSpaces() {
		SPACE_CACHE.clear();
		SPACE_TEAMS_CACHE.clear();
		loadSpaces();

	}

	public static void loadSpaces() {
		if (SPACE_CACHE.size() > 0) {
			return;
		}
		synchronized (LOAD_SPACES_LOCK) {
			if (SPACE_CACHE.size() > 0) {
				return;
			}
			try {
				ISpaceService spaceService = new SpaceService();
				List<SpaceBean> spaces = spaceService.queryList(new HashMap<String, Object>());
				for (SpaceBean space : spaces) {
					SPACE_CACHE.put(space.getId(), space);

					Map<String, Object> param = new HashMap<String, Object>();
					param.put("spaceid", space.getId());
					List<SpaceTeamBean> teams = spaceService.queryList(SpaceTeamBean.class, param);
					SPACE_TEAMS_CACHE.put(space.getId(), teams);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void remove(String spaceid) {
		SPACE_CACHE.remove(spaceid);
		SPACE_TEAMS_CACHE.remove(spaceid);
	}

	public static SpaceBean get(String spaceid) {
		if (StringUtil.isEmpty(spaceid)) {
			return null;
		}

		loadSpaces();

		if (SPACE_CACHE.get(spaceid) != null) {
			return SPACE_CACHE.get(spaceid);
		}

		try {
			ISpaceService spaceService = new SpaceService();
			SpaceBean space = spaceService.get(spaceid);
			if (space != null) {
				SPACE_CACHE.put(space.getId(), space);

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("spaceid", space.getId());
				List<SpaceTeamBean> teams = spaceService.queryList(SpaceTeamBean.class, param);
				SPACE_TEAMS_CACHE.put(space.getId(), teams);
			}
			return space;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SpaceFormatParam getFormat(String spaceid, ClientSession session) {
		SpaceBean space = get(spaceid);
		return getFormat(space, session);
	}

	public static SpaceFormatParam getFormat(SpaceBean space, ClientSession session) {
		if (space == null) {
			return null;
		}

		return getSpaceFormatParam(space, session);

	}

	private static SpaceFormatParam getSpaceFormatParam(SpaceBean space, ClientSession session) {
		if (space == null) {
			return null;
		}
		JSONObject json = (JSONObject) JSON.toJSON(space);
		SpaceFormatParam param = json.toJavaObject(SpaceFormatParam.class);
		String root = "";
		String servletpath = "";

		Set<String> searched_spaceids = new HashSet<String>();
		searched_spaceids.add(space.getId());

		root = space.getName() + "/";
		servletpath = space.getName();
		SpaceType lastType = SpaceType.valueOf(space.getType());
		SpaceBean parent = get(space.getParentid());
		while (parent != null && parent != space) {
			lastType = SpaceType.valueOf(parent.getType());
			searched_spaceids.add(parent.getId());
			root = parent.getName() + "/" + root;
			servletpath = parent.getName() + "/" + servletpath;
			if (StringUtil.isNotEmpty(parent.getParentid())) {
				if (searched_spaceids.contains(parent.getParentid())) {
					break;
				}
				parent = get(parent.getParentid());
			} else {
				parent = null;
			}
		}
		if (lastType != SpaceType.USERS) {
			servletpath = lastType.getValue().toLowerCase() + "/" + servletpath;
			root = lastType.getValue().toLowerCase() + "/" + root;
		}
		servletpath = "/" + servletpath;

		param.setRoot(root);
		param.setServletpath(servletpath);
		param.setPermission(getPermission(space, session, false));
		return param;
	}

	public static File getSpaceRootFolder() {
		File spaceRootFolder = new File(IDEConstant.SPACE_FOLDER);
		return spaceRootFolder;
	}

	private static SpacePermission getPermission(SpaceBean space, ClientSession session, boolean isParent) {
		if (space == null) {
			return null;
		}
		SpacePermission permission = null;
		if (session != null && session.isLogin()) {
			String userid = session.getUser().getId();
			if (isUsers(space)) {
				if (space.getId().equalsIgnoreCase(session.getCache("user", UserBean.class).getSpaceid())) {
					return SpacePermission.MASTER;
				}
			}

			List<SpaceTeamBean> teams = SPACE_TEAMS_CACHE.get(space.getId());
			if (teams != null && teams.size() > 0) {
				for (SpaceTeamBean team : teams) {
					if (SpaceTeamType.USERS.getValue().equalsIgnoreCase(team.getType())) {
						if (userid.equals(team.getRecordid())) {
							permission = SpacePermission.valueOf(team.getPermission());
							break;
						}
					}
				}
			}
		}

		if (permission == null) {
			if (!StringUtil.isEmpty(space.getParentid()) && !space.getParentid().equals(space.getId())) {
				SpaceBean parent = get(space.getParentid());
				if (parent != null) {
					permission = getPermission(parent, session, true);
				}
			}
		}
		if (permission == null && !isParent) {
			if (String.valueOf(PublicType.OPEN.getValue()).equalsIgnoreCase(space.getPublictype())) {
				permission = SpacePermission.VIEWER;
			}
		}
		return permission;
	}

	public static boolean isRepositorys(SpaceBean space) {
		if (space == null || StringUtil.isEmpty(space.getType())) {
			return false;
		}
		if (SpaceType.REPOSITORYS.getValue().equalsIgnoreCase(space.getType())) {
			return true;
		}
		return false;

	}

	public static boolean isUsers(SpaceBean space) {
		if (space == null || StringUtil.isEmpty(space.getType())) {
			return false;
		}
		if (SpaceType.USERS.getValue().equalsIgnoreCase(space.getType())) {
			return true;
		}
		return false;

	}
}

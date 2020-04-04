package com.teamide.ide.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.exception.NoPermissionException;
import com.teamide.ide.processor.enums.ProjectProcessorType;
import com.teamide.ide.processor.enums.RepositoryProcessorType;
import com.teamide.ide.processor.enums.SpaceProcessorType;

public class SpacePermissionHandler {

	public final static Map<SpacePermission, List<SpaceProcessorType>> SPACE_PERMISSIONS = new HashMap<SpacePermission, List<SpaceProcessorType>>();
	public final static Map<SpacePermission, List<RepositoryProcessorType>> REPOSITORY_PERMISSIONS = new HashMap<SpacePermission, List<RepositoryProcessorType>>();
	public final static Map<SpacePermission, List<ProjectProcessorType>> PROJECT_PERMISSIONS = new HashMap<SpacePermission, List<ProjectProcessorType>>();

	static {
		for (SpacePermission permission : SpacePermission.values()) {
			List<SpaceProcessorType> spaces = new ArrayList<SpaceProcessorType>();
			List<RepositoryProcessorType> repositorys = new ArrayList<RepositoryProcessorType>();
			List<ProjectProcessorType> projects = new ArrayList<ProjectProcessorType>();
			switch (permission) {
			case MASTER:
				spaces.addAll(SpaceProcessorType.getList());
				repositorys.addAll(RepositoryProcessorType.getList());
				projects.addAll(ProjectProcessorType.getList());
				break;
			case DEVELOPER:
				repositorys.addAll(RepositoryProcessorType.getList());
				repositorys.remove(RepositoryProcessorType.BRANCH_CREATE);
				repositorys.remove(RepositoryProcessorType.GIT_REMOTE_ADD);

				projects.addAll(ProjectProcessorType.getList());
				break;
			case VIEWER:
				projects.add(ProjectProcessorType.FILE_CLOSE);
				projects.add(ProjectProcessorType.FILE_OPEN);
				break;
			case NO:
				break;

			}
			SPACE_PERMISSIONS.put(permission, spaces);
			REPOSITORY_PERMISSIONS.put(permission, repositorys);
			PROJECT_PERMISSIONS.put(permission, projects);
		}

	}

	public static List<SpaceProcessorType> getSpacePermissions(SpacePermission permission) {
		return SPACE_PERMISSIONS.get(permission);
	}

	public static List<RepositoryProcessorType> getRepositoryPermissions(SpacePermission permission) {
		return REPOSITORY_PERMISSIONS.get(permission);
	}

	public static List<ProjectProcessorType> getProjectPermissions(SpacePermission permission) {
		return PROJECT_PERMISSIONS.get(permission);
	}

	public static void checkPermission(SpaceProcessorType type, SpacePermission permission) throws Exception {
		if (permission == null || permission == SpacePermission.NO) {
			throw new NoPermissionException("无权限操作！");
		}
		List<SpaceProcessorType> permissions = getSpacePermissions(permission);
		if (permissions.indexOf(type) < 0) {
			throw new NoPermissionException("无权限操作！");
		}
	}

	public static void checkPermission(RepositoryProcessorType type, SpacePermission permission) throws Exception {
		if (permission == null || permission == SpacePermission.NO) {
			throw new NoPermissionException("无权限操作！");
		}
		List<RepositoryProcessorType> permissions = getRepositoryPermissions(permission);
		if (permissions.indexOf(type) < 0) {
			throw new NoPermissionException("无权限操作！");
		}
	}

	public static void checkPermission(ProjectProcessorType type, SpacePermission permission) throws Exception {
		if (permission == null || permission == SpacePermission.NO) {
			throw new NoPermissionException("无权限操作！");
		}
		List<ProjectProcessorType> permissions = getProjectPermissions(permission);
		if (permissions.indexOf(type) < 0) {
			throw new NoPermissionException("无权限操作！");
		}
	}

}

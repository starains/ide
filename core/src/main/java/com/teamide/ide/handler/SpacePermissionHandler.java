package com.teamide.ide.handler;

import java.util.ArrayList;
import java.util.List;

import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.exception.NoPermissionException;
import com.teamide.ide.processor.enums.ProjectProcessorType;
import com.teamide.ide.processor.enums.RepositoryProcessorType;
import com.teamide.ide.processor.enums.SpaceProcessorType;
import com.teamide.util.ObjectUtil;

public class SpacePermissionHandler {
	public static List<ProjectProcessorType> getProjectProcessors(SpacePermission permission) {
		List<ProjectProcessorType> projects = new ArrayList<ProjectProcessorType>();
		switch (permission) {
		case MASTER:
			projects.addAll(ProjectProcessorType.getList());
			break;
		case DEVELOPER:

			projects.addAll(ProjectProcessorType.getList());
			break;
		case VIEWER:
			projects.add(ProjectProcessorType.FILE_CLOSE);
			projects.add(ProjectProcessorType.FILE_OPEN);
			break;
		case NO:
			break;

		}
		IDEConfigure configure = IDEConfigure.get();
		if (configure != null) {
		}
		return projects;

	}

	public static List<RepositoryProcessorType> getRepositoryProcessors(SpacePermission permission) {
		List<RepositoryProcessorType> repositorys = new ArrayList<RepositoryProcessorType>();
		switch (permission) {
		case MASTER:
			repositorys.addAll(RepositoryProcessorType.getList());
			break;
		case DEVELOPER:
			repositorys.addAll(RepositoryProcessorType.getList());
			repositorys.remove(RepositoryProcessorType.BRANCH_CREATE);
			repositorys.remove(RepositoryProcessorType.GIT_REMOTE_ADD);
			break;
		case VIEWER:
			break;
		case NO:
			break;

		}
		IDEConfigure configure = IDEConfigure.get();
		if (configure != null) {

			if (ObjectUtil.isTrue(configure.getRepository().getProhibitstarter())) {
				repositorys.remove(RepositoryProcessorType.DELETE_STARTER_OPTION);
				repositorys.remove(RepositoryProcessorType.SET_STARTER_OPTION);
				repositorys.remove(RepositoryProcessorType.STARTER_START);
				repositorys.remove(RepositoryProcessorType.STARTER_STOP);
				repositorys.remove(RepositoryProcessorType.STARTER_REMOVE);
				repositorys.remove(RepositoryProcessorType.STARTER_LOG_CLEAN);
				repositorys.remove(RepositoryProcessorType.STARTER_DEPLOY);
			}
		}
		return repositorys;
	}

	public static List<SpaceProcessorType> getSpaceProcessors(SpacePermission permission) {
		List<SpaceProcessorType> spaces = new ArrayList<SpaceProcessorType>();
		switch (permission) {
		case MASTER:
			spaces.addAll(SpaceProcessorType.getList());
			break;
		case DEVELOPER:
			break;
		case VIEWER:
			break;
		case NO:
			break;

		}
		IDEConfigure configure = IDEConfigure.get();
		if (configure != null) {
			if (ObjectUtil.isTrue(configure.getSpace().getProhibitcreate())) {
				spaces.remove(SpaceProcessorType.SPACE_CREATE);
				spaces.remove(SpaceProcessorType.SPACE_DELETE);
				spaces.remove(SpaceProcessorType.SPACE_RENAME);
				spaces.remove(SpaceProcessorType.SPACE_UPDATE);
			}
		}
		return spaces;
	}

	public static List<SpaceProcessorType> getSpacePermissions(SpacePermission permission) {
		return getSpaceProcessors(permission);
	}

	public static List<RepositoryProcessorType> getRepositoryPermissions(SpacePermission permission) {
		return getRepositoryProcessors(permission);
	}

	public static List<ProjectProcessorType> getProjectPermissions(SpacePermission permission) {
		return getProjectProcessors(permission);
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

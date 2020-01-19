package com.teamide.ide.protect.service;
//package com.coospro.protect.ide.service;
//
//import javax.annotation.Resource;
//
//import com.coospro.ide.bean.WorkParamBean;
//import com.coospro.ide.service.IWorkspace;
//import com.coospro.protect.ide.workspace.WorkspaceWork;
//
//@Resource
//public class Workspace implements IWorkspace {
//
//	@Override
//	public Object work(WorkParamBean param) throws Exception {
//		return WorkspaceWork.work(param);
//	}
//
//	// @Override
//	// public List<WorkspaceControlBean> getControls(SpaceBean space, Client
//	// client) throws Exception {
//	// List<WorkspaceControlBean> controls = new
//	// ArrayList<WorkspaceControlBean>();
//	// if (space == null || client == null) {
//	// return controls;
//	// }
//	//
//	// SpacePermission permission = new SpaceService().getPermission(space,
//	// client);
//	// if (permission != null) {
//	// controls.add(WorkspaceControl.DATA_LOAD.getControl());
//	// if (IDEFactory.getSpaceService().isRepositorys(space)) {
//	// controls.add(WorkspaceControl.SPACE_CODE_INDEX.getControl());
//	// } else {
//	// controls.add(WorkspaceControl.SPACE_INDEX.getControl());
//	// }
//	// controls.add(WorkspaceControl.SPACE_EVENT_INDEX.getControl());
//	//
//	// if (!space.getType().equalsIgnoreCase(SpaceType.USERS.getValue())) {
//	// controls.add(WorkspaceControl.SPACE_TEAM_INDEX.getControl());
//	// controls.add(WorkspaceControl.SPACE_OPTION_INDEX.getControl());
//	// }
//	//
//	// switch (permission) {
//	// case MASTER:
//	// controls.add(WorkspaceControl.SPACE_DELETE.getControl());
//	// controls.add(WorkspaceControl.SPACE_UPDATE.getControl());
//	// break;
//	// case DEVELOPER:
//	// break;
//	// case VIEWER:
//	// break;
//	// default:
//	// break;
//	//
//	// }
//	// }
//	// return controls;
//	// }
//
//}

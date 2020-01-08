package com.teamide.ide.service;

import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.enums.WorkspaceControl;

public interface ISpaceEventService extends IBaseService<SpaceEventBean> {

	public SpaceEventBean append(ClientSession session, WorkspaceControl control, String name, SpaceBean space)
			throws Exception;
}

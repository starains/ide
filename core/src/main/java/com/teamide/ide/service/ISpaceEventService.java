package com.teamide.ide.service;

import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.enums.WorkspaceControl;

public interface ISpaceEventService extends IBaseService<SpaceEventBean> {

	public SpaceEventBean append(Client client, WorkspaceControl control, String name, SpaceBean space)
			throws Exception;
}

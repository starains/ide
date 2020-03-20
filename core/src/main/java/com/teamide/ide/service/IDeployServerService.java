package com.teamide.ide.service;

import com.teamide.ide.bean.DeployServerBean;

public interface IDeployServerService extends IBaseService<DeployServerBean> {
	public DeployServerBean getByToken(String token) throws Exception;
}

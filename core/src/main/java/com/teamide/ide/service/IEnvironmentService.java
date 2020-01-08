package com.teamide.ide.service;

import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.enums.EnvironmentType;;

public interface IEnvironmentService extends IBaseService<EnvironmentBean> {

	public EnvironmentBean getByVersion(EnvironmentType type, String version) throws Exception;

}

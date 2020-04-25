package com.teamide.ide.service;

import com.teamide.client.ClientSession;
import com.teamide.ide.bean.ConfigureBean;
import com.teamide.ide.configure.IDEConfigure;

public interface IConfigureService extends IBaseService<ConfigureBean> {

	public IDEConfigure get();

	public IDEConfigure save(ClientSession session, IDEConfigure configure) throws Exception;

}

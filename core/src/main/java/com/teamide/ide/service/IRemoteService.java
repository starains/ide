package com.teamide.ide.service;

import com.teamide.ide.bean.RemoteBean;

public interface IRemoteService extends IBaseService<RemoteBean> {

	public RemoteBean getByToken(String token) throws Exception;
}

package com.teamide.ide.service;

import java.util.Map;

import com.teamide.service.ITService;
import com.teamide.ide.client.Client;

public interface IBaseService<T> extends ITService<T> {

	public T save(Client client, T t) throws Exception;

	public T insert(Client client, T t) throws Exception;

	public T update(Client client, T t) throws Exception;

	public T delete(Client client, T t) throws Exception;

	public boolean findIgnoreId(String id, Map<String, Object> param) throws Exception;
}

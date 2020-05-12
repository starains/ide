package com.teamide.ide.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.ide.bean.CertificateBean;
import com.teamide.ide.enums.CertificateType;

@Resource
public class CertificateService extends BaseService<CertificateBean> {

	public List<CertificateBean> query(String userid, CertificateType type) throws Exception {
		return query(userid, type.name());
	}

	public List<CertificateBean> query(String userid, String type) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userid", userid);
		if (type != null) {
			param.put("type", type);
		}
		List<CertificateBean> certificates = queryList(param);
		return certificates;
	}

}

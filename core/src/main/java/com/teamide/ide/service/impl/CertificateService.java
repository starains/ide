package com.teamide.ide.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.bean.CertificateBean;
import com.teamide.ide.bean.SpaceRepositoryOptionBean;
import com.teamide.ide.enums.CertificateType;
import com.teamide.ide.util.AESTool;

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

	public static void main(String[] args) throws Exception {
		SpaceRepositoryOptionService optionService = new SpaceRepositoryOptionService();
		CertificateService certificateService = new CertificateService();

		List<SpaceRepositoryOptionBean> gitCertificateOptions = optionService.query(null, null, null, null,
				"GIT_CERTIFICATE");
		for (SpaceRepositoryOptionBean gitCertificateOption : gitCertificateOptions) {
			JSONObject option = gitCertificateOption.getJSONOption();
			List<SpaceRepositoryOptionBean> gitOptions = optionService.query(gitCertificateOption.getSpaceid(), null,
					null, null, "GIT");
			JSONObject gitOption = null;
			if (gitOptions.size() > 0) {
				gitOption = gitOptions.get(0).getJSONOption();
			}
			if (gitOption != null) {
				String url = gitOption.getString("url");
				String name = url.substring(0, url.indexOf("/", url.indexOf("//") + 3));

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("name", name);
				param.put("userid", gitCertificateOption.getCreateuserid());

				if (certificateService.queryCount(param) == 0) {
					CertificateBean certificate = new CertificateBean();
					certificate.setName(name);
					certificate.setType("GIT");
					certificate.setUserid(gitCertificateOption.getCreateuserid());
					certificate.setUsername(option.getString("username"));
					certificate.setPassword(AESTool.encode(option.getString("password")));
					certificate.setCreatetime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
					certificate.setCreateuserid(gitCertificateOption.getCreateuserid());
					certificateService.save(null, certificate);
				}
			}
		}

	}
}

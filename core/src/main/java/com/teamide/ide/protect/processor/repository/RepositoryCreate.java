package com.teamide.ide.protect.processor.repository;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceRepositoryOptionBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.service.SpaceRepositoryOptionService;

public class RepositoryCreate extends RepositoryBase {

	public RepositoryCreate(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONObject create() throws Exception {

		File appRoot = this.param.getSpaceFolder();
		if (appRoot.exists()) {
			throw new Exception("应用已存在！");
		}

		createBranch(param.getBranch(), null);

		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject createBranch(String branch, String copybranch) throws Exception {

		File branchFolder = new File(this.param.getBranchsFolder(), branch);
		if (branchFolder.exists() && branchFolder.list().length > 0) {
			throw new Exception("应用版本已存在！");
		}

		branchFolder.mkdirs();

		if (!StringUtil.isEmpty(copybranch)) {
			try {
				File src = new File(this.param.getBranchsFolder(), copybranch);
				if (src.exists()) {
					FileUtils.copyDirectory(src, branchFolder);
					SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
					List<SpaceRepositoryOptionBean> options = service.query(this.param.getSession(),
							this.param.getSpace(), null, null, null, null);

					for (SpaceRepositoryOptionBean option : options) {
						option.setBranch(copybranch);
						option.setId(null);
						service.save(this.param.getSession(), option);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JSONObject result = new JSONObject();
		return result;
	}

}

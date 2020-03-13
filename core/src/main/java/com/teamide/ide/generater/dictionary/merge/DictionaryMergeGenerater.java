package com.teamide.ide.generater.dictionary.merge;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DictionaryBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class DictionaryMergeGenerater extends BaseMergeGenerater {

	protected final List<DictionaryBean> dictionarys;

	public DictionaryMergeGenerater(String directory, List<DictionaryBean> dictionarys, RepositoryProcessorParam param,
			AppBean app, AppContext context) {
		super(directory, dictionarys, param, app, context);
		this.dictionarys = dictionarys;
	}

	public String getPackage() {
		return getDictionaryPackage();
	}

	@Override
	public void buildData() {

	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/merge/dictionary";
	}

}

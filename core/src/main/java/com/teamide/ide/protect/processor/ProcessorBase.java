package com.teamide.ide.protect.processor;

import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.protect.processor.param.ProcessorParam;
import com.teamide.ide.protect.service.SpaceEventService;

public class ProcessorBase {

	protected final ProcessorParam param;

	public ProcessorBase(ProcessorParam param) {
		this.param = param;
	}

	public void appendEvent(SpaceEventBean spaceEventBean) {
		new Thread() {

			@Override
			public void run() {
				try {
					new SpaceEventService().insert(param.getSession(), spaceEventBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

}
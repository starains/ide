package com.teamide.protect.ide.processor.param;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.protect.ide.handler.SpaceHandler;

public class SpaceProcessorParam extends ProcessorParam {

	private final String spaceid;

	private final SpaceBean space;

	private final JSONObject formatSpace;

	private final File spaceFolder;

	public SpaceProcessorParam(ClientSession session, String spaceid) {
		super(session);
		this.spaceid = spaceid;
		this.space = SpaceHandler.get(spaceid);
		this.formatSpace = SpaceHandler.getFormat(spaceid);
		String root = "";
		if (this.formatSpace != null) {
			root = this.formatSpace.getString("root");
		}

		this.spaceFolder = new File(IDEConstant.SPACE_FOLDER, root);
	}

	public String getSpaceid() {
		return spaceid;
	}

	public SpaceBean getSpace() {
		return space;
	}

	public File getSpaceFolder() {
		return spaceFolder;
	}

	public JSONObject getFormatSpace() {
		return formatSpace;
	}

}

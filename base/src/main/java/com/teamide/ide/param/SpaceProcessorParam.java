package com.teamide.ide.param;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.IDEConstant;

public class SpaceProcessorParam extends ProcessorParam {

	private final String spaceid;

	private final JSONObject formatSpace;

	private final File spaceFolder;

	public SpaceProcessorParam(RepositoryProcessorParam param) {
		this(param.getSession(), param.getSpaceid(), param.getFormatSpace());
	}

	public SpaceProcessorParam(ClientSession session, String spaceid, JSONObject formatSpace) {
		super(session);
		this.spaceid = spaceid;
		this.formatSpace = formatSpace;
		String root = "";
		if (this.formatSpace != null) {
			root = this.formatSpace.getString("root");
		}

		this.spaceFolder = new File(IDEConstant.SPACE_FOLDER, root);
	}

	public String getSpaceid() {
		return spaceid;
	}

	public String getSpaceName() {
		String name = null;
		if (this.formatSpace != null) {
			name = this.formatSpace.getString("name");
		}
		return name;
	}

	public File getSpaceFolder() {
		return spaceFolder;
	}

	public JSONObject getFormatSpace() {
		return formatSpace;
	}

}

package com.teamide.ide.param;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.util.StringUtil;

public class SpaceProcessorParam extends ProcessorParam {

	private final String spaceid;

	private final JSONObject formatSpace;

	private final File spaceFolder;

	private final File spaceRootFolder;

	public SpaceProcessorParam(RepositoryProcessorParam param) {
		this(param.getSession(), param.getSpaceRootFolder(), param.getFormatSpace());
	}

	public SpaceProcessorParam(ClientSession session, File spaceRootFolder, JSONObject formatSpace) {
		super(session);
		if (formatSpace == null) {
			throw new RuntimeException("space is null.");
		}
		this.formatSpace = formatSpace;
		this.spaceid = this.formatSpace.getString("id");
		String root = this.formatSpace.getString("root");

		if (StringUtil.isEmpty(this.spaceid)) {
			throw new RuntimeException("space spaceid is null.");
		}
		if (StringUtil.isEmpty(root)) {
			throw new RuntimeException("space root is null.");
		}
		this.spaceRootFolder = spaceRootFolder;
		this.spaceFolder = new File(spaceRootFolder, root);
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

	public File getSpaceRootFolder() {
		return spaceRootFolder;
	}

	public JSONObject getFormatSpace() {
		return formatSpace;
	}

}

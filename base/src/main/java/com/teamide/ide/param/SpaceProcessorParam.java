package com.teamide.ide.param;

import java.io.File;

import com.teamide.client.ClientSession;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.util.StringUtil;

public class SpaceProcessorParam extends ProcessorParam {

	private final SpaceFormatParam spaceFormat;

	private final File spaceFolder;

	private final File spaceRootFolder;

	protected final SpacePermission permission;

	public SpaceProcessorParam(RepositoryProcessorParam param) {
		this(param.getSession(), param.getSpaceRootFolder(), param.getSpaceFormat());
	}

	public SpaceProcessorParam(ClientSession session, File spaceRootFolder, SpaceFormatParam spaceFormat) {
		super(session);
		if (spaceFormat == null) {
			throw new RuntimeException("space is null.");
		}
		this.spaceFormat = spaceFormat;
		if (this.spaceFormat.getPermission() == null) {
			this.permission = SpacePermission.NO;
		} else {
			this.permission = this.spaceFormat.getPermission();
		}
		String root = this.spaceFormat.getRoot();

		if (StringUtil.isEmpty(root)) {
			throw new RuntimeException("space root is null.");
		}
		this.spaceRootFolder = spaceRootFolder;
		this.spaceFolder = new File(spaceRootFolder, root);
	}

	public String getSpaceid() {
		return this.spaceFormat.getId();
	}

	public String getSpaceName() {
		return this.spaceFormat.getName();
	}

	public File getSpaceFolder() {
		return spaceFolder;
	}

	public File getSpaceRootFolder() {
		return spaceRootFolder;
	}

	public SpaceFormatParam getSpaceFormat() {
		return spaceFormat;
	}

	public SpacePermission getPermission() {
		return permission;
	}

}

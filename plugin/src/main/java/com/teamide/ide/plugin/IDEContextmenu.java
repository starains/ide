package com.teamide.ide.plugin;

import java.util.List;

public class IDEContextmenu {

	public String match;

	public String text;

	public boolean divider;

	public String header;

	public String href;

	public List<IDEContextmenu> menus;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isDivider() {
		return divider;
	}

	public void setDivider(boolean divider) {
		this.divider = divider;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List<IDEContextmenu> getMenus() {
		return menus;
	}

	public void setMenus(List<IDEContextmenu> menus) {
		this.menus = menus;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

}

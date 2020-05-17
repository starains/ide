package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.PERMISSION_INFO)
public class PermissionBean extends BaseBean {

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "url", length = 200)
	private String url;

	@Column(name = "shouldlogin")
	private Boolean shouldlogin;

	@Column(name = "shouldauthorize")
	private Boolean shouldauthorize;

	@Column(name = "parentid", length = 20)
	private String parentid;

	@Column(name = "fonticon", length = 100)
	private String fonticon;

	@Column(name = "sequence", length = 10)
	private Integer sequence;

	@Column(name = "status", length = 10)
	private Integer status;

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public Boolean isShouldlogin() {

		return shouldlogin;
	}

	public void setShouldlogin(Boolean shouldlogin) {

		this.shouldlogin = shouldlogin;
	}

	public Boolean isShouldauthorize() {

		return shouldauthorize;
	}

	public void setShouldauthorize(Boolean shouldauthorize) {

		this.shouldauthorize = shouldauthorize;
	}

	public String getParentid() {

		return parentid;
	}

	public void setParentid(String parentid) {

		this.parentid = parentid;
	}

	public String getFonticon() {

		return fonticon;
	}

	public void setFonticon(String fonticon) {

		this.fonticon = fonticon;
	}

	public Integer getSequence() {

		return sequence;
	}

	public void setSequence(Integer sequence) {

		this.sequence = sequence;
	}

	public Integer getStatus() {

		return status;
	}

	public void setStatus(Integer status) {

		this.status = status;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}

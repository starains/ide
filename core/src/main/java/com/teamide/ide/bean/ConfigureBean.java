package com.teamide.ide.bean;

import javax.persistence.Table;

import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.CONFIGURE_INFO)
public class ConfigureBean extends IDEConfigure {
}

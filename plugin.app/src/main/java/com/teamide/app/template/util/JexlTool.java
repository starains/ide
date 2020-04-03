package com.teamide.app.template.util;

import org.apache.commons.jexl3.JexlException;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.teamide.jexl.JexlProcessor;
import com.teamide.util.LogUtil;

public class JexlTool {

	static Logger logger = LogUtil.get();

	static JexlProcessor JEXL_PROCESSOR = new JexlProcessor();

	public static Object invoke(String jexlScript, JSONObject data) {
		try {
			Object value = JEXL_PROCESSOR.invoke(jexlScript, data);
			return value;
		} catch (JexlException.Variable e) {
			logger.warn(e.getMessage());
		}
		return null;
	}

}

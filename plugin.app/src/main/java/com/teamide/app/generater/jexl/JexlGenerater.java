package com.teamide.app.generater.jexl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.teamide.app.AppContext;
import com.teamide.app.generater.Generater;
import com.teamide.app.plugin.AppBean;

public class JexlGenerater {

	public static Map<String, String> SCRIPT_MAP = new HashMap<String, String>();

	static {
		SCRIPT_MAP.put("$script_id", "JexlScriptID");
		SCRIPT_MAP.put("$script_date", "JexlScriptDate");
		SCRIPT_MAP.put("$script_json", "JexlScriptJSON");
		SCRIPT_MAP.put("$script_md5", "JexlScriptMD5");
		SCRIPT_MAP.put("$script_tree", "JexlScriptTree");
		SCRIPT_MAP.put("$script_encrypt", "JexlScriptEncrypt");
		SCRIPT_MAP.put("$script_base64", "JexlScriptBase64");
		SCRIPT_MAP.put("$script_aes", "JexlScriptAES");
		SCRIPT_MAP.put("$script_util", "JexlScriptUtil");
	}

	public JexlGenerater() {
	}

	public void generate(File sourceFolder, AppBean app, AppContext context) throws Exception {

		Generater generater = new JexlProcessorGenerater(sourceFolder, app, context);
		generater.generate();

		for (String propertyname : JexlGenerater.SCRIPT_MAP.keySet()) {
			String scriptName = JexlGenerater.SCRIPT_MAP.get(propertyname);
			ScriptGenerater scriptGenerater = new ScriptGenerater(sourceFolder, app, context, scriptName, propertyname);
			scriptGenerater.generate();
		}

	}

}

package com.teamide.ide.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.teamide.util.StringUtil;

public class AESTool {

	public static final String AES_KEY = "SFWIYTASDOJF";

	/**
	 * 使用AES加密算法经行加密（可逆）
	 * 
	 * @param res
	 *            需要加密的密文
	 * @return
	 */
	public static String encode(String arg) {

		return encode(arg, AES_KEY);
	}

	/**
	 * 使用AES加密算法经行加密（可逆）
	 * 
	 * @param res
	 *            需要加密的密文
	 * @param key
	 *            需要加密的密钥
	 * @return
	 */
	public static String encode(String arg, String key) {
		if (StringUtil.isEmpty(arg)) {
			return arg;
		}
		return keyGeneratorES(arg, key, true);
	}

	/**
	 * 对使用AES加密算法的密文进行解密
	 * 
	 * @param res
	 *            需要解密的密文
	 * @return
	 */
	public static String decode(String arg) {

		return decode(arg, AES_KEY);
	}

	/**
	 * 对使用AES加密算法的密文进行解密
	 * 
	 * @param res
	 *            需要解密的密文
	 * @param key
	 *            秘钥
	 * @return
	 */
	public static String decode(String arg, String key) {
		if (StringUtil.isEmpty(arg)) {
			return arg;
		}
		return keyGeneratorES(arg, key, false);
	}

	private static Map<String, SecretKeySpec> keySpecMap = new HashMap<String, SecretKeySpec>();

	private static Object lock = new Object();

	public static SecretKeySpec getSecretKeySpec(String key) {
		SecretKeySpec keySpec = keySpecMap.get(key);
		if (keySpec == null) {
			synchronized (lock) {
				keySpec = keySpecMap.get(key);
				if (keySpec == null) {
					try {
						KeyGenerator kg = KeyGenerator.getInstance("AES");

						byte[] keyBytes = key.getBytes();
						// 防止linux下 随机生成key
						SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
						secureRandom.setSeed(keyBytes);
						kg.init(128, secureRandom);
						SecretKey sk = kg.generateKey();
						keySpec = new SecretKeySpec(sk.getEncoded(), "AES");
						keySpecMap.put(key, keySpec);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return keySpec;

	}

	/**
	 * 使用KeyGenerator双向加密，DES/AES，注意这里转化为字符串的时候是将2进制转为16进制格式的字符串，不是直接转，因为会出错
	 * 
	 * @param res
	 *            加密的原文
	 * @param key
	 *            密钥
	 * @param isEncode
	 * @return
	 */
	public static String keyGeneratorES(String res, String key, boolean isEncode) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			if (isEncode) {
				cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(key));
				byte[] resBytes = res.getBytes();
				return new String(Base64.getEncoder().encode(cipher.doFinal(resBytes)));
			} else {
				cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(key));
				byte[] resBytes = res.getBytes();
				return new String(cipher.doFinal(Base64.getDecoder().decode(resBytes)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

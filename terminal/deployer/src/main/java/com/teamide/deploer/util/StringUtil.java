package com.teamide.deploer.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {

	public static final int INDEX_NOT_FOUND = -1;

	public static final String SPACE = " ";
	public static final String TAB = "	";
	public static final String DOT = ".";
	public static final String DOUBLE_DOT = "..";
	public static final String SLASH = "/";
	public static final String BACKSLASH = "\\";
	public static final String EMPTY = "";
	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = "\r\n";
	public static final String UNDERLINE = "_";
	public static final String COMMA = ",";
	public static final String DELIM_START = "{";
	public static final String DELIM_END = "}";
	public static final String BRACKET_START = "[";
	public static final String BRACKET_END = "]";
	public static final String COLON = ":";

	public static final String HTML_NBSP = "&nbsp;";
	public static final String HTML_AMP = "&amp;";
	public static final String HTML_QUOTE = "&quot;";
	public static final String HTML_APOS = "&apos;";
	public static final String HTML_LT = "&lt;";
	public static final String HTML_GT = "&gt;";

	public static final String EMPTY_JSON = "{}";

	public static boolean isEmpty(CharSequence str) {

		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(CharSequence str) {

		return false == isEmpty(str);
	}

	/**
	 * 如果对象是字符串是否为空白，空白的定义如下： <br>
	 * 1、为null <br>
	 * 2、为不可见字符（如空格）<br>
	 * 3、""<br>
	 * 
	 * @param obj
	 *            对象
	 * @return 如果为字符串是否为空串
	 * @since 3.3.0
	 */
	public static boolean isEmptyIfStr(Object obj) {

		if (null == obj) {
			return true;
		} else if (obj instanceof CharSequence) {
			return isEmpty((CharSequence) obj);
		}
		return false;
	}

	public static String nullToEmpty(String str) {

		if (isEmpty(str)) {
			return EMPTY;
		}
		return str;
	}

	/**
	 * 清理空白字符
	 * 
	 * @param str
	 *            被清理的字符串
	 * @return 清理后的字符串
	 */
	public static String cleanBlank(CharSequence str) {

		if (str == null) {
			return null;
		}

		int len = str.length();
		final StringBuilder sb = new StringBuilder(len);
		char c;
		for (int i = 0; i < len; i++) {
			c = str.charAt(i);
			if (false == isBlankChar(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 是否空白符<br>
	 * 空白符包括空格、制表符、全角空格和不间断空格<br>
	 * 
	 * @see Character#isWhitespace(int)
	 * @see Character#isSpaceChar(int)
	 * @param c
	 *            字符
	 * @return 是否空白符
	 * @since 3.0.6
	 */
	public static boolean isBlankChar(char c) {

		return isBlankChar((int) c);
	}

	/**
	 * 是否空白符<br>
	 * 空白符包括空格、制表符、全角空格和不间断空格<br>
	 * 
	 * @see Character#isWhitespace(int)
	 * @see Character#isSpaceChar(int)
	 * @param c
	 *            字符
	 * @return 是否空白符
	 * @since 3.0.6
	 */
	public static boolean isBlankChar(int c) {

		return Character.isWhitespace(c) || Character.isSpaceChar(c);
	}

	/**
	 * 编码字符串<br>
	 * 使用系统默认编码
	 * 
	 * @param str
	 *            字符串
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(CharSequence str) {

		return bytes(str, Charset.defaultCharset());
	}

	/**
	 * 编码字符串
	 * 
	 * @param str
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(CharSequence str, String charset) {

		return bytes(str, isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 编码字符串
	 * 
	 * @param str
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(CharSequence str, Charset charset) {

		if (str == null) {
			return null;
		}

		if (null == charset) {
			return str.toString().getBytes();
		}
		return str.toString().getBytes(charset);
	}

	/**
	 * 将byte数组转为字符串
	 * 
	 * @param bytes
	 *            byte数组
	 * @param charset
	 *            字符集
	 * @return 字符串
	 */
	public static String str(byte[] bytes, String charset) {

		return str(bytes, isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 解码字节码
	 * 
	 * @param data
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String str(byte[] data, Charset charset) {

		if (data == null) {
			return null;
		}

		if (null == charset) {
			return new String(data);
		}
		return new String(data, charset);
	}

	/**
	 * 将Byte数组转为字符串
	 * 
	 * @param bytes
	 *            byte数组
	 * @param charset
	 *            字符集
	 * @return 字符串
	 */
	public static String str(Byte[] bytes, String charset) {

		return str(bytes, isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 解码字节码
	 * 
	 * @param data
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String str(Byte[] data, Charset charset) {

		if (data == null) {
			return null;
		}

		byte[] bytes = new byte[data.length];
		Byte dataByte;
		for (int i = 0; i < data.length; i++) {
			dataByte = data[i];
			bytes[i] = (null == dataByte) ? -1 : dataByte.byteValue();
		}

		return str(bytes, charset);
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 * 
	 * @param data
	 *            数据
	 * @param charset
	 *            字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String str(ByteBuffer data, String charset) {

		if (data == null) {
			return null;
		}

		return str(data, Charset.forName(charset));
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 * 
	 * @param data
	 *            数据
	 * @param charset
	 *            字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String str(ByteBuffer data, Charset charset) {

		if (null == charset) {
			charset = Charset.defaultCharset();
		}
		return charset.decode(data).toString();
	}

	/**
	 * {@link CharSequence} 转为字符串，null安全
	 * 
	 * @param cs
	 *            {@link CharSequence}
	 * @return 字符串
	 */
	public static String str(CharSequence cs) {

		return null == cs ? null : cs.toString();
	}

	/**
	 * 给定字符串是否被字符包围
	 * 
	 * @param str
	 *            字符串
	 * @param prefix
	 *            前缀
	 * @param suffix
	 *            后缀
	 * @return 是否包围，空串不包围
	 */
	public static boolean isSurround(CharSequence str, CharSequence prefix, CharSequence suffix) {

		if (StringUtil.isEmpty(str)) {
			return false;
		}
		if (str.length() < (prefix.length() + suffix.length())) {
			return false;
		}

		final String str2 = str.toString();
		return str2.startsWith(prefix.toString()) && str2.endsWith(suffix.toString());
	}

	/**
	 * 给定字符串是否被字符包围
	 * 
	 * @param str
	 *            字符串
	 * @param prefix
	 *            前缀
	 * @param suffix
	 *            后缀
	 * @return 是否包围，空串不包围
	 */
	public static boolean isSurround(CharSequence str, char prefix, char suffix) {

		if (StringUtil.isEmpty(str)) {
			return false;
		}
		if (str.length() < 2) {
			return false;
		}

		return str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix;
	}

	/**
	 * 是否包含特定字符，忽略大小写，如果给定两个参数都为<code>null</code>，返回true
	 * 
	 * @param str
	 *            被检测字符串
	 * @param testStr
	 *            被测试是否包含的字符串
	 * @return 是否包含
	 */
	public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {

		if (null == str) {
			// 如果被监测字符串和
			return null == testStr;
		}
		return str.toString().toLowerCase().contains(testStr.toString().toLowerCase());
	}

	public static boolean hasText(CharSequence str) {

		return (str != null && str.length() > 0 && containsText(str));
	}

	public static boolean hasText(String str) {

		return (str != null && !str.isEmpty() && containsText(str));
	}

	private static boolean containsText(CharSequence str) {

		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {

		if (str == null) {
			return new String[0];
		}

		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<String> collection) {

		return collection.toArray(new String[0]);
	}

	public static String trim(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

}

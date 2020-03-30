package com.teamide.db.dialect;

import java.util.Arrays;
import java.util.Collection;

import com.teamide.util.StringUtil;

/**
 * 包装器<br>
 * 主要用于字段名的包装（在字段名的前后加字符，例如反引号来避免与数据库的关键字冲突）
 * 
 *
 */
public class Wrapper {

	/** 前置包装符号 */
	private Character preWrapQuote;
	/** 后置包装符号 */
	private Character sufWrapQuote;

	public Wrapper() {

	}

	/**
	 * 构造
	 * 
	 * @param wrapQuote
	 *            单包装字符
	 */
	public Wrapper(Character wrapQuote) {

		this.preWrapQuote = wrapQuote;
		this.sufWrapQuote = wrapQuote;
	}

	/**
	 * 包装符号
	 * 
	 * @param preWrapQuote
	 *            前置包装符号
	 * @param sufWrapQuote
	 *            后置包装符号
	 */
	public Wrapper(Character preWrapQuote, Character sufWrapQuote) {

		this.preWrapQuote = preWrapQuote;
		this.sufWrapQuote = sufWrapQuote;
	}

	// --------------------------------------------------------------- Getters
	// and Setters start
	/**
	 * @return 前置包装符号
	 */
	public char getPreWrapQuote() {

		return preWrapQuote;
	}

	/**
	 * 设置前置包装的符号
	 * 
	 * @param preWrapQuote
	 *            前置包装符号
	 */
	public void setPreWrapQuote(Character preWrapQuote) {

		this.preWrapQuote = preWrapQuote;
	}

	/**
	 * @return 后置包装符号
	 */
	public char getSufWrapQuote() {

		return sufWrapQuote;
	}

	/**
	 * 设置后置包装的符号
	 * 
	 * @param sufWrapQuote
	 *            后置包装符号
	 */
	public void setSufWrapQuote(Character sufWrapQuote) {

		this.sufWrapQuote = sufWrapQuote;
	}

	// --------------------------------------------------------------- Getters
	// and Setters end

	/**
	 * 包装字段名<br>
	 * 有时字段与SQL的某些关键字冲突，导致SQL出错，因此需要将字段名用单引号或者反引号包装起来，避免冲突
	 * 
	 * @param field
	 *            字段名
	 * @return 包装后的字段名
	 */
	public String wrap(String field) {

		if (preWrapQuote == null || sufWrapQuote == null || StringUtil.isEmpty(field)) {
			return field;
		}

		// 如果已经包含包装的引号，返回原字符
		if (StringUtil.isSurround(field, preWrapQuote, sufWrapQuote)) {
			return field;
		}

		// 如果字段中包含通配符或者括号（字段通配符或者函数），不做包装
		if (StringUtil.containsAnyIgnoreCase(field, "*", "(", " ", "as")) {
			return field;
		}

		return (preWrapQuote + field + sufWrapQuote);
	}

	/**
	 * 包装字段名<br>
	 * 有时字段与SQL的某些关键字冲突，导致SQL出错，因此需要将字段名用单引号或者反引号包装起来，避免冲突
	 * 
	 * @param fields
	 *            字段名
	 * @return 包装后的字段名
	 */
	public String[] wrap(String... fields) {

		if (fields == null) {
			return fields;
		}

		String[] wrappedFields = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			wrappedFields[i] = wrap(fields[i]);
		}

		return wrappedFields;
	}

	/**
	 * 包装字段名<br>
	 * 有时字段与SQL的某些关键字冲突，导致SQL出错，因此需要将字段名用单引号或者反引号包装起来，避免冲突
	 * 
	 * @param fields
	 *            字段名
	 * @return 包装后的字段名
	 */
	public Collection<String> wrap(Collection<String> fields) {

		if (fields == null) {
			return fields;
		}

		return Arrays.asList(wrap(fields.toArray(new String[fields.size()])));
	}

}

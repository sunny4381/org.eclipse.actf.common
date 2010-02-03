/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.as;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for Deserializing a string in JSON notation to a Java object. Note that
 * a property name should be quoted in JSON object notation. E.g.
 * <code>{"id":123}</code>. In this deserializer, a JSON object is
 * deserialized to an {@link ASObject} instance.
 * 
 * @see ASObject
 */
public class ASDeserializer {
	/**
	 * Token dictionary used to convert reserved words to Java constants.
	 */
	private static final Map<String, Object> tokenDic = new HashMap<String, Object>();
	/**
	 * String constant to denote JSON <code>undefined</code> value.
	 */
	private static final Object UNDEFINED = "undefined".intern(); //$NON-NLS-1$
	/**
	 * String constant to denote JSON <code>null</code> value.
	 */
	private static final Object NULL = "null".intern(); //$NON-NLS-1$

	/**
	 * Initializes the token dictionary.
	 */
	static {
		tokenDic.put("true", Boolean.valueOf(true)); //$NON-NLS-1$
		tokenDic.put("false", Boolean.valueOf(false)); //$NON-NLS-1$
		tokenDic.put("undefined", UNDEFINED); //$NON-NLS-1$
		tokenDic.put("null", NULL); //$NON-NLS-1$
	}

	/**
	 * Internal variable to hold cursor position in parsing. <code>0</code>
	 * means the first character of the string.
	 */
	private int idx;
	/**
	 * Internal variable to hold a string to be serialized. Set by the
	 * constructor.
	 */
	private String str;

	/**
	 * Skips a sequence of whitespace characters.
	 * 
	 * @return Cursor position after skipping. It returns <code>-1</code> if
	 *         it reaches the end of the string.
	 */
	private int skipSP() {
		if(str==null){
			//TODO
			//System.out.println("AS Str is null");
			return -1;
		}
		while (idx < str.length()) {
			char ch = str.charAt(idx);
			switch (ch) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				idx++;
				break;
			default:
				return idx;
			}
		}
		return -1;

	}

	/**
	 * Seatch the end of a token starts from the current cursor position.
	 * 
	 * @return Cursor position after search. It points the next character to the
	 *         end of given token.
	 */
	private int getTokenEndIdx() {
		int idx2 = idx;
		while (idx2 < str.length()) {
			char ch = str.charAt(idx2);
			switch (ch) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
			case ',':
			case ':':
			case '}':
			case ']':
				return idx2;

			default:
				idx2++;
			}
		}
		return idx2;
	}

	/**
	 * Deserializes a JSON string to Java string. Note that JSON string should
	 * be quoted as <code>"Hello, Java"</code>. This method is internally
	 * called from {@link #deserialize()}.
	 * 
	 * @return Deserialized Java string.
	 * @throws IllegalArgumentException
	 *             when given JSON string is ill-formed.
	 */
	private String deserializeString() {
		StringBuffer ret = new StringBuffer();
		idx++;
		while (idx < str.length()) {
			char ch = str.charAt(idx);
			switch (ch) {
			case '"':
				idx++;
				return ret.toString();
			case '\\':
				idx++;
				if (idx == str.length()) {
					throw new IllegalArgumentException(
							"Abnormal end of the string:" + str); //$NON-NLS-1$
				}
				ret.append(str.charAt(idx));
				idx++;
				break;
			default:
				ret.append(ch);
				idx++;
			}
		}
		throw new IllegalArgumentException("Invalid String:" + str); //$NON-NLS-1$
	}

	/**
	 * Deserializes a JSON array string to Java array. This method is internally
	 * called from {@link #deserialize()}.
	 * 
	 * @return Deserialized Java array.
	 * @throws IllegalArgumentException
	 *             when given JSON string is ill-formed.
	 */
	private Object[] deserializeArray() {
		idx++;
		ArrayList<Object> ret = new ArrayList<Object>();
		while (idx < str.length()) {
			skipSP();
			char ch = str.charAt(idx);
			if (ch == ']') {
				idx++;
				return ret.toArray(new Object[ret.size()]);
			} else {
				ret.add(deserialize());
			}
			skipSP();
			ch = str.charAt(idx);
			if (ch == ']') {
				idx++;
				return ret.toArray(new Object[ret.size()]);
			}
			if (ch != ',') {
				throw new IllegalArgumentException("Missing ',':" + str); //$NON-NLS-1$
			}
			idx++;
		}
		throw new IllegalArgumentException("Abnormal end of the array:" + str); //$NON-NLS-1$
	}

	/**
	 * Deserializes a JSON object string to {@link ASObject} instance. This
	 * method is internally called from {@link #deserialize()}.
	 * 
	 * @return Deserialized {@link ASObject} instance.
	 * @throws IllegalArgumentException
	 *             when given JSON string is ill-formed.
	 */
	private ASObject deserializeASObject() {
		idx++;
		ASObject ret = new ASObject();
		while (idx < str.length()) {
			skipSP();
			char ch = str.charAt(idx);
			if (ch == '}') {
				idx++;
				return ret;
			} else {
				String prop = deserializeString();
				skipSP();
				if (str.charAt(idx) != ':') {
					throw new IllegalArgumentException("Missing ':':" + str); //$NON-NLS-1$
				}
				idx++;
				Object o = deserialize();
				ret.put(prop, o);
			}
			skipSP();
			ch = str.charAt(idx);
			if (ch == '}') {
				idx++;
				return ret;
			}
			if (str.charAt(idx) != ',') {
				throw new IllegalArgumentException("Missing ',':" + str); //$NON-NLS-1$
			}
			idx++;
		}
		throw new IllegalArgumentException("Abnormal end of the array:" + str); //$NON-NLS-1$
	}

	/**
	 * Deserializes a given JSON string. Especially a JSON object string is
	 * deserialized to an {@link ASObject} instance.
	 * 
	 * Note that a property name should be quoted in JSON object notation. E.g.
	 * <code>{"id":123}</code>.
	 * 
	 * @return Deserialized object.
	 * @throws IllegalArgumentException
	 *             when given JSON string is not in valid format.
	 */
	public Object deserialize() {
		int r = skipSP();
		if (r < 0)
			return null;
		char ch = str.charAt(idx);
		switch (ch) {
		case '[':
			return deserializeArray();
		case '{':
			return deserializeASObject();
		case '"':
			return deserializeString();
		default:
			int idx2 = getTokenEndIdx();
			String tok = str.substring(idx, idx2);
			Object o = tokenDic.get(tok);
			this.idx = idx2;
			if (o == NULL) {
				return null;
			}
			if (o != null) {
				return o;
			}
			try {
				int i = Integer.parseInt(tok);
				return Integer.valueOf(i);
			} catch (NumberFormatException e) {
			}
			try {
				double d = Double.parseDouble(tok);
				return new Double(d);
			} catch (NumberFormatException e) {
			}
			throw new IllegalArgumentException(tok + " is not a valid token."); //$NON-NLS-1$
		}

	}

	/**
	 * Creates an {@link ASDeserializer} instance.
	 * 
	 * @param str
	 *            JSON string to be deserialized
	 */
	public ASDeserializer(String str) {
		this.idx = 0;
		this.str = str;
	}
}

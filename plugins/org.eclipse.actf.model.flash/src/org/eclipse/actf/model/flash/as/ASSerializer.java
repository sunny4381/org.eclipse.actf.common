/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initinal API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.as;

/**
 * Utility class for serializing Java objects to ActionScript style string.
 * 
 * @see ASDeserializer
 */
public class ASSerializer {
	/**
	 * Escape a given {@link String}. Especially it escapes double quotation
	 * and back slash characters and convert to ActionScript style String
	 * literal by prepending and appending double quotations .
	 * 
	 * @param str
	 *            A {@link String} to be escaped
	 * @return Escaped {@link String} literal expression
	 */
	public static String serialize(String str) {
		if ((str.indexOf('"') >= 0) || (str.indexOf('\\') >= 0)) {
			StringBuffer ret = new StringBuffer('"');
			for (int i = 0; i < str.length(); i++) {
				char ch = str.charAt(i);
				switch (ch) {
				case '"':
					ret.append("\\\""); //$NON-NLS-1$
					break;
				case '\\':
					ret.append("\\\\"); //$NON-NLS-1$
					break;
				default:
					ret.append(ch);
				}
			}
			ret.append('"');
			return ret.toString();
		} else {
			return "\"" + str + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Serializes given Java {@link Object} to a {@link String}. It serializes
	 * objects of type {@link String}, {@link Number}, and {@link Boolean}.
	 * 
	 * @param a
	 *            Java {@link Object} to be serialized
	 * @return Serialized {@link String}
	 */
	private static String serialize(Object a) {
		if (a instanceof String) {
			return serialize((String) a);
		} else if (a instanceof Number) {
			return a.toString();
		} else if (a instanceof Boolean) {
			return a.toString();
		} else if (a instanceof Object[]) {
			StringBuffer ret = new StringBuffer();
			for (Object tmp : (Object[]) a) {
				ret.append(serialize(tmp));
				ret.append(","); //$NON-NLS-1$
			}
			if (ret.length() > 1) {
				return ret.substring(0, ret.length() - 2);
			}
			return ""; //$NON-NLS-1$
		}
		throw new IllegalArgumentException(a + " cannot be serialized."); //$NON-NLS-1$
	}

	/**
	 * Serializes a given secret and a given array of objects to the secret
	 * followed by a ActionScript style {@link String}. The secret is used to
	 * secure communication between a program use this class and a browser
	 * component.
	 * 
	 * @param secret
	 *            Secret {@link String}
	 * @param args
	 *            Array of {@link Object}s
	 * 
	 * @return Serialized {@link String}
	 */
	public static String serialize(String secret, Object[] args) {
		StringBuffer ret = new StringBuffer(secret);
		ret.append('[');
		if (args.length > 0) {
			ret.append(serialize(args[0]));
			for (int i = 1; i < args.length; i++) {
				ret.append(","); //$NON-NLS-1$
				Object a = args[i];
				ret.append(serialize(a));
			}
		}
		ret.append("]"); //$NON-NLS-1$
		return ret.toString();
	}

}

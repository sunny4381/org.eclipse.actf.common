/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.flash.proxy;

import java.io.IOException;
import java.io.PushbackInputStream;

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;

public class SWFUtil {
	static private final byte[] CT_SHOCKWAVE_FLASH = "application/x-shockwave-flash"
			.getBytes();
	static private final byte[] CT_IMAGE = "image".getBytes();
	static private final byte[] CT_VIDEO = "video".getBytes();

	static public final byte[] X_FLASH_VERSION_A = "x-flash-version".getBytes();
	static public final byte[] MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH_A = "application/x-shockwave-flash"
			.getBytes();
	static public final byte[] MIME_TYPE_APPLICATION_X_WWW_FORM_URLENCODED_A = "application/x-www-form-urlencoded"
			.getBytes();

	static public final String MSIE = "MSIE";
	public static final String BRIDGE_INIT_SWF_FILENAME = "Eclipse-ACTF-SWF-Initialize-Bridge-Package-1082017309130187472.swf";
	public static final String BRIDGE_INIT_SWF_V9_FILENAME = "bridge_as3.swf";
	public static final String LOADVARS_PROPERTY_FILENAME = "Eclipse-ACTF-SWF-LoadVars-1082017309130187472.txt";

	static public final int FLASH_MAGIC_NUMBER_SIZE = 4;

	public static boolean isContentTypeEqual(byte[] ct, byte[] type) {
		if (ct.length < type.length)
			return false;
		int i;
		for (i = 0; i < type.length; i++) {
			if (ct[i] != type[i])
				return false;
		}
		if ((ct[i] == '/') || (ct.length == type.length))
			return true;
		return false;
	}

	public static boolean isPossiblySWFContentType(IHTTPResponseMessage response) {
		IHTTPHeader contentTypeH = response
				.getHeader(IHTTPHeader.CONTENT_TYPE_A);
		if (contentTypeH != null) {
			byte[] ct = contentTypeH.getValue();
			if (isContentTypeEqual(ct, CT_IMAGE))
				return false;
			if (isContentTypeEqual(ct, CT_VIDEO))
				return false;
		}
		return true;
	}

	public static boolean isSWFContentType(IHTTPResponseMessage response) {
		IHTTPHeader contentTypeH = response
				.getHeader(IHTTPHeader.CONTENT_TYPE_A);
		if (contentTypeH != null) {
			return contentTypeH.compareValueIgnoreCase(CT_SHOCKWAVE_FLASH);
		}
		return false;
	}

	static public int isSWF(PushbackInputStream pbis) {
		try {
			byte[] magicNumber = new byte[FLASH_MAGIC_NUMBER_SIZE];
			int returnValue = -1;
			int size = pbis.read(magicNumber);
			if (size < FLASH_MAGIC_NUMBER_SIZE) {
				if (size > 0) {
					pbis.unread(magicNumber, 0, size);
				}
				return -1;
			}
			if (((magicNumber[0] == 'C') || (magicNumber[0] == 'F'))
					&& (magicNumber[1] == 'W') && (magicNumber[2] == 'S')) {
				returnValue = magicNumber[3];
			}
			pbis.unread(magicNumber);
			return returnValue;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
}

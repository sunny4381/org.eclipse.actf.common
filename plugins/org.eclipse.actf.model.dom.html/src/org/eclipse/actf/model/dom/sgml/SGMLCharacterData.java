/*******************************************************************************
 * Copyright (c) 1998, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Goh KONDOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.sgml;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

abstract class SGMLCharacterData extends SGMLNode implements CharacterData {
	protected SGMLCharacterData(String str, Document doc) {
		super(doc);
		text = str;
	}

	protected String text;

	public String getNodeValue() {
		return text;
	}

	public void setNodeValue(String nodeValue) {
		text = nodeValue;
	}

	public String getData() throws DOMException {
		return text;
	}

	public void setData(String data) throws DOMException {
		text = data;
	}

	public int getLength() {
		return text.length();
	}

	public String substringData(int offset, int count) throws DOMException {
		try {
			return text.substring(offset, count);
		} catch (StringIndexOutOfBoundsException e) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, e.getMessage()) {
			};
		}
	}

	public void appendData(String arg) throws DOMException {
		text = text + arg;
	}

	public void insertData(int offset, String arg) throws DOMException {
		try {
			text = text.substring(0, offset) + arg + text.substring(offset);
		} catch (StringIndexOutOfBoundsException e) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, e.getMessage()) {
			};
		}
	}

	public void deleteData(int offset, int count) throws DOMException {
		try {
			text = text.substring(0, offset) + text.substring(offset + count);
		} catch (StringIndexOutOfBoundsException e) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, e.getMessage()) {
			};
		}
	}

	public void replaceData(int offset, int count, String arg)
			throws DOMException {
		try {
			if (text.length() < offset + count) {
				text = text.substring(0, offset) + arg.substring(0, count);
			} else {
				text = text.substring(0, offset) + arg.substring(0, count)
						+ text.substring(offset + count);
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, e.getMessage()) {
			};
		}
	}
}

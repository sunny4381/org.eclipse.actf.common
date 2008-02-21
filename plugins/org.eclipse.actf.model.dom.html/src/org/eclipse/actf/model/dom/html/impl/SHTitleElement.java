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

package org.eclipse.actf.model.dom.html.impl;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTitleElement;

public class SHTitleElement extends SHElement implements HTMLTitleElement {
	public String getText() {
		String tmpS = "";
		if (hasChildNodes()) {
			Node firstChild = getFirstChild();
			if (firstChild.getNodeType() == Node.TEXT_NODE) {
				tmpS = getFirstChild().getNodeValue();
			}
		}
		return tmpS;
	}

	public void setText(String text) {
		boolean flag = false;
		if (hasChildNodes()) {
			Node firstChild = getFirstChild();
			if (firstChild.getNodeType() == Node.TEXT_NODE) {
				firstChild.setNodeValue(text);
			} else {
				flag = true;
			}
		} else {
			flag = true;
		}
		if (flag) {
			insertBefore(getOwnerDocument().createTextNode("text"),
					getFirstChild());
		}
	}

	protected SHTitleElement(String tagName, SHDocument doc) {
		super(tagName, doc);
	}
}

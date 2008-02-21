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

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLMapElement;

public class SHMapElement extends SHElement implements HTMLMapElement {
	protected SHMapElement(String tagName, SHDocument doc) {
		super(tagName, doc);
	}

	public HTMLCollection getAreas() {
		return ((SHDocument) getOwnerDocument())
				.createCollection(getElementsByTagName("AREA"));
	}

	public String getName() {
		return getAttribute("name");
	}

	public void setName(String name) {
		setAttribute("name", name);
	}
}

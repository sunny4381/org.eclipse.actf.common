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

package org.eclipse.actf.model.internal.dom.html.impl;

import org.w3c.dom.html.HTMLDivElement;

@SuppressWarnings("nls")
public class SHDivElement extends SHElement implements HTMLDivElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2263402888738941477L;

	protected SHDivElement(String tagName, SHDocument doc) {
		super(tagName, doc);
	}

	public String getAlign() {
		return getAttribute("align");
	}

	public void setAlign(String align) {
		setAttribute("align", align);
	}
}

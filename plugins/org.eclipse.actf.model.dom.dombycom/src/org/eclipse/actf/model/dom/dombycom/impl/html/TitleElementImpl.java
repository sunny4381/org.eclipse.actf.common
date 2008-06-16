/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.dombycom.impl.html;

import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.html.HTMLTitleElement;

public class TitleElementImpl extends HTMLElementImpl implements
		HTMLTitleElement {

	private static final String TEXT = "text";

	protected TitleElementImpl(NodeImpl baseNode, IDispatch inode) {
		super(baseNode, inode);
	}

	protected TitleElementImpl(DocumentImpl baseDoc, IDispatch inode) {
		super(baseDoc, inode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.dom.dombycom.impl.NodeImpl#getText()
	 */
	@Override
	public String getText() {
		String ret = "";
		ret = (String) Helper.get(inode, TEXT);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.dom.dombycom.impl.NodeImpl#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		Helper.put(inode, TEXT, text);
	}

}

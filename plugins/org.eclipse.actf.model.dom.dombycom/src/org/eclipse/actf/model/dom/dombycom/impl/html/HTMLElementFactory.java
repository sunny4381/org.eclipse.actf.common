/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.html;

import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.Element;

@SuppressWarnings("nls")
public class HTMLElementFactory {

	private static final String IFRAME = "iframe";
	private static final String TITLE = "title";
	private static final String IMG = "img";
	private static final String SELECT = "select";
	private static final String TEXT = "text";
	private static final String FRAMENODE = "framenode";

	public static NodeImpl create(NodeImpl base, IDispatch inode, String tagName) {
		String tagNameLower = tagName.toLowerCase();
		try {
			if (tagNameLower.equals(FRAMENODE)) {
				return new FrameNodeImpl(base, inode);
			} else if (tagNameLower.equals(TEXT)) {
				return new TextImpl(base, inode);
			} else if (tagNameLower.equals(SELECT)) {
				return new SelectElementImpl(base, inode);
			} else if (tagNameLower.equals(IMG)) {
				return new ImageElementImpl(base, inode);
			} else if (tagNameLower.equals(TITLE)) {
				return new TitleElementImpl(base, inode);
			}
			return new HTMLElementImpl(base, inode);
		} catch (DispatchException e) {
		}
		return null;
	}

	public static Element createElement(DocumentImpl base, IDispatch inode,
			String tagName) {
		try {
			IDispatch r;
			r = (IDispatch) inode.invoke1("createElement", tagName);
			if (r == null)
				return null;

			if (tagName.toLowerCase().equals(IFRAME)) {
				return new FrameNodeImpl(base, r);
			}
			return new HTMLElementImpl(base, r);
		} catch (DispatchException e) {
		}
		return null;
	}
}

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

package org.eclipse.actf.model.dom.dombycom;

import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.StyleSheetImpl;
import org.eclipse.actf.util.win32.comclutch.ComPlugin;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * DomByCom is a utility class to obtain the COM wrapper object form the pointer
 * of the web browser object.
 */
public class DomByCom {
	private ResourceManager resourceManager;

	private final IDispatch iWebBrowser2;

	/**
	 * This id is used for div elements created by aiBrowser to show border line
	 * of the focused object.
	 */
	public static final String ID_DIV = "__org_eclipse_actf_highlight_div__"; //$NON-NLS-1$

	public static final int DIV_BORDER_WIDTH = 3;

	public static final int STYLE_BORDER = 0;

	public static final int DIV_BORDER = 1;

	public static final int STYLE_BORDER2 = 2;

	public static final int BORDER_MODE = STYLE_BORDER2;

	public static final String BORDER_STYLE_STRING = "border: 4px inset yellow;"; //$NON-NLS-1$

	private void addStyles(DocumentImpl doc) {
		StyleSheetImpl style = doc.createStyleSheet();
		if (style != null) {
			style.addRule(".CSStoHighlight", BORDER_STYLE_STRING); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("nls")
	private void addDivs(DocumentImpl doc) {
		Element el = doc.getElementById(ID_DIV);
		if (el == null) {
			el = doc.createElement("div");
			el.setAttribute("id", ID_DIV);

			NodeList body = doc.getElementsByTagName("body");
			if (body != null && body.getLength() > 0) {
				System.out.println("insert");
				body.item(0).appendChild(el);

				try {
					IElementEx elx = (IElementEx) el;
					IStyle st = elx.getStyle();

					st.put("position", "absolute");
					st.put("backgroundColor", "transparent");
					st.put("border", DIV_BORDER_WIDTH + "px solid green");
					st.put("zIndex", "10000");
					st.put("left", "-1000px");
					st.put("top", "-1000px");
					st.put("width", "100px");
					st.put("height", "100px");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initialize(DocumentImpl doc) {
		switch (BORDER_MODE) {
		case STYLE_BORDER:
			addStyles(doc);
			break;
		case DIV_BORDER:
			addDivs(doc);
		case STYLE_BORDER2:
		default:
			// do nothing;
		}
	}

	/**
	 * Release all objects managed by the DomByCom. DomByCom has a resource
	 * manager which manages the life cycle of the objects obtained from the
	 * document.
	 */
	public void release() {
		resourceManager.releaseAll(iWebBrowser2);
	}

	/**
	 * @return the document object of the web browser.
	 */
	public IDocumentEx getDocument() {
		IDispatch iDocument = (IDispatch) iWebBrowser2.get("Document"); //$NON-NLS-1$
		DocumentImpl doc = new DocumentImpl(iDocument);
		initialize(doc);
		return doc;
	}

	/**
	 * DomByCom is a utility class to obtain the COM wrapper object form the
	 * pointer of the web browser object.
	 * 
	 * @param iWebBrowser2
	 *            the native pointer of the IWebBrowser2 object.
	 */
	public DomByCom(long iWebBrowser2) {
		this.resourceManager = ComPlugin.getDefault().newResourceManager(null);
		this.iWebBrowser2 = ComService.newIDispatch(resourceManager,
				iWebBrowser2, true);
	}
}

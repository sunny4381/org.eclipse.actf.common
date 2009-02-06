/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.util.dom.TreeWalkerImpl;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

public class WebBrowserStyleInfoImpl implements IWebBrowserStyleInfo {
	private static final String TAG_HEAD = "HEAD";
	private final WebBrowserIEImpl browser;

	public WebBrowserStyleInfoImpl(WebBrowserIEImpl browser) {
		this.browser = browser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getCurrentStyles()
	 */
	public HashMap<String, ICurrentStyles> getCurrentStyles() {
		HashMap<String, ICurrentStyles> currentStyles = new HashMap<String, ICurrentStyles>(
				512);
		Document doc = browser.getLiveDocument();
		TreeWalkerImpl treeWalker = new TreeWalkerImpl(doc,
				NodeFilter.SHOW_ELEMENT, new NodeFilter() {

					public short acceptNode(Node arg0) {
						if (TAG_HEAD.equalsIgnoreCase(arg0.getNodeName())) {
							return FILTER_REJECT;
						}
						return FILTER_ACCEPT;
					}
				}, false);
		Node tmpN = treeWalker.nextNode();
		URL base = null;
		try {
			base = new URL(browser.getURL());
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		}
		while (tmpN != null) {
			if (tmpN instanceof IElementEx) {
				ICurrentStyles curStyle = new CurrentStylesImpl(
						(IElementEx) tmpN, base);
				currentStyles.put(curStyle.getXPath(), curStyle);
			}
			tmpN = treeWalker.nextNode();
		}

		return currentStyles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getUnvisitedLinkColor()
	 */
	public RGB getUnvisitedLinkColor() {
		return browser.getAnchorColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getVisitedLinkColor()
	 */
	public RGB getVisitedLinkColor() {
		return browser.getVisitedAnchorColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getSizeInfo()
	 */
	public ModelServiceSizeInfo getSizeInfo(boolean isWhole) {
		return browser.getBrowserSize(isWhole);
	}
}

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

package org.eclipse.actf.model.ui.editors.ie.impl;

import java.util.HashMap;

import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.swt.graphics.RGB;

public class WebBrowserStyleInfoImpl implements IWebBrowserStyleInfo {
	private HashMap<String, ICurrentStyles> nodeStyles;

	private ModelServiceSizeInfo size;
	
	private RGB unvisited, visited;

	/**
	 * @param nodeStyles
	 * @param rgb2
	 * @param rgb
	 * @param pageSize
	 */
	public WebBrowserStyleInfoImpl(ModelServiceSizeInfo size,
			HashMap<String, ICurrentStyles> nodeStyles, RGB unvisited,
			RGB visited) {
		this.size = size;
		this.nodeStyles = nodeStyles;
		this.unvisited = unvisited;
		this.visited = visited;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getCurrentStyles()
	 */
	public HashMap<String, ICurrentStyles> getCurrentStyles() {
		return this.nodeStyles;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getPageSizeX()
	 */
	public int getPageSizeX() {
		return size.getWholeSizeX();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getPageSizeY()
	 */
	public int getPageSizeY() {
		return size.getWholeSizeY();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getScreenSizeX()
	 */
	public int getScreenSizeX() {
		return size.getViewSizeX();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getScreenSizeY()
	 */
	public int getScreenSizeY() {
		return size.getViewSizeY();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getUnvisitedLinkColor()
	 */
	public RGB getUnvisitedLinkColor() {
		return unvisited;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo#getVisitedLinkColor()
	 */
	public RGB getVisitedLinkColor() {
		return visited;
	}
}

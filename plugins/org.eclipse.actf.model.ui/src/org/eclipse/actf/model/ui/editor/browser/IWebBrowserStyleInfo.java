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
package org.eclipse.actf.model.ui.editor.browser;

import java.util.HashMap;

import org.eclipse.swt.graphics.RGB;

public interface IWebBrowserStyleInfo {

	/**
	 * @return Returns the Map between Elements (XPath) and node's current styles.
	 */
	public abstract HashMap<String, ICurrentStyles> getCurrentStyles();

	/**
	 * @return Returns the pageSizeX.
	 */
	public abstract int getPageSizeX();

	/**
	 * @return Returns the pageSizeY.
	 */
	public abstract int getPageSizeY();

	/**
	 * @return Returns the screenSizeX.
	 */
	public abstract int getScreenSizeX();

	/**
	 * @return Returns the screenSizeY.
	 */
	public abstract int getScreenSizeY();

	/**
	 * @return Returns the unvisited link color in String format.
	 */
	public abstract String getUnvisitedLinkColorStr();

	/**
	 * @return Returns the visited link color in String format.
	 */
	public abstract String getVisitedLinkColorStr();

	/**
	 * @return Returns the unvisited link color in RGB format.
	 */
	public abstract RGB getUnvisitedLinkColor();

	/**
	 * @return Returns the visited link color in RGB format.
	 */
	public abstract RGB getVisitedLinkColor();
	
}
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

import java.net.URL;

import org.eclipse.swt.graphics.Rectangle;

public interface ICurrentStyles {

	/**
	 * @return
	 */
	public abstract String getXPath();
	
	/**
	 * @return
	 */
	public abstract String getTagName();


	/**
	 * @return
	 */
	public abstract Rectangle getRectangle();

	
	/**
	 * @return
	 */
	public abstract boolean isLink();

	/**
	 * @return
	 */
	public abstract URL getLinkURL();
	
	
	/**
	 * @return
	 */
	public abstract String getBackgroundColor();

	/**
	 * @return
	 */
	public abstract String getBackgroundRepeat();

	/**
	 * @return
	 */
	public abstract String getColor();

//	/**
//	 * @return
//	 */
//	public abstract String getCssText();	//style
//	
//	/**
//	 * @return
//	 */
//	public abstract String getFontWeight(); //style

	
	/**
	 * @return
	 */
	public abstract String getDisplay();

	/**
	 * @return
	 */
	public abstract String getFontFamily();

	/**
	 * @return
	 */
	public abstract String getFontSize();

	/**
	 * @return
	 */
	public abstract String getFontStyle();

	/**
	 * @return
	 */
	public abstract String getFontVariant();

	/**
	 * @return
	 */
	public abstract String getLetterSpacing();

	/**
	 * @return
	 */
	public abstract String getLineHeight();

	/**
	 * @return
	 */
	public abstract String getPosition();

	/**
	 * @return
	 */
	public abstract String getTextAlign();

	/**
	 * @return
	 */
	public abstract String getTextDecoration();


	/**
	 * @return
	 */
	public abstract String getVisibility();

}
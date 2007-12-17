/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Barry Feigenbaum - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.model.format;

/**
 * The IElementFormatter interface is implemented in order to allow user control of the content and format of
 * GUI trees created by the <codee>IGuiModel.printTree</code> API.
 *
 * @author Barry Feigenbaum
 */
public interface IElementFormatter
{

	/**
	 * format the element into the output StringBuffer
	 *
	 * @param element the GUI object to format; class depends on the active IGuiModel
	 * @param nest the element nesting level
	 * @param out the buffer to generate content into
	 */
	public void formatElement (Object element, int nest, StringBuffer out);
	
	/**
	 * gets the properties used in formatting the node
	 * 
	 * @return array of properties names used when formatting nodes with this formatter
	 */
	public String[] getDisplayedProperties ();
	
} // IElementFormatter

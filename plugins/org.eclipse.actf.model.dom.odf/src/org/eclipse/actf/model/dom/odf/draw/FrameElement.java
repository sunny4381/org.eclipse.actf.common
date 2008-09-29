/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tatsuya ISHIHARA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.odf.draw;

import java.util.Iterator;

import org.eclipse.actf.model.dom.odf.base.DrawingObjectElement;

/**
 * Interface for <draw:frame> element.
 */
public interface FrameElement extends DrawingObjectElement {
	public String getAttrTableEndCellAddress();

	public String getAttrTableEndX();

	public String getAttrTableEndY();

	public int getAttrDrawZIndex();

	public String getAttrSvgWidth();

	public String getAttrSvgHeight();

	public String getAttrSvgX();

	public String getAttrSvgY();

	public long getContentSize();

	public Iterator getChildIterator();
}
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
package org.eclipse.actf.model.dom.odf.base;

import org.eclipse.actf.model.dom.odf.draw.TextBoxElement;

public interface DrawingObjectElement extends DrawingObjectBaseElement {
	public String getAttrDrawCaptionId();

	long getHeight();

	long getWidth();

	long getX();

	long getY();

	void setHeight(long height);

	void setWidth(long width);

	void setX(long x);

	void setY(long y);

	long getPageIndex();

	long getZIndex();

	public TextBoxElement getBoundCaptionTextBoxElement();

	// for Notes8, Notes8 save document by ODF 1.1 schema,
	// but odf:version is set as 1.0
	public TextBoxElement getBoundCaptionTextBoxElement(double version);
}
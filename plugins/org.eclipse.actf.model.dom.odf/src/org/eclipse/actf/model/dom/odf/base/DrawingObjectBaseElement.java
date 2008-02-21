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

import org.eclipse.actf.model.dom.odf.svg.DescElement;
import org.eclipse.actf.model.dom.odf.svg.TitleElement;

public interface DrawingObjectBaseElement extends ODFElement {
	public TitleElement getSVGTitleElement();

	public DescElement getSVGDescElement();

	public ODFElement getShortDescElement();

	public ODFElement getLongDescElement();

	// for Notes8, Notes8 save document by ODF 1.1 schema,
	// but odf:version is set as 1.0
	public ODFElement getShortDescElement(double version);

	public ODFElement getLongDescElement(double version);
}
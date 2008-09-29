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

import java.util.List;

import org.eclipse.actf.model.dom.odf.base.ODFElement;
import org.eclipse.actf.model.dom.odf.presentation.NotesElement;
import org.w3c.dom.NodeList;

/**
 * Interface for <draw:page> element.
 */
public interface PageElement extends ODFElement {
	public ODFElement createObject(long x, long y, long width, long height);

	public String getAttrDrawName();

	public String getAttrDrawStyleName();

	public String getAttrDrawMasterPageName();

	public String getAttrDrawNavOrder();

	public int getPageIndex();

	public List<ODFElement> getChildNodesInNavOrder();

	public NodeList getDrawingObjectElements();

	public NotesElement getPresentationNotesElement();
}
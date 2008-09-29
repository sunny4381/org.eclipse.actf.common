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

import org.eclipse.actf.model.dom.odf.base.EmbedDrawingObjectElement;
import org.w3c.dom.NodeList;

/**
 * Interface for <draw:image-map> element.
 */
public interface ImageMapElement extends EmbedDrawingObjectElement {
	public ImageElement getImageElements();

	public NodeList getAreaElements();
}
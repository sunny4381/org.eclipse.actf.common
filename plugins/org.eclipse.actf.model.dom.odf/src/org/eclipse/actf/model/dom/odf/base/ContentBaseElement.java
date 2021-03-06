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

import org.eclipse.actf.model.dom.odf.ODFConstants.ContentType;
import org.eclipse.actf.model.dom.odf.range.IContentRange;

/**
 * ODF elements that has &lt;office:body&gt; element as parent should implement this interface.
 */
public interface ContentBaseElement extends ODFElement {
	/**
	 * Returns content type defined in ODFConstants.ContentType.
	 * 
	 * @return ODF document type
	 */
	public ContentType getContentType();

	/**
	 * Returns newly created range class.
	 * 
	 * @return new range instance
	 */
	IContentRange createRange();
}
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
package org.eclipse.actf.model.dom.odf.style.impl;

import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.impl.ODFElementImpl;
import org.eclipse.actf.model.dom.odf.format.FormatConstants;
import org.eclipse.actf.model.dom.odf.style.StyleConstants;
import org.eclipse.actf.model.dom.odf.style.TableColumnPropertiesElement;
import org.w3c.dom.Element;


class TableColumnPropertiesElementImpl extends ODFElementImpl implements
		TableColumnPropertiesElement {
	private static final long serialVersionUID = -4691982278363837610L;

	protected TableColumnPropertiesElementImpl(ODFDocument odfDoc,
			Element element) {
		super(odfDoc, element);
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttrFormatBreakBefore() {
		if (hasAttributeNS(FormatConstants.FORMAT_NAMESPACE_URI,
				FormatConstants.ATTR_BREAK_BEFORE))
			return getAttributeNS(FormatConstants.FORMAT_NAMESPACE_URI,
					FormatConstants.ATTR_BREAK_BEFORE);
		return null;
	}

	public String getAttrStyleColumnWidth() {
		if (hasAttributeNS(StyleConstants.STYLE_NAMESPACE_URI,
				StyleConstants.ATTR_COLUMN_WIDTH))
			return getAttributeNS(StyleConstants.STYLE_NAMESPACE_URI,
					StyleConstants.ATTR_COLUMN_WIDTH);
		return null;
	}

	public boolean getAttrStyleUseOptimalColumnWidth() {
		if (hasAttributeNS(StyleConstants.STYLE_NAMESPACE_URI,
				StyleConstants.ATTR_USE_OPTIMAL_COLUMN_WIDTH)) {
			return new Boolean(getAttributeNS(
					StyleConstants.STYLE_NAMESPACE_URI,
					StyleConstants.ATTR_USE_OPTIMAL_COLUMN_WIDTH))
					.booleanValue();
		}
		return false;
	}
}
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
package org.eclipse.actf.model.dom.odf.office.impl;

import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.impl.ODFElementImpl;
import org.eclipse.actf.model.dom.odf.office.DocumentStylesElement;
import org.eclipse.actf.model.dom.odf.office.OfficeConstants;
import org.w3c.dom.Element;


class DocumentStylesElementImpl extends ODFElementImpl implements
		DocumentStylesElement {
	private static final long serialVersionUID = 5125587112923168320L;

	protected DocumentStylesElementImpl(ODFDocument odfDoc, Element element) {
		super(odfDoc, element);
	}

	public double getAttrOfficeVersion() {
		if (hasAttributeNS(OfficeConstants.OFFICE_NAMESPACE_URI,
				OfficeConstants.ATTR_VERSION)) {
			return new Double(getAttributeNS(
					OfficeConstants.OFFICE_NAMESPACE_URI,
					OfficeConstants.ATTR_VERSION)).doubleValue();
		}
		return -1.0;
	}
}
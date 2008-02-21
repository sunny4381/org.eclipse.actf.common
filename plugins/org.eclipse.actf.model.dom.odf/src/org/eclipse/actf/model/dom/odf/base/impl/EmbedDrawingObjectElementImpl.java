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
package org.eclipse.actf.model.dom.odf.base.impl;

import org.eclipse.actf.model.dom.odf.ODFException;
import org.eclipse.actf.model.dom.odf.base.EmbedDrawingObjectElement;
import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.ODFElement;
import org.eclipse.actf.model.dom.odf.draw.FrameElement;
import org.eclipse.actf.model.dom.odf.svg.DescElement;
import org.eclipse.actf.model.dom.odf.svg.TitleElement;
import org.eclipse.actf.model.dom.odf.text.SequenceElement;
import org.eclipse.actf.model.dom.odf.text.TextConstants;
import org.eclipse.actf.util.xpath.XPathUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public abstract class EmbedDrawingObjectElementImpl extends
		DrawingObjectBaseElementImpl implements EmbedDrawingObjectElement {

	protected EmbedDrawingObjectElementImpl(ODFDocument odfDoc, Element element) {
		super(odfDoc, element);
	}

	public FrameElement getFrameElement() {
		ODFElement parent = (ODFElement) this.getParentNode();
		if (parent instanceof FrameElement) {
			return (FrameElement) parent;
		} else {
			new ODFException(
					"parent of embed object element should be draw:frame element.")
					.printStackTrace();
		}
		return null;
	}

	public TitleElement getSVGTitleElement() {
		FrameElement frame = getFrameElement();
		if (frame == null)
			return null;
		return frame.getSVGTitleElement();
	}

	public DescElement getSVGDescElement() {
		FrameElement frame = getFrameElement();
		if (frame == null)
			return null;
		return frame.getSVGDescElement();
	}

	public SequenceElement getTextSequenceElement() {
		FrameElement frame = getFrameElement();
		if (frame == null)
			return null;

		NodeList nl = XPathUtil.evalXPathNodeList(frame,
				"../*[namespace-uri()='" + TextConstants.TEXT_NAMESPACE_URI
						+ "' and local-name()='"
						+ TextConstants.ELEMENT_SEQUENCE + "']");
		if ((nl != null) && (nl.getLength() == 1))
			return (SequenceElement) nl.item(0);
		if ((nl != null) && (nl.getLength() > 1)) {
			new ODFException(
					"draw:image has more than one text:sequence elements.")
					.printStackTrace();
		}
		return null;
	}
}
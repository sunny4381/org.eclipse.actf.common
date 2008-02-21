/*******************************************************************************
 * Copyright (c) 1998, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Goh KONDOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.html.errorhandler;

import java.io.IOException;

import org.eclipse.actf.model.dom.sgml.ParseException;
import org.eclipse.actf.model.dom.sgml.SGMLParser;
import org.eclipse.actf.model.dom.sgml.errorhandler.IErrorHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * If a parser using a DTD which does not define some element. this error
 * handler make the parser use specified dtd which defines it.
 */
public class UnknownElementErrorHandler implements IErrorHandler {
	private String unknownElements[];

	private String publicID;

	/**
	 * @param unknownElement
	 * @param DTD's
	 *            public ID which defines <code>unknownElement</code>
	 */
	public UnknownElementErrorHandler(String unknownElement, String publicID) {
		this.unknownElements = new String[1];
		unknownElements[0] = unknownElement;
		this.publicID = publicID;
	}

	public UnknownElementErrorHandler(String unknownElements[], String publicID) {
		this.unknownElements = unknownElements;
		this.publicID = publicID;
	}

	public boolean handleError(int code, SGMLParser parser, Node errorNode)
			throws ParseException, IOException {
		if (code == UNKNOWN_ELEMENT && errorNode instanceof Element) {
			for (int i = 0; i < unknownElements.length; i++) {
				if (errorNode.getNodeName()
						.equalsIgnoreCase(unknownElements[i])) {
					parser.error(UNKNOWN_ELEMENT, parser.getDTD()
							+ " does not define FRAMESET. "
							+ " Try to change DTD to " + publicID);
					parser.setupDTD(publicID);
					parser.pushBackNode(errorNode);
					return true;
				}
			}
		}
		return false;
	}
}

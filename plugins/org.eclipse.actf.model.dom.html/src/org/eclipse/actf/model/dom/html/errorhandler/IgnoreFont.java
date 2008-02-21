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

import org.eclipse.actf.model.dom.sgml.SGMLParser;
import org.eclipse.actf.model.dom.sgml.errorhandler.IErrorHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * If a parser using Strict or some DTD which does not define FRAMESET, this
 * error handler makes the parser use FRAMESET while parsing.
 */
public class IgnoreFont implements IErrorHandler {
	public boolean handleError(int code, SGMLParser parser, Node errorNode) {
		if (code == ILLEGAL_CHILD && errorNode instanceof Element
				&& errorNode.getNodeName().equalsIgnoreCase("FONT")) {
			parser.error(code, errorNode + " is illegal under "
					+ parser.getContext() + ".  So ignore it.");
			return true; // ignore the errorNode.
		} else {
			return false;
		}
	}
}

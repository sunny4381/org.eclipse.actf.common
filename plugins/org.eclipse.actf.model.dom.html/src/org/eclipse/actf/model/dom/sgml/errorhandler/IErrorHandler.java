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

package org.eclipse.actf.model.dom.sgml.errorhandler;

import java.io.IOException;

import org.eclipse.actf.model.dom.sgml.ISGMLConstants;
import org.eclipse.actf.model.dom.sgml.ParseException;
import org.eclipse.actf.model.dom.sgml.SGMLParser;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Node-level error handler interface.
 */
public interface IErrorHandler extends ISGMLConstants {
	/**
	 * Handles error whose type is specified by <code>code</code>
	 * 
	 * @param code
	 *            error type.
	 * @param parser
	 *            caller of this handler. This parser's state is easily changed
	 *            by the methods below.
	 * @param errorNode
	 *            a node that causes the error.
	 * @return <code>true</code> if error was handled. Otherwise <code>false
	 * </code>.
	 * @see SGMLParser#getNode()
	 * @see SGMLParser#pushBackNode(org.w3c.dom.Node)
	 * @see SGMLParser#getExtraErrInfo()
	 * @see SGMLParser#getContext()
	 * @see SGMLParser#setContext(org.w3c.dom.Element)
	 */
	public boolean handleError(int code, SGMLParser parser, Node errorNode)
			throws ParseException, IOException, SAXException;
}

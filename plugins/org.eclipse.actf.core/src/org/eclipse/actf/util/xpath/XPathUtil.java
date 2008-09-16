/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.xpath;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.actf.util.dom.EmptyNodeListImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for XPath evaluation
 */
public class XPathUtil {
	static XPath xpath = XPathFactory.newInstance().newXPath();

	// TODO merge this class into XPathService

	/**
	 * Evaluate the XPath expression in the specified context and return the
	 * result as NodeList.
	 * 
	 * @param context
	 *            the target context
	 * @param expression
	 *            the target XPath expression
	 * @return evaluation result as NodeList
	 */
	static public NodeList evalXPathNodeList(Node context, String expression) {
		try {
			return (NodeList) xpath.evaluate(expression, context,
					XPathConstants.NODESET);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return new EmptyNodeListImpl();
	}

	/**
	 * Canonicalize target String
	 * 
	 * @param targetS
	 *            the target String
	 * @return canonicalized String
	 */
	public static String canonicalize(String targetS) {
		return (targetS.replaceAll("\\p{Cntrl}", "").replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll(
						"\"", "&quot;").replaceAll("\'", "&apos;"));
	}

}

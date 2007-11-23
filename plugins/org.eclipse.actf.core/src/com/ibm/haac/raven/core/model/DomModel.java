/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Mike Squillace - initial API and implementation
 *******************************************************************************/

package com.ibm.haac.raven.core.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.haac.raven.core.traverse.DomTreeNodeWalker;

/**
 * serves as a model of a hierarchy of nodes that represent the basic elements
 * of an XML document. Nodes are represented by instances of
 * <code>org.w3c.dom.Node</code> instances as specified by the W3C DOM Level 2
 * specification.
 * 
 * <p>
 * Objects passed to the methods of this class should be instances of
 * <code>org.w3c.dom.Node</code>. Clients can extend this class to validate
 * XML or HTML documents.
 * 
 * @see org.w3c.dom.Node
 * @see <a href="http://www.w3.org/TR/DOM-Level-2-Core">Document Object Model
 *      (DOM) Level 2 Core Specification</a>
 * @author Mike Squillace
 */
public class DomModel extends AbstractModel {

	public static final String DOM_MODEL_TYPE = "dom";

	private static String[] PACKAGE_LIST = new String[] { "java.lang", "org.w3c.dom" };

	/**
	 * new DomModel instance; default is
	 * <code>com.ibm.haac.raven.core.config.Configuration.DOM_MODEL</code>
	 */
	public DomModel() {
		this(DOM_MODEL_TYPE);
	}

	public DomModel(String type) {
		super(type);
		treeNodeWalker = new DomTreeNodeWalker();
		setFilters();
	}

	/**
	 * {@inheritDoc} returns the tag name of this element if it is a W3C Dom
	 * element
	 */
	public String getNodeName(Object element) {
		return element instanceof Node && ((Node) element).getNodeType() == Node.ELEMENT_NODE
			? ((Element) element).getTagName() : null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Includes packages:
	 * <p>
	 * <ul>
	 * <li> java.lang
	 * <li> org.w3c.dom
	 * </ul>
	 */
	public String[] getPackageNames() {
		return PACKAGE_LIST;
	}

	/** {@inheritDoc} */
	public boolean isTopDown() {
		return false;
	}
} // DomModel

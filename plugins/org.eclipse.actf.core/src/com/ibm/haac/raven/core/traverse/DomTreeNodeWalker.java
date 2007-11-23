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

package com.ibm.haac.raven.core.traverse;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.haac.raven.core.model.InvalidComponentException;

/**
 * used to walk the nodes of a <code>DomModel</code>. In particular,
 * each node of trees walked should be an instance of <code>org.w3c.dom.Node</code>.
 * 
 * @see com.ibm.haac.raven.core.model.DomModel
 * @see org.w3c.dom.Node
 * @author Mike Squillace
 */
public class DomTreeNodeWalker extends AbstractTreeNodeWalker
{

	public DomTreeNodeWalker () {
		super();
	}

	/** {@inheritDoc} */
	public Object[] getChildren (Object element)
		throws InvalidComponentException {
		Object[] children = new Object[0];
		if (element instanceof Node) {
			Object bridgedChild = getBridgedChild(element);
			NodeList nodeList = ((Node) element).getChildNodes();
			if (children.length == 0 && bridgedChild != null) {
				children = new Object[] {bridgedChild};
			}else {
				ArrayList siblings = new ArrayList();
				int size = nodeList.getLength();
				for (int c = 0; c < size; ++c) {
					Node child = nodeList.item(c);
					if (child != null) {
						siblings.add(child);
					}
				}
				children = siblings.toArray();
			}
		}
		return children;
	}

	/** {@inheritDoc} */
	public Object getParent (Object element) throws InvalidComponentException {
		Object parent = null;
		if (element instanceof Node) {
			Object bridgedParent = getBridgedParent(element);
			parent = ((Node) element).getParentNode();
			if (parent == null && bridgedParent != null) {
				parent = bridgedParent;
			}
		}
		return parent;
	}
	
	/**
	 * returns empty array{@inheritDoc}
	 */
	public Object[] getStartNodes () {
		return new Object[0];
	}
	
} // DomTreeNodeWalker

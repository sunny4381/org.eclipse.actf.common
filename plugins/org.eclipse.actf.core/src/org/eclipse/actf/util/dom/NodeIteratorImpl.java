/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class NodeIteratorImpl implements NodeIterator {

	private IDocumentTraversal document;
	private Node root;
	private Node current = null;
	private int whatToShow;
	private NodeFilter filter;
	private NodeFilter defaultFilter;
	private boolean entitiyReferenceExpansion;
	private boolean isDetach = false;
	private boolean noFilter = true;
	private boolean isForward = true;

	private TreeWalkerImpl treeWalker;

	/**
	 * 
	 */
	public NodeIteratorImpl(IDocumentTraversal document, Node root,
			int whatToShow, NodeFilter filter, boolean entityReferenceExpansion)
			throws DOMException {
		if (null == root) {
			throw new DOMException(DOMException.NOT_FOUND_ERR,
					"Root can't be a null.");
		}
		if (null == document) {
			throw new DOMException(DOMException.NOT_FOUND_ERR,
					"Document can't be a null.");
		}
		this.document = document;
		this.root = root;
		this.whatToShow = whatToShow;
		this.filter = filter;
		this.noFilter = (null == filter);
		this.entitiyReferenceExpansion = entityReferenceExpansion;
		this.defaultFilter = new WhatToShowNodeFilter(whatToShow);

		this.treeWalker = new TreeWalkerImpl(root, whatToShow, filter,
				entityReferenceExpansion);
	}

	private short eval(Node target) {
		short flag = defaultFilter.acceptNode(target);

		// If a node is skipped by whatToShow flag, a NodeFiilter will not be
		// called.
		if (noFilter || flag == NodeFilter.FILTER_SKIP) {
			return flag;
		}
		return filter.acceptNode(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#detach()
	 */
	public void detach() {
		document.removeNodeIterator(this);

		isDetach = true;
		root = null;
		current = null;
		filter = null;
		defaultFilter = null;
		// whatToShow = Integer.MIN_VALUE;
		// entitiyReferenceExpansion = false;
		// noFilter = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#getExpandEntityReferences()
	 */
	public boolean getExpandEntityReferences() {
		return entitiyReferenceExpansion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#getFilter()
	 */
	public NodeFilter getFilter() {
		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#getRoot()
	 */
	public Node getRoot() {
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#getWhatToShow()
	 */
	public int getWhatToShow() {
		return whatToShow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#nextNode()
	 */
	public Node nextNode() throws DOMException {
		if (isDetach) {
			throw new DOMException(DOMException.INVALID_STATE_ERR, this
					.getClass().toString()
					+ " is detached.");
		}

		if (null == current) {// first call
			treeWalker.setCurrentNode(root);
			if (eval(root) == NodeFilter.FILTER_ACCEPT) {
				current = root;
			} else {
				current = treeWalker.nextNode();
			}
			isForward = true;
			return current;
		}

		if (isForward) {
			Node tmpN = treeWalker.nextNode();
			if (null == tmpN) {
				return null;
			}
			current = tmpN;
			return current;
		}
		// !isForward : only change the flag and return current
		isForward = true;
		return current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.traversal.NodeIterator#previousNode()
	 */
	public Node previousNode() throws DOMException {
		if (isDetach) {
			throw new DOMException(DOMException.INVALID_STATE_ERR, this
					.getClass().toString()
					+ " is detached.");
		}

		if (null == current) {
			return null;
		}

		if (!isForward) {
			Node tmpN = treeWalker.previousNode();
			if (null == tmpN) {
				return null;
			}
			current = tmpN;
			return current;
		}
		// isForward : only change the flag and return current
		isForward = false;
		return current;
	}

	private Node nonChildNextNode(Node target) {
		if (target == root) {
			return null;
		}
		Node tmpN = target.getNextSibling();
		if (null != tmpN) {
			return tmpN;
		}
		Node tmpP = target.getParentNode();
		while (null != tmpP && tmpP != root) {
			tmpN = tmpP.getNextSibling();
			if (null != tmpN) {
				return tmpN;
			}
			tmpP = tmpP.getParentNode();
		}
		return null;
	}

	private Node checkParent(Node target) {
		Node tmpN = current;
		while (null != tmpN && tmpN != root) {
			if (tmpN == target) {
				return target;
			}
			tmpN = tmpN.getParentNode();
		}
		return null;
	}

	public void prepareNodeRemove(Node target) {
		Node tmpN = checkParent(target);
		if (null != tmpN) {
			if (isForward) {
				treeWalker.setCurrentNode(tmpN);
				current = treeWalker.previousNode();
			} else {
				current = nonChildNextNode(tmpN);
				if (null == current) {
					// no next node in the iterator
					treeWalker.setCurrentNode(tmpN);
					current = treeWalker.previousNode();
					isForward = true;
				} else {
					treeWalker.setCurrentNode(current);
				}
			}
		}
	}

}

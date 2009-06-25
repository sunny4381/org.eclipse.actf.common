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

package org.eclipse.actf.model.internal.dom.sgml.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Base class of org.w3c.dom.Node implementation. This class implements most
 * methods except for a few which depends on concrete its classes.
 */
@SuppressWarnings("nls")
public abstract class SGMLParentNode extends SGMLNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3126509435625384557L;
	SGMLNode firstChild, lastChild;

	SGMLParentNode(Document doc) {
		super(doc);
	}

	abstract void check(Node newChild) throws DOMException;

	public Node cloneNode(boolean deep) {
		SGMLParentNode ret = null;
		if (deep && firstChild != null) {
			ret = cloneNodeDeep();
			if (ret == null)
				return null;
		} else {
			try {
				ret = (SGMLParentNode) clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
			ret.lastChild = ret.firstChild = null;
		}
		ret.parent = ret.nextSibling = ret.previousSibling = null;
		return ret;
	}

	private SGMLParentNode cloneNodeDeep() {
		try {
			SGMLParentNode ret = (SGMLParentNode) clone();
			ret.previousSibling = ret.nextSibling = ret.parent = ret.firstChild = ret.lastChild = null;
			if (this.firstChild == null)
				return ret;
			SGMLNode child = ret.firstChild = (SGMLNode) this.firstChild
					.cloneNode(true);
			child.parent = ret;
			for (SGMLNode source = this.firstChild.nextSibling; source != null; source = source.nextSibling) {
				child.nextSibling = (SGMLNode) source.cloneNode(true);
				child.nextSibling.previousSibling = child;
				child = child.nextSibling;
				child.parent = ret;
			}
			ret.lastChild = child;
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public NodeList getChildNodes() {
		return new NodeList() {
			public Node item(int index) {
				SGMLNode ret = firstChild;
				while (index > 0 && ret != null) {
					ret = ret.nextSibling;
					index--;
				}
				return index == 0 ? ret : null;
			}

			public int getLength() {
				int ret = 0;
				for (SGMLNode child = firstChild; child != null; child = child.nextSibling) {
					ret++;
				}
				return ret;
			}
		};
	}

	public Node getFirstChild() {
		return this.firstChild;
	}

	public Node getLastChild() {
		return this.lastChild;
	}

	public Node getPreviousSibling() {
		return previousSibling;
	}

	public boolean hasChildNodes() {
		return firstChild != null;
	}

	public Node appendChild(Node node) throws DOMException {
		if (node instanceof SGMLDocumentFragment
				&& node.getOwnerDocument() == this.getOwnerDocument()) {
			SGMLDocumentFragment fragment = (SGMLDocumentFragment) node;
			for (SGMLNode child = fragment.firstChild; child != null; child = child.nextSibling) {
				child.parent = this;
			}
			if (firstChild == null) {
				this.firstChild = fragment.firstChild;
				this.lastChild = fragment.lastChild;
			} else {
				this.lastChild.nextSibling = fragment.firstChild;
				fragment.firstChild.previousSibling = this.lastChild;
				this.lastChild = fragment.lastChild;
			}
			return node;
		}
		check(node);
		SGMLNode sgmlNode = (SGMLNode) node;
		if (sgmlNode.parent != null) {
			sgmlNode.parent.removeChild(sgmlNode);
		}
		sgmlNode.parent = this;
		if (firstChild == null) {
			this.firstChild = this.lastChild = sgmlNode;
			// added for performance reason @2009/06/25 by dsato@jp.ibm.com
			if (node instanceof Element) {
				processNodeForOptimization((Element) node);
			}
			return node;
		} else {
			this.lastChild.nextSibling = sgmlNode;
			sgmlNode.previousSibling = this.lastChild;
			this.lastChild = sgmlNode;
			// added for performance reason @2009/06/25 by dsato@jp.ibm.com
			if (node instanceof Element) {
				processNodeForOptimization((Element) node);
			}
			return node;
		}
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		if (refChild == null) {
			return appendChild(newChild);
		}
		SGMLNode sgmlRefChild = (SGMLNode) refChild;
		if (sgmlRefChild.parent != this) {
			throw new DOMException(DOMException.NOT_FOUND_ERR, "There isn't "
					+ refChild + " as a children") {

				/**
						 * 
						 */
				private static final long serialVersionUID = -401620698015402759L;
			};
		}
		if (newChild instanceof SGMLDocumentFragment
				&& newChild.getOwnerDocument() == this.getOwnerDocument()) {
			SGMLDocumentFragment fragment = (SGMLDocumentFragment) newChild;
			for (SGMLNode child = fragment.firstChild; child != null; child = child.nextSibling) {
				child.parent = this;
			}
			if (firstChild == refChild) {
				this.firstChild.previousSibling = fragment.lastChild;
				fragment.lastChild.nextSibling = this.firstChild;
				this.firstChild = fragment.firstChild;
			} else {
				fragment.firstChild.previousSibling = sgmlRefChild.previousSibling;
				fragment.lastChild.nextSibling = sgmlRefChild;
				sgmlRefChild.previousSibling.nextSibling = fragment.firstChild;
				sgmlRefChild.previousSibling = fragment.lastChild;
			}
			return newChild;
		}
		check(newChild);
		SGMLNode sgmlNewChild = (SGMLNode) newChild;
		if (sgmlNewChild.parent != null) {
			sgmlNewChild.parent.removeChild(sgmlNewChild);
		}

		if (this.firstChild == refChild) {
			this.firstChild.previousSibling = sgmlNewChild;
			sgmlNewChild.nextSibling = this.firstChild;
			sgmlNewChild.parent = this;
			this.firstChild = sgmlNewChild;
		} else {
			sgmlNewChild.previousSibling = sgmlRefChild.previousSibling;
			sgmlNewChild.nextSibling = sgmlRefChild;
			sgmlRefChild.previousSibling.nextSibling = sgmlNewChild;
			sgmlRefChild.previousSibling = sgmlNewChild;
			sgmlNewChild.parent = this;
		}

		// added for performance reason @2009/06/25 by dsato@jp.ibm.com
		if (newChild instanceof Element) {
			processNodeForOptimization((Element) newChild);
		}
		return newChild;
	}

	public Node removeChild(Node oldChild) throws DOMException {
		SGMLNode sgmlOldChild;
		if (firstChild == null || !(oldChild instanceof SGMLNode)
				|| (sgmlOldChild = (SGMLNode) oldChild).parent != this) {
			throw new DOMException(DOMException.NOT_FOUND_ERR, "There isn't "
					+ oldChild + " as a children") {

				/**
						 * 
						 */
				private static final long serialVersionUID = -4190622701270985282L;
			};
		}
		if (this.firstChild == oldChild) {
			if (this.firstChild == this.lastChild) {
				this.firstChild = this.lastChild = null;
			} else {
				this.firstChild = sgmlOldChild.nextSibling;
				this.firstChild.previousSibling = null;
			}
		} else if (this.lastChild == oldChild) {
			this.lastChild = sgmlOldChild.previousSibling;
			this.lastChild.nextSibling = null;
		} else {
			sgmlOldChild.nextSibling.previousSibling = sgmlOldChild.previousSibling;
			sgmlOldChild.previousSibling.nextSibling = sgmlOldChild.nextSibling;
		}
		sgmlOldChild.previousSibling = sgmlOldChild.nextSibling = null;
		sgmlOldChild.parent = null;

		// added for performance reason @2009/06/25 by dsato@jp.ibm.com
		if (oldChild instanceof Element) {
			processNodeForOptimization((Element) oldChild);
		}
		return oldChild;
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		check(newChild);
		SGMLNode sgmlNewChild = (SGMLNode) newChild;
		SGMLNode sgmlOldChild = (SGMLNode) oldChild;
		if (sgmlOldChild.parent != this) {
			throw new DOMException(DOMException.NOT_FOUND_ERR, this
					+ "doesn't have " + newChild + " as a child") {

				/**
						 * 
						 */
				private static final long serialVersionUID = 164773639627266417L;
			};
		}

		if (this.firstChild == oldChild) {
			if (this.firstChild != this.lastChild) {
				sgmlNewChild.nextSibling = this.firstChild.nextSibling;
				this.firstChild.nextSibling.previousSibling = sgmlNewChild;
			} else {
				this.lastChild = sgmlNewChild;
			}
			this.firstChild = sgmlNewChild;
			sgmlOldChild.previousSibling = sgmlOldChild.nextSibling = null;
			sgmlOldChild.parent = null;
			sgmlNewChild.parent = this;
			return newChild;
		}
		if (this.lastChild == oldChild) {
			this.lastChild.previousSibling.nextSibling = sgmlNewChild;
			sgmlNewChild.previousSibling = this.lastChild.previousSibling;
			this.lastChild = sgmlNewChild;
			sgmlOldChild.previousSibling = null;
			sgmlOldChild.parent = null;
			sgmlNewChild.parent = this;
			return newChild;
		}
		sgmlNewChild.previousSibling = sgmlOldChild.previousSibling;
		sgmlNewChild.nextSibling = sgmlOldChild.nextSibling;
		sgmlOldChild.previousSibling.nextSibling = sgmlNewChild;
		sgmlOldChild.nextSibling.previousSibling = sgmlNewChild;
		sgmlOldChild.previousSibling = sgmlOldChild.nextSibling = null;
		sgmlOldChild.parent = null;
		sgmlNewChild.parent = this;
		return newChild;
	}

	// DOM Level 2

	public void normalize() {
		for (SGMLNode n = this.firstChild; n != null;) {
			if (n.getNodeType() == TEXT_NODE) {
				if (n.nextSibling != null
						&& n.nextSibling.getNodeType() == TEXT_NODE) {
					((Text) n).appendData(((Text) n.nextSibling).getData());
					removeChild(n.nextSibling);
				} else {
					n = n.nextSibling;
				}
			} else {
				n.normalize();
				n = n.nextSibling;
			}
		}
	}

	/**
	 * @2009/06/25 by dsato@jp.ibm.com
	 * Optimization for the following functions
	 *  - getElementsByTagName
	 *  - getElementById
	 */
	
	protected void processNodeForOptimization(Element element) {
		String id = element.getAttribute("id");
		HashMap<String,Element> idMap = getIdMap(ownerDocument);
		if (id != null) {
			Node node = idMap.get(id);
			if (node == null) {
				idMap.put(id, element);
			}
		} else {
			if (idMap.containsValue(element)) {
				for (String key: idMap.keySet()) {
					if (idMap.get(key) == element) {
						idMap.remove(key);
						break;
					}
				}
			}
		}
		
		String name = element.getNodeName().toLowerCase();
		if (name != null) {
			ArrayList<Node> nodeList = getNodeList(ownerDocument, name);
			if (element.getParentNode() == null) {
				nodeList.remove(element);
			} else if (nodeList.size() == 0) {
				nodeList.add(element);
			} else {
				// find previous node whose tagName is <name>;
				Node node = findPreviousNodeByTagName(element, name);
				int index1 = 0;
				if (node != null) {
					index1 = nodeList.indexOf(node);
				}
				
				if (nodeList.contains(element)) {
					int index2 = nodeList.indexOf(element);
					if (index1 + 1 != index2) {
						nodeList.remove(index2);
						nodeList.add(index1+1, element);
					}
				} else {
					nodeList.add(index1+1, element);
				}
			}
		}
		updateNodeList(ownerDocument, name);
	}
	
	protected Node findPreviousNodeByTagName(Node element, String name) {
		while (true) {
			Node prev = element.getPreviousSibling();
			Node p = element.getParentNode();
			
			if (prev == null) {
				if (p != null) {
					element = p;
				} else {
					element = null;
				}
			} else {
				while(prev.getLastChild() != null) {
					prev = prev.getLastChild();
				}
				element = prev;
			}
			
			if (element == null) {
				break;
			} else if (element.getNodeName().toLowerCase().equals(name)) {
				return element;
			}
		}
		return null;
	}



	private static HashMap<Document,HashMap<String,ArrayList<Node>>> documentTagNameMap = new HashMap<Document,HashMap<String,ArrayList<Node>>>();
	
	protected static ArrayList<Node> getNodeList(Document doc, String name) {
		HashMap<String,ArrayList<Node>> tagNameMap = documentTagNameMap.get(doc);
		if (tagNameMap == null) {
			tagNameMap = new HashMap<String, ArrayList<Node>>();
			documentTagNameMap.put(doc, tagNameMap);
		}
		ArrayList<Node> nodeList = tagNameMap.get(name);
		if (nodeList == null) {
			nodeList = new ArrayList<Node>();
			tagNameMap.put(name, nodeList);
		}
		return nodeList;
	}
	
	private static HashMap<Document,HashMap<String,Element>> documentIdMap = new HashMap<Document,HashMap<String,Element>>();

	protected static HashMap<String,Element> getIdMap(Document doc) {
		HashMap<String,Element> idMap = documentIdMap.get(doc);
		if (idMap == null) {
			idMap = new HashMap<String, Element>();
			documentIdMap.put(doc, idMap);
		}
		return idMap;
	}
	
	private static HashMap<Document,HashMap<String, Long>> documentUpdatedMap = new HashMap<Document,HashMap<String, Long>>();

	protected static long getNodeListUpdatedAt(Document doc, String name) {
		HashMap<String, Long> updatedMap = documentUpdatedMap.get(doc);
		if (updatedMap == null) {
			updatedMap = new HashMap<String, Long>();
			documentUpdatedMap.put(doc, updatedMap);
		}
		Long l = updatedMap.get(name);
		if (l == null) {
			return -1;
		}
		return l;
	}

	protected static void updateNodeList(Document doc, String name) {
		HashMap<String, Long> updatedMap = documentUpdatedMap.get(doc);
		if (updatedMap == null) {
			updatedMap = new HashMap<String, Long>();
			documentUpdatedMap.put(doc, updatedMap);
		}
		updatedMap.put(name, (new Date()).getTime());
	}
}

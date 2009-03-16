/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl;

import java.util.HashMap;

import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl implements NamedNodeMap {
	private IDispatch attributes;
	private HashMap<AttrKey, Node> attrMap;
	private NodeImpl baseNode;

	private static class AttrKey {
		String ns;
		String name;

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof AttrKey))
				return false;
			AttrKey ak = (AttrKey) o;
			return ns.equals(ak.ns) && name.equals(ak.name);
		}

		@Override
		public int hashCode() {
			return ns.hashCode() ^ name.hashCode();
		}

		AttrKey(String ns, String name) {
			this.ns = ns;
			this.name = name;
		}
	}

	public NamedNodeMapImpl(NodeImpl baseNode, IDispatch attributes) {
		this.baseNode = baseNode;
		this.attributes = attributes;
	}

	private void initCache() {
		if (attrMap != null)
			return;
		attrMap = new HashMap<AttrKey, Node>();
		int len = getLength();
		for (int i = 0; i < len; i++) {
			Node n = item(i);
			AttrKey key = new AttrKey(n.getNamespaceURI(), n.getLocalName());
			attrMap.put(key, n);
		}
	}

	public Node item(int index) {
		try {
			IDispatch i = (IDispatch) attributes.invoke1(
					"item", Integer.valueOf(index)); //$NON-NLS-1$
			return baseNode.newNode(i);
		} catch (DispatchException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getLength() {
		Integer i = (Integer) Helper.get(attributes, "length"); //$NON-NLS-1$
		if (i == null)
			return 0;
		return i.intValue();
	}

	public Node getNamedItem(String name) {
		try {
			IDispatch i = (IDispatch) attributes.invoke1(
					"removeNamedItem", name); //$NON-NLS-1$
			return baseNode.newNode(i);
		} catch (DispatchException e) {
			return null;
		}
	}

	public Node setNamedItem(Node node) throws DOMException {
		if (!(node instanceof NodeImpl)) {
			Helper.notSupported();
		}
		NodeImpl ni = (NodeImpl) node;
		try {
			IDispatch i = (IDispatch) attributes.invoke1("setNamedItem", //$NON-NLS-1$
					ni.getINode());
			return baseNode.newNode(i);
		} catch (DispatchException e) {
			return null;
		}
	}

	public Node removeNamedItem(String name) throws DOMException {
		try {
			IDispatch i = (IDispatch) attributes.invoke1(
					"removeNamedItem", name); //$NON-NLS-1$
			return baseNode.newNode(i);
		} catch (DispatchException e) {
			return null;
		}
	}

	public Node getNamedItemNS(String namespaceURI, String localName) {
		initCache();
		Node n = attrMap.get(new AttrKey(namespaceURI, localName));
		return n;
	}

	public Node setNamedItemNS(Node node) throws DOMException {
		initCache();
		Node n = attrMap.get(new AttrKey(node.getNamespaceURI(), node
				.getLocalName()));
		return setNamedItem(n);
	}

	public Node removeNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		initCache();
		Node n = attrMap.get(new AttrKey(namespaceURI, localName));
		return removeNamedItem(n.getNodeName());
	}
}

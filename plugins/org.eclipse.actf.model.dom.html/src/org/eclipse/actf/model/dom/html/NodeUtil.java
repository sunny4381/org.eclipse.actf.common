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

package org.eclipse.actf.model.dom.html;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;

public class NodeUtil {
	public static Node commonAncester(Node anc1, Node anc2) {
		for (Node p1 = anc1.getParentNode(); p1 != null; p1 = p1
				.getParentNode()) {
			for (Node p2 = anc2.getParentNode(); p2 != null; p2 = p2
					.getParentNode()) {
				if (p1 == p2)
					return p1;
			}
		}
		return null;
	}

	/**
	 * Replaces <code>node</code> with its children. The following is an
	 * example:
	 * 
	 * <pre>
	 *      R          R     
	 *    / \        /|\    
	 *   A   B  -&gt;  A E F   
	 *      / \             
	 *     E   F            
	 *                      
	 *      remove B        
	 * </pre>
	 * 
	 * @param node
	 *            target node.
	 * @return <code>node</code> if succeeded. Otherwize, <code>null</code>.
	 * @throws IllegalArgumentException
	 */
	public static void remove(Node node) {
		Node parent = node.getParentNode();
		if (parent == null) {
			throw new IllegalArgumentException(node + " doesn't have a parent");
		}
		for (Node child = node.getFirstChild(); child != null; child = node
				.getFirstChild()) {
			node.removeChild(child);
			parent.insertBefore(child, node);
		}
		parent.removeChild(node);
	}

	/**
	 * Adds <code>node</code> and moves children specified by
	 * <code>from</code> and <code>to</code> to it. The following is an
	 * example:
	 * 
	 * <pre>
	 *      R         R       
	 *     /|\       / \      
	 *    A E F  -&gt; A   B     
	 *                 / \    
	 *                E   F   
	 *                        
	 *     add B ...          
	 * </pre>
	 * 
	 * @param node
	 *            target node
	 * @param from
	 *            beginning of the children moved to <code>node</code>. If
	 *            <code>null</code>, no child will be moved.
	 * @param to
	 *            end of the children moved to <code>node</code>
	 * @return <code>node</code> if succeeded. Otherwize, <code>null</code>.
	 * @throws IllegalArgumentException
	 */
	public static void add(Node parent, Node node, Node from, Node to) {
		if (parent == null) {
			throw new IllegalArgumentException("parent=null");
		} else if (node.getParentNode() != null) {
			throw new IllegalArgumentException(node + " already has its parent");
		} else if (from == null) {
			parent.insertBefore(node, null);
		} else if (from.getParentNode() != parent) {
			throw new IllegalArgumentException(from + "'s parent isn't "
					+ parent);
		} else if (to.getParentNode() != parent) {
			throw new IllegalArgumentException(to + "'s parent isn't " + parent);
		} else if (from != null) {
			for (Node child = from; child != null; child = child
					.getNextSibling()) {
				if (child == to) {
					parent.insertBefore(node, from);
					for (child = from; child != to; child = node
							.getNextSibling()) {
						parent.removeChild(child);
						node.insertBefore(child, null);
					}
					parent.removeChild(to);
					node.insertBefore(to, null);
					return;
				}
			}
			throw new IllegalArgumentException("No link from " + from + " to "
					+ to);
		}
	}

	public static Node commonAncester(HTMLCollection ancesters) {
		switch (ancesters.getLength()) {
		case 0:
			return null;
		case 1:
			return ancesters.item(0).getParentNode();
		default:
			Node ret = commonAncester(ancesters.item(0), ancesters.item(1));
			for (int i = 2; i < ancesters.getLength(); i++) {
				Node tmp = commonAncester(ancesters.item(i - 1), ancesters
						.item(i));
				if (ret != tmp) {
					ret = commonAncester(ret, tmp);
				}
			}
			return ret;
		}
	}
}

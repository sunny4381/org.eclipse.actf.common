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

package org.eclipse.actf.util.jxpath;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



public class DOMExNodePointer extends DOMNodePointer {
    private static final long serialVersionUID = 7107226604740248209L;

    private Node getDOMNode() {
        return (Node) getImmediateNode();
    }

    public DOMExNodePointer(Node node, Locale locale) {
        super(node, locale);
    }

    public DOMExNodePointer(Node node, Locale locale, String id) {
        super(node, locale, id);
    }

    public DOMExNodePointer(NodePointer parent, Node node) {
        super(parent, node);
    }

    public DOMExNodePointer(DOMNodePointer ptOrg) {
        super(ptOrg.getParent(), (Node) ptOrg.getImmediateNode());
    }
    
    /********************************************************************************
            Overriding Methods.
    ********************************************************************************/

    @Override
    public boolean testNode(NodeTest test) {
        if (test instanceof NodeTypeTest) {
            NodeTypeTest ntt = (NodeTypeTest) test;
            if (ntt.getNodeType()  == Compiler.NODE_TYPE_NODE)
                return true;
        } else if (test instanceof NodeNameTest) {
            NodeNameTest nnt = (NodeNameTest) test;
            String nsTest = nnt.getNamespaceURI();
            if (nsTest == null) {
                test = new NodeNameTest(nnt.getNodeName(), ""); //$NON-NLS-1$
            }
        }
        return super.testNode(test);
    }

    @Override
    public Pointer getPointerByID(JXPathContext context, String id) {
        Node node = getDOMNode();
        Document doc = node.getOwnerDocument();
        Element element = doc.getElementById(id);
        if (element == null) {
            return new NullPointer(getLocale(), id);
        }
        return new DOMExNodePointer(element, getLocale(), id);
    }

    @Override
    public NodePointer getParent() {
        NodePointer pointer = getImmediateParentPointer();
        while (pointer != null && pointer.isContainer()) {
            pointer = pointer.getImmediateParentPointer();
        }
        return pointer;
    }

    @Override
    public NodePointer getImmediateParentPointer() {
        if (parent != null) return parent;
        Node node = getDOMNode();
        Node parentNode = node.getParentNode();
        if (parentNode == null) return null;
        return new DOMExNodePointer(parentNode, getLocale());
    }
    
    @Override
    public Object getRootNode() {
        Node node = getDOMNode();
        return node.getOwnerDocument();
    }
    
    @Override
    public NodeIterator childIterator(NodeTest test,
                                      boolean reverse,
                                      NodePointer startWith) {
        if (test instanceof NodeNameTest) {
            NodeNameTest nnt = (NodeNameTest) test;
            String nsTest = nnt.getNamespaceURI();
            if (nsTest == null) {
                test = new NodeNameTest(nnt.getNodeName(), ""); //$NON-NLS-1$
            }
        }
        return new DOMExNodeIterator(this, test, reverse, startWith);
    }

    @Override
    public String getNamespaceURI() {
        Node node = getDOMNode();
        String ns = node.getNamespaceURI();
        // Treat the default namespace, "", as null.
        if (ns.length() == 0) return null;
        return ns;
    }
}

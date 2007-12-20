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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.eclipse.actf.util.xpath.XPathService;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




class XPathServiceImpl extends XPathService {
    static {
        DOMExNodePointerFactory.initialize();
    }

    @Override
    public Object compile(String path) {
        return JXPathContext.compile(path);
    }

    private static class NodeListImpl implements NodeList {
        private List<Node> nodeList;

        public Node item(int index) {
            if ((index < 0) || (index >= nodeList.size()))
                return null;
            return nodeList.get(index);
        }

        public int getLength() {
            return nodeList.size();
        }

        NodeListImpl(List<Node> nodeList) {
            if (nodeList == null) {
                this.nodeList = new ArrayList<Node>(0);
            } else {
                this.nodeList = nodeList;
            }
        }
    }

    @Override
    public NodeList evalForNodeList(Object compiled, Node base) {
        CompiledExpression ce = (CompiledExpression) compiled;
        JXPathContext bctx = JXPathContext.newContext(base.getOwnerDocument());
        Pointer ptr = new DOMExNodePointer(base, null);
        JXPathContext ctx = bctx.getRelativeContext(ptr);
        Iterator it = ce.iteratePointers(ctx);
        if (!it.hasNext()) return null;
        List<Node> result = new ArrayList<Node>();
        do {
            Pointer pt = (Pointer) it.next();
            Object o = pt.getNode();
            if (!(o instanceof Node)) continue;
            Node n = (Node) o;
            result.add(n);
        } while (it.hasNext());
        return new NodeListImpl(result);
    }

    @Override
    public String evalForString(Object compiled, Node base) {
        CompiledExpression ce = (CompiledExpression) compiled;
        JXPathContext bctx = JXPathContext.newContext(base.getOwnerDocument());
        Pointer ptr = new DOMExNodePointer(base, null);
        JXPathContext ctx = bctx.getRelativeContext(ptr);
        return ce.getValue(ctx).toString();
    }

    public static XPathService newInstance() {
        return new XPathServiceImpl();
    }

    private XPathServiceImpl() {
    }
}

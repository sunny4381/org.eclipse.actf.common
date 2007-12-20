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

import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.w3c.dom.Node;



public class DOMExNodePointerFactory implements NodePointerFactory {
    public static void initialize() {
        JXPathContextReferenceImpl.addNodePointerFactory(new DOMExNodePointerFactory());
    }

    private static final int FACTORY_ORDER = 0;
    public int getOrder() {
        return FACTORY_ORDER;
    }

    public NodePointer createNodePointer(QName name,
                                         Object nodeObj,
                                         Locale locale) {
        if (!(nodeObj instanceof Node)) return null;
        Node node = (Node) nodeObj;
        return new DOMExNodePointer(node, locale);
    }

    public NodePointer createNodePointer(NodePointer parent,
                                         QName name,
                                         Object nodeObj) {
        if (!(nodeObj instanceof Node)) return null;
        Node node = (Node) nodeObj;
        return new DOMExNodePointer(parent, node);
    }
    
    private DOMExNodePointerFactory() {
    }
}
    


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

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.dom.DOMNodeIterator;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;



public class DOMExNodeIterator extends DOMNodeIterator {
    DOMExNodeIterator(NodePointer parent,
                      NodeTest test,
                      boolean reverse,
                      NodePointer startWith) {
        super(parent, test, reverse, startWith);
    }

    @Override
    public NodePointer getNodePointer() {
        DOMNodePointer ptOrg = (DOMNodePointer) super.getNodePointer();
        return new DOMExNodePointer(ptOrg);
    }
}

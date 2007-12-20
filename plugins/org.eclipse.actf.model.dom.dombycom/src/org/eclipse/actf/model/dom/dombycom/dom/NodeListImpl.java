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

package org.eclipse.actf.model.dom.dombycom.dom;

import org.eclipse.actf.ai.comclutch.win32.DispatchException;
import org.eclipse.actf.ai.comclutch.win32.IDispatch;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class NodeListImpl implements NodeList {
    private IDispatch inodeCollection;
    private NodeImpl baseNode;

    public NodeListImpl(NodeImpl baseNode, IDispatch inodeCollection) {
        this.baseNode = baseNode;
        this.inodeCollection = inodeCollection;
    }

    public Node item(int index) {
        try {
            IDispatch i = (IDispatch) inodeCollection.invoke1("item", Integer.valueOf(index));
            return baseNode.newNode(i);
        } catch (DispatchException e) {
            return null;
        }
    }

    public int getLength() {
        Integer i = (Integer) Helper.get(inodeCollection, "length");
        if (i == null) return 0;
        return i.intValue();
    }
}

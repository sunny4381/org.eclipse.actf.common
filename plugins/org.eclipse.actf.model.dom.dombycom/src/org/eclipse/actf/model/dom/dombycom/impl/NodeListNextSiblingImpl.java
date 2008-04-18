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

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class NodeListNextSiblingImpl implements NodeList {
    private ArrayList<NodeImpl> inodeList = new ArrayList<NodeImpl>();

    private final NodeImpl baseNode;

    public NodeListNextSiblingImpl(NodeImpl baseNode) {
        this.baseNode = baseNode;
        initialize();
    }

    public void initialize() {
        inodeList.clear();
        NodeImpl node = (NodeImpl) baseNode.getFirstChild();
        while (node != null) {
            if ("HTML".equals(node.getNodeName()) && !("#document".equals(baseNode.getNodeName()))) {
                node = (NodeImpl) node.getNextSibling();
                continue;
            }
                
            inodeList.add(node);
            node = (NodeImpl) node.getNextSibling();
        }
    }

    public Node item(int index) {
        try {
            return inodeList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getLength() {
        return inodeList.size();
    }
}

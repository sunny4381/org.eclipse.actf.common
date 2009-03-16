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

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class ListNodeListImpl implements NodeList {
    private final List<Node> l;

    public Node item(int index) {
        if ((index < 0) || (index >= l.size())) return null;
        return l.get(index);
    }

    public int getLength() {
        return l.size();
    }

    public ListNodeListImpl(List<Node> l) {
        this.l = l;
    }
}

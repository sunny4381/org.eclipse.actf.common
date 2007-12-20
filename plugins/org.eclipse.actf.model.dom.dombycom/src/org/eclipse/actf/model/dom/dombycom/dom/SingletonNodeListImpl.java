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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class SingletonNodeListImpl implements NodeList {
    private final Node node;
    public Node item(int index) {
        if (index == 0) return node;
        return null;
    }

    public int getLength() {
        return 1;
    }

    public SingletonNodeListImpl(Node node) {
        this.node = node;
    }
}

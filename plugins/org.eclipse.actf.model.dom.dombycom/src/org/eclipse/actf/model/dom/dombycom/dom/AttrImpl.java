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

import org.eclipse.actf.util.comclutch.win32.IDispatch;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;



public class AttrImpl extends NodeImpl implements Attr {
    AttrImpl(NodeImpl baseNode, IDispatch inode) {
        super(baseNode, inode, Node.ATTRIBUTE_NODE);
    }

    public String getName() {
        return (String) Helper.get(inode, "name");
    }

    public boolean getSpecified() {
        // We regard `this' attribute is always specified in HTML DOM whenever `this' exists.
        return true;
    }

    public String getValue() {
        return (String) Helper.get(inode, "value");
    }

    public void setValue(String value) throws DOMException {
        Helper.put(inode, "value", value);
    }

    public Element getOwnerElement() {
        IDispatch i = (IDispatch) Helper.get(inode, "parentNode");
        if (i == null) return null;
        return (Element) newNode(i, Node.ELEMENT_NODE);
    }

    public TypeInfo getSchemaTypeInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isId() {
        // TODO Auto-generated method stub
        return false;
    }
}

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

package org.eclipse.actf.model.dom.dombycom.impl.html;

import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;



abstract class CharacterDataImpl extends NodeImpl implements CharacterData {
    CharacterDataImpl(NodeImpl baseNode, IDispatch inode, short type) {
        super(baseNode, inode, type);
    }

    public String getData() throws DOMException {
        return (String) Helper.get(inode, "data");
    }

    public void setData(String data) throws DOMException {
        Helper.put(inode, "data", data);
    }

    public int getLength() {
        Integer i = (Integer) Helper.get(inode, "length");
        if (i == null) return 0;
        return i.intValue();
    }

    public String substringData(int offset, int count) throws DOMException {
        String r = (String) inode.invoke("substringData",
                                         new Object[]{Integer.valueOf(offset),
                                                      Integer.valueOf(count)});
        return r;
    }

    public void appendData(String str) throws DOMException {
        inode.invoke1("appendData", str);
    }

    public void insertData(int offset, String str) throws DOMException {
        inode.invoke("insertData",
                     new Object[]{Integer.valueOf(offset), str});
    }

    public void deleteData(int offset, int count) throws DOMException {
        inode.invoke("deleteData",
                     new Object[]{Integer.valueOf(offset),
                                  Integer.valueOf(count)});
    }

    public void replaceData(int offset, int count, String str) throws DOMException {
        inode.invoke("replaceData",
                     new Object[]{Integer.valueOf(offset),
                                  Integer.valueOf(count),
                                  str});
    }
    
    public AbstractTerms getTerms(){
        return HTMLTerms.getInstance();
    }
}

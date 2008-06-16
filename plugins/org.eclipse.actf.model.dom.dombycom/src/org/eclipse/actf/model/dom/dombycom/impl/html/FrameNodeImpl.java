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

import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeListImpl;
import org.eclipse.actf.model.dom.dombycom.impl.SingletonNodeListImpl;
import org.eclipse.actf.model.dom.dombycom.impl.StyleSheetImpl;
import org.eclipse.actf.util.dom.EmptyNodeListImpl;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IEnumUnknown;
import org.eclipse.actf.util.win32.comclutch.IOleContainer;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




class FrameNodeImpl extends HTMLElementImpl {
    FrameNodeImpl(NodeImpl baseNode, IDispatch idisp) {
        super(baseNode, idisp);
    }
    
    @Override
    public NodeList getElementsByTagName(String tagName){
        IDispatch document = (IDispatch) Helper.get(inode, "document");
        IDispatch r = (IDispatch) document.invoke1("getElementsByTagName", tagName);
        if (r == null) 
            return null;
        return new NodeListImpl(this, r);
    }
    
    private int getFrameIndex() {
        NodeList frame = getElementsByTagName("frame");
        if (frame.getLength() > 0)
            return getFrameIndex(frame);
        
        NodeList iframe = getElementsByTagName("iframe");
        if (iframe.getLength() > 0)
            return getFrameIndex(iframe);
        
        return -1;
    }
    
    private int getFrameIndex(NodeList frame) {
        int len = frame.getLength();
        for (int i = 0; i < len; i++) {
            Node node = frame.item(i);
            if (!(node instanceof INodeEx))
                continue;
            if(((INodeEx) node).isSameNode(this)) 
                return i;
        }
        return -1;
    }

    public IDispatch getFrameByIndex(IDispatch document, int index) {
        IOleContainer iole = (IOleContainer) document.queryInterface(IUnknown.IID_IOleContainer);
        IEnumUnknown ieu = iole.enumObjects(IOleContainer.OLECONTF_EMBEDDINGS);
        IUnknown[] iunks;
        int count = -1;
        for (iunks = ieu.next(1); iunks != null && iunks.length > 0; iunks = ieu.next(1)) {
            try {
                IDispatch idisp = (IDispatch) iunks[0].queryInterface(IUnknown.IID_IWebBrowser2);
                iunks[0].release();
                if (idisp == null)
                    continue;
                count++;
                if (count == index) {
                    return idisp;
                }
            } catch (DispatchException ex){
            }
        }
        return null;
    }

    private Element getFrameRootElement() {
        IDispatch document = (IDispatch) Helper.get(inode, "document");
        
        int index = getFrameIndex();
        if (index < 0) 
            return null;
        
        IDispatch idisp = getFrameByIndex(document, index);
        if (idisp == null)
            return null;

        IDispatch idoc = (IDispatch) Helper.get(idisp, "Document");
        if (idoc == null)
            return null;
        IDispatch ielem = (IDispatch) Helper.get(idoc, "documentElement");
        if (ielem == null)
            return null;

        initialize(idoc);
        return (Element) newNode(ielem, Node.ELEMENT_NODE, this);
    }

    private void initialize(IDispatch doc) {
        try {
            IDispatch r = (IDispatch) doc.invoke0("createStyleSheet");
            if (r == null)
                return;
            StyleSheetImpl style = new StyleSheetImpl(r);
            if (style != null) {
                style.addRule(".CSStoHighlight", "border: 3px solid green; background: #fdd;");
            }
        } catch (DispatchException e) {

        }
    }

    //--------------------------------------------------------------------------------
    // Node Overriding Impl.
    //--------------------------------------------------------------------------------

    @Override
    public Node getFirstChild() {
        return getFrameRootElement();
    }

    @Override
    public Node getLastChild() {
        return getFrameRootElement();
    }

    @Override
    public NodeList getChildNodes() {
        Node n = getFrameRootElement();
        if (n == null)
            return EmptyNodeListImpl.getInstance();
        return new SingletonNodeListImpl(n);
    }

    @Override
    public boolean hasChildNodes() {
        Node n = getFrameRootElement();
        if (n == null)
            return false;
        return true;
    }

}

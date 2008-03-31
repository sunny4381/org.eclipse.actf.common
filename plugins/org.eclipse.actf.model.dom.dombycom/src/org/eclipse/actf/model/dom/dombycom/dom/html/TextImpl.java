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

package org.eclipse.actf.model.dom.dombycom.dom.html;


import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.dom.Helper;
import org.eclipse.actf.model.dom.dombycom.dom.NodeImpl;
import org.eclipse.actf.util.comclutch.win32.DispatchException;
import org.eclipse.actf.util.comclutch.win32.IDispatch;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;



class TextImpl extends CharacterDataImpl implements Text, INodeEx {
    TextImpl(NodeImpl baseNode, IDispatch inode) {
        super(baseNode, inode, Node.TEXT_NODE);
    }

    public Text splitText(int offset) throws DOMException {
        try {
            IDispatch r = (IDispatch) inode.invoke1("splitText", Integer.valueOf(offset));
            if (r == null) return null;
            return (Text) newNode(r, Node.TEXT_NODE);
        } catch (DispatchException e) {
            return null;
        }
    }

    public boolean isElementContentWhitespace() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getWholeText() {
        // TODO Auto-generated method stub
        return null;
    }

    public Text replaceWholeText(String content) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    //--------------------------------------------------------------------------------
    // INodeEx interface
    //--------------------------------------------------------------------------------

    public boolean doClick() {
        Node parent = getParentNode();
        if (parent instanceof INodeEx) {
            return ((INodeEx) parent).doClick();
        }
	return false;
    }

    public boolean highlight() {
        Node parent = getParentNode();
        if (parent instanceof INodeEx) {
            return ((INodeEx) parent).highlight();
        }
        return false;
    }

    public boolean unhighlight() {
        Node parent = getParentNode();
        if (parent instanceof INodeEx) {
            return ((INodeEx) parent).unhighlight();
        }
        return false;
    }

    public String extractString() {
        String nv = getNodeValue();
        if (nv == null) return "";
        nv = Helper.trimHTMLStr(nv);

        Node parent = getParentNode();
        
        if (!(parent instanceof ElementImpl))
            return nv;
        
        if (!"ABBR".equals(parent.getNodeName()) //
                && !"ACRONYM".equals(parent.getNodeName())) 
            return nv;
        
        ElementImpl el = (ElementImpl) parent;
        
        String title = el.getAttribute("title");
        
        return nv + " ("+title+")";
    }

    public boolean setFocus() {
        return false;
    }

    public Rectangle getLocation(){
        Node parent = getParentNode();
        while (parent != null && !(parent instanceof IElementEx)){
            parent = parent.getParentNode();
        }
        if (parent == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        return ((INodeEx) parent).getLocation();
    }

    public char getAccessKey() {
        Node parent = getParentNode();
        
        if (!(parent instanceof INodeEx)) 
            return 0;
        
        return ((INodeEx) parent).getAccessKey();
    }
}

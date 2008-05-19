/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import java.util.ArrayList;

import org.eclipse.actf.model.dom.dombycom.IFlashMSAANode;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.model.dom.dombycom.impl.html.ElementImpl;
import org.eclipse.actf.model.flash.util.FlashMSAAUtil;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.msaa.MSAA;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class FlashMSAANodeImpl extends ElementImpl implements IFlashMSAANode {
    public String getID() {
        return aObject.getAccKeyboardShortcut();
    }
    
    @Override
    public int hashCode() {
        String id = this.getID();
        if (id == null)
            return super.hashCode();
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IFlashMSAANode))
            return super.equals(o);
        IFlashMSAANode msaa = (IFlashMSAANode) o;
        String id1 = this.getID();
        String id2 = msaa.getID();
        if (id1 == null || id2 == null)
            return super.equals(o);
        return id1.equals(id2);
    }
   
    FlashMSAAObject aObject;
    
    private final boolean isTop;
    
    private final FlashMSAANodeImpl topNode;

    protected ElementImpl base;

    private NodeImpl parent;

    private int number;

    private FlashMSAANodeImpl(ElementImpl impl, IDispatch inode, FlashMSAAObject aObject) {
        super(impl, inode);
        this.base = impl;
        this.parent = null;
        this.aObject = searchFlash(aObject);
        this.topNode = this;
        this.isTop = true;
    }

    private FlashMSAAObject searchFlash(FlashMSAAObject top) {
        try {
            return searchFlash(top, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private FlashMSAAObject searchFlash(FlashMSAAObject top, int n) {
        if (top == null)
            return null;
        FlashMSAAObject[] children = top.getChildren();
        //System.out.println(children.length);
        for (int i = 0; i < children.length; i++) {
            if (children[i] == null)
                continue;
            if (FlashMSAAUtil.isFlash(children[i])) {
                IDispatch htmlElem = FlashMSAAUtil.getHtmlElementFromObject(children[i]);

                if (htmlElem != null) {
                    String targetUniqueID = (String) Helper.get(htmlElem, "uniqueID");
                    String myUniqueID = (String) base.getUniqueID();
                    if (myUniqueID.equals(targetUniqueID)) {
                        return children[i];
                    }
                }
            }
        }
        for (int i = 0; i < children.length; i++) {
            if (n > 0) {
                FlashMSAAObject iacc = searchFlash(children[i], n - 1);
                if(iacc != null)
	                return iacc;
            }
        }
        return null;
    }

    private FlashMSAANodeImpl(ElementImpl msaaBase, NodeImpl parent, FlashMSAANodeImpl topNode, FlashMSAAObject aObject) {
        super(msaaBase, null);
        this.base = msaaBase;
        this.aObject = aObject;
        this.parent = parent;
        this.topNode = topNode;
        this.isTop = false;
    }

    public static FlashMSAANodeImpl newMSAANode(ElementImpl impl, IDispatch inode) {
    	FlashMSAAObject iacc = FlashMSAAObjectFactory.getFlashMSAAObjectFromElement(inode);
    	
    	if (iacc != null) {
            FlashMSAANodeImpl ret = new FlashMSAANodeImpl(impl, inode, iacc);
            if (ret.aObject == null)
            	return null;
            return ret;
    	}

        return null;
    }


    public static long getHWNDFromObject(IUnknown unknown) {
    	FlashMSAAObject iacc = FlashMSAAObjectFactory.getFlashMSAAObjectFromElement(unknown);

    	if (iacc == null)
    		return 0;
    	
    	return iacc.getWindow();
    }


    @Override
    public Node getParentNode() {
        if (parent == null)
            return super.getParentNode();
        return parent;
    }

    public static class NodeListImpl implements NodeList {
        ArrayList<Node> list;

        FlashMSAANodeImpl parent;

        private boolean showOffscreen = true;

        private NodeListImpl(FlashMSAAObject[] aObjects, FlashMSAANodeImpl parent, boolean isTop, int total) {
            this.parent = parent;
            list = new ArrayList<Node>();
            for (int i = 0; i < aObjects.length; i++) {
                if (aObjects[i] != null) {
                    int accState = aObjects[i].getAccState();
                    FlashMSAANodeImpl node = null;
                    if (0 == (accState & MSAA.STATE_INVISIBLE)) {
                        node = new FlashMSAANodeImpl(parent.base, parent, parent.topNode, aObjects[i]);
                    } else if (showOffscreen && 0 != (accState & MSAA.STATE_OFFSCREEN)) {
                        node = new FlashMSAANodeImpl(parent.base, parent, parent.topNode, aObjects[i]);
                    }
                    if (node != null) {
                        node.number = total++;
                        list.add(node);
                    }
                }
            }
        }

        public int getLength() {
            return list.size();
        }

        public Node item(int index) {
            return list.get(index);
        }

    }

    @Override
    public String getLinkURI() {
        return "";
    }

    @Override
    public boolean doClick() {
        return aObject.doDefaultAction();
    }

    @Override
    public NodeList getChildNodes() {
        if (aObject == null) {
            return new NodeListImpl(new FlashMSAAObject[0], this, isTop, 0);
        }
        return new NodeListImpl(aObject.getChildren(), this, isTop, 0);
    }

    @Override
    public boolean hasChildNodes() {
        if (aObject == null)
            return false;
        return aObject.getChildCount() > 0;
    }

    @Override
    public Node getFirstChild() {
        NodeList list = new NodeListImpl(aObject.getChildren(), this, isTop, 0);
        if (list.getLength() > 0)
            return list.item(0);
        return null;
    }

    @Override
    public Node getLastChild() {
        NodeList list = new NodeListImpl(aObject.getChildren(), this, isTop, 0);
        if (list.getLength() > 0)
            return list.item(list.getLength() - 1);
        return null;
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public String extractString() {
        if (aObject == null)
            return "";

        int r = aObject.getAccRole();
        String accName = aObject.getAccName();
        if (accName == null)
            accName = "";

        if (r == MSAA.ROLE_SYSTEM_PUSHBUTTON) {
            if (accName.length() == 0) {
                accName = "" + number;
            }
        }
        if (accName.length() > 0)
            return accName;
        return accName;
    }

    @Override
    public String getTagName() {
        return super.getTagName();
    }

    @Override
    public boolean highlight() {
        aObject.select(MSAA.SELFLAG_TAKEFOCUS);
        return true;
    }


    @Override
    public boolean setFocus() {
        if (aObject == null)
            return false;
        aObject.select(MSAA.SELFLAG_TAKEFOCUS);
        return true;
    }

    @Override
    public boolean unhighlight() {
        if (aObject == null)
            return false;
        aObject.select(MSAA.SELFLAG_REMOVESELECTION);
        return true;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String getNodeName() {
        if (aObject == null)
            return "";
//        String id = aObject.getAccKeyboardShortcut();
//        System.out.println("ID = " + id);
        //
        String role = MSAA.getRoleText(aObject.getAccRole());
        String accName = aObject.getAccName();
        if (accName == null)
            accName = "";
        //return aObject.getAccLocation() + "(" + role + ")" + accName;
        return "(" + role + ")" + accName;
    }

    @Override
    public Node getPreviousSibling() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getNodeType() {
        // return FLASH_NODE;
        return Node.ELEMENT_NODE;
    }

    @Override
    public String getAttribute(String name) {
        return "";
    }

    public long getWindow() {
        return aObject.getWindow();
    }
    
    @Override
    public AbstractTerms getTerms(){
        return FlashMSAATerms.getInstance();
    }
    
    private IFlashMSAANode searchByIDInternal(String id) {
        String target = aObject.getAccKeyboardShortcut();
        if (id.equals(target)) return this;
        NodeList nl = getChildNodes();
        int len = nl.getLength();
        for (int i = 0; i < len; i++) {
            FlashMSAANodeImpl n = (FlashMSAANodeImpl) nl.item(i);
            IFlashMSAANode ret = n.searchByIDInternal(id);
            if (ret != null) return ret;
        }
        return null;
    }

    public IFlashMSAANode searchByID(String id) {
        return topNode.searchByIDInternal(id);
    }

    public INodeEx getBaseNode() {
        return base;
    }

    @Override
    public short getHeadingLevel() {
        if (isTop)
            return super.getHeadingLevel();
        return 0;
    }

    @Override
    public Rectangle getLocation() {
        return base.getLocation();
    }
}

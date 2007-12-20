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

package org.eclipse.actf.model.dom.dombycom.dom.flash;

import java.util.ArrayList;

import org.eclipse.actf.ai.comclutch.win32.ComService;
import org.eclipse.actf.ai.comclutch.win32.IDispatch;
import org.eclipse.actf.model.dom.dombycom.IMSAANode;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.dom.Helper;
import org.eclipse.actf.model.dom.dombycom.dom.NodeImpl;
import org.eclipse.actf.model.dom.dombycom.dom.html.ElementImpl;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.win32.COMUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.ole.win32.IUnknown;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.AccessibleObjectFactory;
import org.eclipse.actf.accservice.swtbridge.IServiceProvider;
import org.eclipse.actf.accservice.swtbridge.MSAA;



public class MSAANodeImpl extends ElementImpl implements IMSAANode {
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
        if (!(o instanceof IMSAANode))
            return super.equals(o);
        IMSAANode msaa = (IMSAANode) o;
        String id1 = this.getID();
        String id2 = msaa.getID();
        if (id1 == null || id2 == null)
            return super.equals(o);
        return id1.equals(id2);
    }
   
    AccessibleObject aObject;
    
    private final boolean isTop;
    
    private final MSAANodeImpl topNode;

    protected ElementImpl base;

    private NodeImpl parent;

    private int number;

    private MSAANodeImpl(ElementImpl impl, IDispatch inode, AccessibleObject aObject) {
        super(impl, inode);
        this.base = impl;
        this.parent = null;
        this.aObject = searchFlash(aObject);
        this.topNode = this;
        this.isTop = true;
    }

    private AccessibleObject searchFlash(AccessibleObject top) {
        try {
            return searchFlash(top, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private AccessibleObject searchFlash(AccessibleObject top, int n) {
        if (top == null)
            return null;
        AccessibleObject[] children = top.getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] == null)
                continue;
            if (isFlash(children[i])) {
                Variant v = getHTMLElementFromObject(children[i]);

                if (v != null) {
                    int ptr = v.getDispatch().getAddress();
                    IDispatch id = ComService.newIDispatch(inode.getResourceManager(), ptr, false);
                    String targetUniqueID = (String) Helper.get(id, "uniqueID");
                    String myUniqueID = (String) base.getUniqueID();
                    if (myUniqueID.equals(targetUniqueID)) {
                        return children[i];
                    }
                }
            }
        }
        for (int i = 0; i < children.length; i++) {
            if (n > 0) {
                AccessibleObject ao = searchFlash(children[i], n - 1);
                if(ao != null)
	                return ao;
            }
        }
        return null;
    }

    private MSAANodeImpl(ElementImpl msaaBase, NodeImpl parent, MSAANodeImpl topNode, AccessibleObject aObject) {
        super(msaaBase, null);
        this.base = msaaBase;
        this.aObject = aObject;
        this.parent = parent;
        this.topNode = topNode;
        this.isTop = false;
    }

    public static MSAANodeImpl newMSAANode(ElementImpl impl, IDispatch inode) {
        Variant v = getMSAAFromObject((int) inode.getPtr());
        if (null != v && OLE.VT_DISPATCH == v.getType()) {
            try {
                AccessibleObject aObject = AccessibleObjectFactory.getAccessibleObjectFromVariant(v);
                MSAANodeImpl ret = new MSAANodeImpl(impl, inode, aObject);
                if (ret.aObject == null)
                    return null;
                return ret;
            } finally {
                v.dispose();
            }
        }

        return null;
    }

    public static final GUID IID_IHTMLElement = COMUtil.IIDFromString("{3050f1ff-98b5-11cf-bb82-00aa00bdce0b}"); //$NON-NLS-1$

    public static final GUID IID_IAccessible = COMUtil.IIDFromString("{618736E0-3C3D-11CF-810C-00AA00389B71}"); // $NON-NLS-1$

    public static Variant getMSAAFromObject(int unknown) {
        IUnknown objUnknown = new IUnknown(unknown);
        int[] ppvServiceProvider = new int[1];
        if (OLE.S_OK == ((IUnknown) objUnknown).QueryInterface(IServiceProvider.IID, ppvServiceProvider)) {
            IServiceProvider sp = new IServiceProvider(ppvServiceProvider[0]);
            try {
                int[] ppvObject = new int[1];
                if (OLE.S_OK == sp.QueryService(IID_IAccessible, IID_IAccessible, ppvObject)) {
                    return new Variant(new org.eclipse.swt.internal.ole.win32.IDispatch(ppvObject[0]));
                }
                return null;
            } finally {
                sp.Release();
            }
        }
        return null;
    }

    public static long getHWNDFromObject(int unknown) {
        Variant v = getMSAAFromObject(unknown);
        if ((v == null) || (v.getType() != OLE.VT_DISPATCH))
            return 0;
        AccessibleObject aObject = AccessibleObjectFactory.getAccessibleObjectFromVariant(v);
        return aObject.getWindow();
    }

    public static Variant getHTMLElementFromObject(Object objUnknown) {
        if (objUnknown instanceof AccessibleObject) {
            objUnknown = ((AccessibleObject) objUnknown).getIAccessible();
        }
        if (objUnknown instanceof IUnknown) {
            int[] ppvServiceProvider = new int[1];
            if (OLE.S_OK == ((IUnknown) objUnknown).QueryInterface(IServiceProvider.IID, ppvServiceProvider)) {
                IServiceProvider sp = new IServiceProvider(ppvServiceProvider[0]);
                try {
                    int[] ppvObject = new int[1];
                    if (OLE.S_OK == sp.QueryService(IID_IHTMLElement, IID_IHTMLElement, ppvObject)) {
                        return new Variant(new org.eclipse.swt.internal.ole.win32.IDispatch(ppvObject[0]));
                    }
                    return null;
                } finally {
                    sp.Release();
                }
            }
        }
        return null;
    }

    public static boolean isFlash(AccessibleObject accObject) {
        return isFlashClass(accObject.getClassName()) || isInvisibleFlash(accObject);
    }

    private static boolean isInvisibleFlash(AccessibleObject accObject) {
        if (MSAA.ROLE_SYSTEM_CLIENT == accObject.getAccRole()) {
            String description = accObject.getAccDescription();
            if (null != description && description.startsWith("PLUGIN: type=")) { //$NON-NLS-1$
                return null != getHtmlAttribute(accObject, "WMode"); //$NON-NLS-1$
            }
        }
        return false;
    }

    public static String getHtmlAttribute(Object objUnknown, String name) {
        Variant varElement = getHTMLElementFromObject(objUnknown);
        if (null != varElement && OLE.VT_DISPATCH == varElement.getType()) {
            try {
                OleAutomation automation = varElement.getAutomation();
                int[] idAttr = automation.getIDsOfNames(new String[] { name });
                if (null != idAttr) {
                    Variant varAttr = automation.getProperty(idAttr[0]);
                    if (null != varAttr) {
                        if (null != varAttr && OLE.VT_BSTR == varAttr.getType()) {
                            return varAttr.getString();
                        }
                    }
                }
            } finally {
                varElement.dispose();
            }
        }
        return null;
    }

    private static boolean isFlashClass(String className) {
        return "MacromediaFlashPlayerActiveX".equals(className); //$NON-NLS-1$
    }

    @Override
    public Node getParentNode() {
        if (parent == null)
            return super.getParentNode();
        return parent;
    }

    public static class NodeListImpl implements NodeList {
        ArrayList<Node> list;

        MSAANodeImpl parent;

        private boolean showOffscreen = true;

        private NodeListImpl(AccessibleObject[] aObjects, MSAANodeImpl parent, boolean isTop, int total) {
            this.parent = parent;
            list = new ArrayList<Node>();
            for (int i = 0; i < aObjects.length; i++) {
                if (aObjects[i] != null) {
                    int accState = aObjects[i].getAccState();
                    MSAANodeImpl node = null;
                    if (0 == (accState & MSAA.STATE_INVISIBLE)) {
                        node = new MSAANodeImpl(parent.base, parent, parent.topNode, aObjects[i]);
                    } else if (showOffscreen && 0 != (accState & MSAA.STATE_OFFSCREEN)) {
                        node = new MSAANodeImpl(parent.base, parent, parent.topNode, aObjects[i]);
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
            return new NodeListImpl(new AccessibleObject[0], this, isTop, 0);
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

    public int getWindow() {
        return aObject.getWindow();
    }
    
    @Override
    public AbstractTerms getTerms(){
        return MSAATerms.getInstance();
    }
    
    private IMSAANode searchByIDInternal(String id) {
        String target = aObject.getAccKeyboardShortcut();
        if (id.equals(target)) return this;
        NodeList nl = getChildNodes();
        int len = nl.getLength();
        for (int i = 0; i < len; i++) {
            MSAANodeImpl n = (MSAANodeImpl) nl.item(i);
            IMSAANode ret = n.searchByIDInternal(id);
            if (ret != null) return ret;
        }
        return null;
    }

    public IMSAANode searchByID(String id) {
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

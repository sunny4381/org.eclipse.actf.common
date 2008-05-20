/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.IFlashNode;
import org.eclipse.actf.model.dom.dombycom.IFlashMSAANode;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.EmptyNodeListImpl;
import org.eclipse.actf.model.flash.as.ASObject;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.win32.comclutch.ComPlugin;
import org.eclipse.actf.util.win32.keyhook.ISendEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;



class FlashNodeImpl implements IFlashNode {
    final ASObject nodeASObj;
    final ASObject accInfo;
    private final String target;
    private final IFlashNode parent;
    private final boolean hasChildren;
    private FlashTopNodeImpl swf;
    private DocumentImpl doc;
    
    @Override
    public int hashCode(){
        return this.target.intern().hashCode();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof FlashNodeImpl){
            FlashNodeImpl f = (FlashNodeImpl) o;
            return swf.equals(f.swf) && this.target.equals(f.target);
        }
        return false;
    }

    private FlashNodeImpl(ASObject nodeASObj, IFlashNode parent) {
        this.nodeASObj = nodeASObj;
        this.target = (String) nodeASObj.get(ASObject.TARGET);
        this.accInfo = (ASObject) nodeASObj.get("accInfo");
        Object o = nodeASObj.get("isOpaqueObject");
        if ((o instanceof Boolean) && ((Boolean) o).booleanValue()) {
            this.hasChildren = false;
        } else {
            this.hasChildren = true;
        }
        this.parent = parent;
    }

    FlashNodeImpl(FlashNodeImpl baseNode, ASObject nodeASObj) {
        this(nodeASObj, baseNode);
        this.swf = baseNode.swf;
        this.doc = baseNode.doc;
    }

    FlashNodeImpl(FlashTopNodeImpl baseNode, ASObject nodeASObj) {
        this(nodeASObj, baseNode);
        this.swf = baseNode;
        this.doc = (DocumentImpl) baseNode.getOwnerDocument();
    }

    private Object getProperty(String prop) {
        return swf.getProperty(getTarget(), prop);
    }

    private void setProperty(String prop, Object value) {
        swf.setProperty(getTarget(), prop, value);
    }

    private static ISendEvent sendEvent;
    private ISendEvent getSendEvent() {
        if (sendEvent == null) {
            sendEvent = ComPlugin.getDefault().newSendEvent();
        }
        return sendEvent;
    }

    public String getNodeName() {
        return "#flashNode:" + getTarget();
    }

    public String getNodeValue() throws DOMException {
        return null;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public short getNodeType() {
        // return FLASH_NODE;
        return Node.ELEMENT_NODE;
    }

    public Node getParentNode() {
        return parent;
    }

    private static class NodeListImpl implements NodeList {
        private final IFlashNode[] nodes;

        public Node item(int index) {
            if ((index < 0) || (index >= nodes.length)) return null;
            return nodes[index];
        }

        public int getLength() {
            return nodes.length;
        }

        NodeListImpl(IFlashNode[] nodes) {
            this.nodes = nodes;
        }
    }

    public NodeList getChildNodes() {
        if (!hasChildren) return EmptyNodeListImpl.INSTANCE;
        IFlashNode[] r = getSWFChildNodes();
        return new NodeListImpl(r);
    }

    public Node getFirstChild() {
        if (!hasChildren) return null;
        IFlashNode[] r = getSWFChildNodes();
        if (r == null) return null;
        if (r.length == 0) return null;
        return r[0];
    }

    public Node getLastChild() {
        if (!hasChildren) return null;
        IFlashNode[] r = getSWFChildNodes();
        if (r == null) return null;
        if (r.length == 0) return null;
        return r[r.length - 1];
    }

    public Node getPreviousSibling() {
        throw new UnsupportedOperationException();
    }

    public Node getNextSibling() {
        throw new UnsupportedOperationException();
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public Document getOwnerDocument() {
        return doc;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node removeChild(Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node appendChild(Node newChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public boolean hasChildNodes() {
        // IFlashNode[] r = getInnerNodes();
        if (!hasChildren) return false;
        IFlashNode[] r = getSWFChildNodes();
        if (r == null) return false;
        if (r.length == 0) return false;
        return true;
    }

    public Node cloneNode(boolean deep) {
        throw new UnsupportedOperationException();
    }

    public void normalize() {
    }

    public boolean isSupported(String feature, String version) {
        return false;
    }

    public String getNamespaceURI() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public String getLocalName() {
        String target = getTarget();
        int idx = target.lastIndexOf('.');
        if (idx < 0) return target;
        return target.substring(idx + 1);
    }

    public boolean hasAttributes() {
        return false;
    }

    public String getBaseURI() {
        return null;
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }

    public String getTextContent() throws DOMException {
        return extractString();
    }

    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public boolean isSameNode(Node other) {
        return false;
    }

    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    public boolean isEqualNode(Node arg) {
        return false;
    }

    public Object getFeature(String feature, String version) {
        return null;
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new UnsupportedOperationException();
    }

    public Object getUserData(String key) {
        throw new UnsupportedOperationException();
    }

    //--------------------------------------------------------------------------------
    // INodeEx interface
    //--------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private String decodeString(String input) {
        if (input == null) return "";
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return input;
    }

    public String extractString() {
        String r = null;
        if (accInfo != null) {
            Object o = accInfo.get("name");
            if (o != null) {
                r = "" + o;
            }
        }
        if (r == null) {
            Object o = nodeASObj.get(ASObject.TEXT);
            if (o != null) {
                r = "" + o;
            }
        }
        if (r == null) return "";
        //return decodeString(r);
        return r;
    }

    public short getHeadingLevel() {
        return 0;
    }

    public String getLinkURI() {
        // TODO
        return null;
    }

    public boolean setText(String text) {
        setProperty(ASObject.TEXT, text);
        return true;
    }

    public String getText() {
        Object o = getProperty(ASObject.TEXT);
        if (o instanceof String) return (String) o;
        return "";
    }

    private int getObjNumber(Object o) {
        if (o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        return ((Double) o).intValue();
    }

    private boolean doHardwareClick() {
        long hwnd = swf.getHWND();
        int ix = getObjNumber(getX());
        int iy = getObjNumber(getY());
        int iw = getObjNumber(getW());
        int ih = getObjNumber(getH());
        int x = ix + iw / 4;
        int y = iy + ih / 4;

        // System.err.println("Click to:" + x + "," + y);

        //getSendEvent().postMouse(x, y, false);
        getSendEvent().focusWindow(hwnd);
        setFocus();
        getSendEvent().postMouse(x, y, false);
        boolean result = getSendEvent().postMouse(x, y, true);
        return result;
    }

    String getClickableTarget(String current) {
        while (current.length() > 0) {
            String tryOnRelease = current + ".onRelease";
            if (swf.getNodeFromPath(tryOnRelease) != null) {
                return current;
            }
            String tryOnPress = current + ".onPress";
            if (swf.getNodeFromPath(tryOnPress) != null) {
                return current;
            }
            int idx = current.lastIndexOf('.');
            if (idx <= 0) break;
            current = current.substring(0, idx);
        }
        return null;
    }

    public boolean doClick() {
        String current = getTarget();
        while (current.length() > 0) {
            String tryOnRelease = current + ".onRelease";
            if (swf.getNodeFromPath(tryOnRelease) != null) {
                swf.callMethod(current, "onRelease");
                return true;
            }
            String tryOnPress = current + ".onPress";
            if (swf.getNodeFromPath(tryOnPress) != null) {
                swf.callMethod(current, "onPress");
                return true;
            }
            if (true) break;
            int idx = current.lastIndexOf('.');
            if (idx <= 0) break;
            current = current.substring(0, idx);
        }
        if (doHardwareClick()) return true;
        return false;
    }

    public boolean highlight() {
        swf.callMethod(getTarget(), "onRollOver");
        boolean ret1 = swf.highlight();
        boolean ret2 = swf.setMarker(getX(), getY(), getW(), getH());
        return ret1 && ret2;
    }

    public boolean unhighlight() {
        swf.callMethod(getTarget(), "onRollOut");
        boolean ret1 = swf.unhighlight();
        boolean ret2 = swf.unsetMarker();
        return ret1 && ret2;
    }
    
    public boolean setFocus() {
        if (!swf.setFocus()) return false;
        return swf.setFocus(getTarget());
    }

    public int getNth() {
        // TODO!
        return 0;
    }

    //--------------------------------------------------------------------------------
    // IFlashNode interface
    //--------------------------------------------------------------------------------

    public String getTarget() {
        return target;
    }

    public IFlashNode getNodeFromPath(String path) {
        return swf.getNodeFromPath(path);
    }

    public IFlashNode getNodeAtDepth(int depth) {
        return swf.getNodeAtDepthWithPath(getTarget(), depth);
    }

    public IFlashNode[] getInnerNodes() {
        return swf.getInnerNodesWithPath(getTarget());
    }

    public IFlashNode[] getSWFChildNodes() {
        return swf.getSWFChildNodesWithPath(getTarget());
    }

    public int getDepth() {
        Integer target = (Integer) nodeASObj.get("depth");
        if (target != null) return target.intValue();
        return INVALID_DEPTH;
    }

    public int getCurrentFrame() {
        Integer target = (Integer) nodeASObj.get("currentFrame");
        if (target != null) return target.intValue();
        return -1;
    }

    public IFlashNode[] translate() {
        return swf.translateWithPath(getTarget());
    }

    public INodeEx getBaseNode() {
        return swf;
    }

    //--------------------------------------------------------------------------------
    // extensions.
    //--------------------------------------------------------------------------------

    // Double or Integer
    public Object getX() {
        return nodeASObj.get("x");
    }

    // Double or Integer
    public Object getY() {
        return nodeASObj.get("y");
    }

    // Double or Integer
    public Object getW() {
        return nodeASObj.get("w");
    }

    // Double or Integer
    public Object getH() {
        return nodeASObj.get("h");
    }

    public AnalyzedResult analyze(AnalyzedResult ar) {
        return ar;
    }

    public AbstractTerms getTerms() {
        return FlashTerms.getInstance();
    }
    
    public Rectangle getLocation() {
        // TODO
        return swf.getLocation();
    }
    //--------------------------------------------------------------------------------
    // Dummy
    //--------------------------------------------------------------------------------
    public IFlashMSAANode getMSAA(){
        return null;
    }

    public void repairFlash(){
    }

    // !FN!
    public String[] getStillPictureData() {
        return new String[3];
    }

    public char getAccessKey() {
        return 0;
    }


}

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

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.impl.html.HTMLElementFactory;
import org.eclipse.actf.model.dom.dombycom.impl.object.ObjectElementFactory;
import org.eclipse.actf.util.comclutch.win32.IDispatch;
import org.eclipse.actf.util.vocab.IEvalTarget;
import org.eclipse.actf.util.vocab.Vocabulary;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;



public class NodeImpl implements Node {
    protected final IDispatch inode;

    IDispatch getIDispatch() {
        return inode;
    }

    protected final short nodeType;

    private NodeImpl parentNode;

    private DocumentImpl doc;

    String uniqueName;

    private static String getUniqueName(IDispatch inode) {
        return (String) Helper.get(inode, "uniqueID");
    }

    private NodeImpl newNodeByType(IDispatch inode, short type) {
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            return new AttrImpl(this, inode);
        case Node.DOCUMENT_NODE:
            // return new DocumentImpl(inode);
            // We would like to keep object identity of DocumentImpl.
            // Thus returns the base DocumentImpl that must be the same as the document of "inode".
            return doc;

        case Node.CDATA_SECTION_NODE:
        case Node.COMMENT_NODE:
            // case Node.DOCUMENT_FLAGMENT_NODE:
        case Node.DOCUMENT_TYPE_NODE:
            return new NodeImpl(this, inode, type);

        case Node.ELEMENT_NODE:
            if (Helper.hasProperty(inode, "contentWindow")) {
                return HTMLElementFactory.create(this, inode, "FrameNode");
            }
            String name = (String) Helper.get(inode, "nodeName");
            if ("OBJECT".equals(name) || "EMBED".equals(name)) {
                NodeImpl newNode = ObjectElementFactory.create(this, inode);
                if (newNode != null)
                    return newNode;
            }
            return HTMLElementFactory.create(this, inode, name);

        case Node.ENTITY_NODE:
        case Node.ENTITY_REFERENCE_NODE:
        case Node.NOTATION_NODE:
        case Node.PROCESSING_INSTRUCTION_NODE:
            return new NodeImpl(this, inode, type);
        case Node.TEXT_NODE:
            return HTMLElementFactory.create(this, inode, "Text");

        default:
            return new NodeImpl(this, inode, type);
        }
    }

    NodeImpl newNode(IDispatch inode) {
        if (inode == null)
            return null;
        String uniqueName = getUniqueName(inode);
        if (uniqueName != null) {
            NodeImpl anode = doc.lookupNode(uniqueName);
            if (anode != null) {
                inode.release();
                return anode;
            }
        }
        Integer val = (Integer) Helper.get(inode, "nodeType");
        if (val == null)
            return null;
        short type = (short) val.intValue();
        NodeImpl node = newNodeByType(inode, type);

        if (uniqueName != null) {
            node.uniqueName = uniqueName;
            doc.regNode(uniqueName, node);
        }
        return node;
    }

    protected NodeImpl newNode(IDispatch inode, short type) {
        if (inode == null)
            return null;
        String uniqueName = getUniqueName(inode);
        if (uniqueName != null) {
            NodeImpl anode = doc.lookupNode(uniqueName);
            if (anode != null) {
                inode.release();
                return anode;
            }
            NodeImpl node = newNodeByType(inode, type);
            node.uniqueName = uniqueName;
            doc.regNode(uniqueName, node);
            return node;
        } else {
            return newNodeByType(inode, type);
        }
    }

    protected NodeImpl newNode(IDispatch inode, short type, NodeImpl parent) {
        NodeImpl node = newNode(inode, type);
        node.parentNode = parent;
        return node;
    }

    public IDispatch getINode() {
        return inode;
    }

    // Special constructor for Document Node.
    NodeImpl(IDispatch inode) {
        this.doc = (DocumentImpl) this;
        this.inode = inode;
        this.nodeType = Node.DOCUMENT_NODE;
    }

    protected NodeImpl(NodeImpl baseNode, IDispatch inode, short nodeType) {
        this.doc = baseNode.doc;
        this.inode = inode;
        this.nodeType = nodeType;
    }

    private String cachedNodeName = null;

    public String getNodeName() {
        if (cachedNodeName == null) {
            cachedNodeName = (String) Helper.get(inode, "nodeName");
        }
        return cachedNodeName;
    }

    public String getNodeValue() throws DOMException {
        Object text = Helper.get(inode, "nodeValue");
        if (text == null)
            return null;
        if (text instanceof String)
            return (String) text;
        if (text instanceof IDispatch) {
            return (String) Helper.get((IDispatch) text, "data");
        }
        return null;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        Helper.put(inode, "nodeValue", nodeValue);
    }

    public short getNodeType() {
        return nodeType;
    }

    public Node getParentNode() {
        if (parentNode == null) {
            IDispatch parent = (IDispatch) Helper.get(inode, "parentNode");
            parentNode = newNode(parent);
        }
        return parentNode;
    }

    private NodeListNextSiblingImpl cachedChildNodes;

    public NodeList getChildNodes() {
        if (cachedChildNodes != null) {
            cachedChildNodes.initialize();
        } else {
            if (false) {
                // Caution!
                // Due to IE's problem, "childNodes" property may have wrong value
                // unless the document is well-formed.  We avoid using this property,
                // and use "nextSibling" instead of that.
                IDispatch nodeCollection = (IDispatch) Helper.get(inode, "childNodes");
                if (nodeCollection == null)
                    return null;
                // cachedChildNodes = new NodeListImpl(this, nodeCollection);
            } else {
                cachedChildNodes = new NodeListNextSiblingImpl(this);
            }
        }
        return cachedChildNodes;
    }

    public Node getFirstChild() {
        IDispatch firstChild = (IDispatch) Helper.get(inode, "firstChild");
        return newNode(firstChild);
    }

    public Node getLastChild() {
        IDispatch lastChild = (IDispatch) Helper.get(inode, "lastChild");
        return newNode(lastChild);
    }

    public Node getPreviousSibling() {
        IDispatch previousSibling = (IDispatch) Helper.get(inode, "previousSibling");
        return newNode(previousSibling);
    }

    public Node getNextSibling() {
        IDispatch nextSibling = (IDispatch) Helper.get(inode, "nextSibling");
        return newNode(nextSibling);
    }

    public NamedNodeMap getAttributes() {
        IDispatch attributes = (IDispatch) Helper.get(inode, "attributes");
        if (attributes == null)
            return null;
        return new NamedNodeMapImpl(this, attributes);
    }

    public Document getOwnerDocument() {
        return doc;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        if (!((newChild instanceof NodeImpl) && (refChild instanceof NodeImpl))) {
            Helper.notSupported();
        }
        NodeImpl nci = (NodeImpl) newChild;
        NodeImpl rci = (NodeImpl) refChild;
        IDispatch i = (IDispatch) inode.invoke("insertBefore", new Object[] { nci.getINode(), rci.getINode() });
        return newNode(i);
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        if (!((newChild instanceof NodeImpl) && (oldChild instanceof NodeImpl))) {
            Helper.notSupported();
        }
        NodeImpl nci = (NodeImpl) newChild;
        NodeImpl oci = (NodeImpl) oldChild;
        IDispatch i = (IDispatch) inode.invoke("replaceChild", new Object[] { nci.getINode(), oci.getINode() });
        return newNode(i);
    }

    public Node removeChild(Node oldChild) throws DOMException {
        if (!(oldChild instanceof NodeImpl)) {
            Helper.notSupported();
        }
        NodeImpl oci = (NodeImpl) oldChild;
        IDispatch i = (IDispatch) inode.invoke1("removeChild", oci.getINode());
        return newNode(i);
    }

    public Node appendChild(Node newChild) throws DOMException {
        if (!(newChild instanceof NodeImpl)) {
            Helper.notSupported();
        }
        NodeImpl nci = (NodeImpl) newChild;
        IDispatch i = (IDispatch) inode.invoke1("appendChild", nci.getINode());
        return newNode(i);
    }

    public boolean hasChildNodes() {
        Boolean b = (Boolean) inode.invoke0("hasChildNodes");
        if (b == null)
            return false;
        return b.booleanValue();
    }

    public Node cloneNode(boolean deep) {
        IDispatch i = (IDispatch) inode.invoke1("cloneNode", Boolean.valueOf(deep));
        return newNode(i);
    }

    public void normalize() {
        // Do nothing.
    }

    public boolean isSupported(String feature, String version) {
        // TODO: Currently we regard all features are not supported.
        return false;
    }

    public String getNamespaceURI() {
        String r = (String) Helper.get(inode, "namespaceURI");
        if (r == null) return "";
        // Treat the local namespace as null for XPath 1.0 data model.
        if (r.length() == 0) return "";
        return r;
    }

    public String getPrefix() {
        String r = getNodeName();
        if (r == null)
            return "";
        int pos = r.indexOf(":");
        if (pos < 0)
            return "";
        return r.substring(0, pos);
    }

    public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public String getLocalName() {
        String r = getNodeName();
        if (r == null)
            return "";
        int pos = r.indexOf(":");
        if (pos < 0)
            return r;
        return r.substring(pos + 1);
    }

    public boolean hasAttributes() {
        IDispatch attributes = (IDispatch) Helper.get(inode, "attributes");
        if (attributes == null)
            return false;
        return true;
    }

    public String getBaseURI() {
        // No way to obtain base URI!
        return null;
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        // We do not support compareDocumentPosition
        return 0;
    }

    public String getTextContent() throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public boolean isSameNode(Node other) {
        if (this == other)
            return true;
        return false;
    }

    public String lookupPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    public String lookupNamespaceURI(String prefix) {
        throw new UnsupportedOperationException();
    }

    public boolean isEqualNode(Node arg) {
        throw new UnsupportedOperationException();
    }

    public Object getFeature(String feature, String version) {
        throw new UnsupportedOperationException();
    }

    public Object getUserData(String key) {
        throw new UnsupportedOperationException();
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    // --------------------------------------------------------------------------------
    // INodeEx interface
    // --------------------------------------------------------------------------------
    public String getLinkURI() {
        String uri = (String) Helper.get(inode, "href");
        if (uri == null) {
            Node p = getParentNode();
            if (p instanceof INodeEx) {
                return ((INodeEx) p).getLinkURI();
            }
            return null;
        }
        return uri;
    }

    public short getHeadingLevel() {
        Node parent = getParentNode();
        if (parent instanceof INodeEx) {
            return ((INodeEx) parent).getHeadingLevel();
        }
        return (short) 0;
    }
    
    public AnalyzedResult analyze(AnalyzedResult ar) {
        if (!Vocabulary.isValidNode().eval((IEvalTarget) this))
            return ar;
        Node n = getFirstChild();
        
        while (n != null) {
            if (n instanceof INodeEx) {
                ar = ((INodeEx) n).analyze(ar);
            }
            n = n.getNextSibling();
        }
        return ar;
    }

    public boolean setText(String text) {
        if (Helper.hasProperty(inode, "value")) {
            return Helper.put(inode, "value", text);
        } else {
            NodeImpl element = (NodeImpl) getParentNode();
            if ("TEXTAREA".equals(element.getLocalName())) {
                IDispatch tr = (IDispatch) element.inode.invoke0("createTextRange");
                return Helper.put(tr, "text", text);
            }
            return false;
        }
    }

    public String getText() {
        String ret = "";
        if (Helper.hasProperty(inode, "value")) {
            ret = (String) Helper.get(inode, "value");
        } else {
            NodeImpl element = (NodeImpl) getParentNode();
            if ("TEXTAREA".equals(element.getLocalName())) {
                IDispatch tr = (IDispatch) element.inode.invoke0("createTextRange");
                ret = (String) Helper.get(tr, "text");
            }
        }
        if (ret == null)
            return "";
        return ret;
    }

    public int getNth() {
        IDispatch current = inode;
        int i;
        for (i = 1; current != null; i++) {
            current = (IDispatch) Helper.get(current, "previousSibling");
        }
        return i;
    }

    public String getUniqueID() {
        return (String) Helper.get(inode, "uniqueID");
    }

    // !FN!
    public String[] getStillPictureData() {
        String[] ret = new String[3];
        ret[1] = (String) Helper.get(inode, "src");
        ret[2] = "";
        String mt = (String) Helper.get(inode, "mimeType");
        if (mt == null) {
            ret[0] = "";
        } else {
            mt = mt.toUpperCase();
            if (mt.contains("GIF")) {
                ret[0] = "image/gif";
            } else if (mt.contains("JPEG")) {
                ret[0] = "image/jpeg";
            } else if (mt.contains("PNG")) {
                ret[0] = "image/png";
            } else {
                ret[0] = "";
            }
        }
        return ret;
    }
}

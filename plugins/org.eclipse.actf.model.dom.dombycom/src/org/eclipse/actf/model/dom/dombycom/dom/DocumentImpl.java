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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.actf.model.dom.dombycom.IDocumentEx;
import org.eclipse.actf.model.dom.dombycom.dom.html.HTMLElementFactory;
import org.eclipse.actf.util.comclutch.win32.DispatchException;
import org.eclipse.actf.util.comclutch.win32.IDispatch;
import org.eclipse.actf.util.comclutch.win32.IEnumUnknown;
import org.eclipse.actf.util.comclutch.win32.IOleContainer;
import org.eclipse.actf.util.comclutch.win32.IUnknown;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;



public class DocumentImpl extends NodeImpl implements IDocumentEx {
    private IDispatch htmlDocument;

    // Unique ID -> NodeImpl
    private HashMap<String, NodeImpl> inodeMap;

    synchronized NodeImpl lookupNode(String uniqueName) {
        NodeImpl n = inodeMap.get(uniqueName);
        return n;
    }

    synchronized void regNode(String uniqueName, NodeImpl node) {
        inodeMap.put(uniqueName, node);
    }

    public DocumentImpl(IDispatch htmlDocument) {
        super(htmlDocument);
        inodeMap = new HashMap<String, NodeImpl>();
        this.htmlDocument = htmlDocument;
    }

    public Element getDocumentElement() {
        IDispatch inode = (IDispatch) Helper.get(htmlDocument, "documentElement");
        return (Element) newNode(inode, Node.ELEMENT_NODE);
    }

    public DocumentType getDoctype() {
        // TODO Auto-generated method stub
        return null;
    }

    public DOMImplementation getImplementation() {
        // TODO Auto-generated method stub
        return null;
    }

    public DocumentFragment createDocumentFragment() {
        // TODO Auto-generated method stub
        return null;
    }

    public Text createTextNode(String data) {
        // TODO Auto-generated method stub
        return null;
    }

    public Comment createComment(String data) {
        // TODO Auto-generated method stub
        return null;
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Attr createAttribute(String name) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public EntityReference createEntityReference(String name) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeList getElementsByTagName(String tagname) {
        IDispatch r = (IDispatch) inode.invoke1("getElementsByTagName", tagname);
        if (r == null)
            return null;
        return new NodeListImpl(this, r);
    }

    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Element getElementById(String elementId) {
        IDispatch i = (IDispatch) inode.invoke1("getElementById", elementId);
        if (i == null)
            return null;
        return (Element) newNode(i, Node.ELEMENT_NODE);
    }

    public List getElementsByIdInAllFrames(String id) {
        List<Node> r = new ArrayList<Node>();
        Element e = getElementById(id);
        if (e != null)
            r.add(e);
        
        List<Node> ids = getElementsByIdInFrames(htmlDocument, id);
        if (ids != null)
            r.addAll(ids);
        return r;
    }
    
    private List<Node> getElementsByIdInFrames(IDispatch htmlDocument, String id) {
        if (!hasFrames(htmlDocument))
            return null;
        
        List<Node> r = new ArrayList<Node>();
        IOleContainer iole = (IOleContainer) htmlDocument.queryInterface(IUnknown.IID_IOleContainer);
        IEnumUnknown ieu = iole.enumObjects(IOleContainer.OLECONTF_EMBEDDINGS);

        if (ieu != null) {
            IUnknown[] iunks;
            for (iunks = ieu.next(1); iunks != null && iunks.length > 0; iunks = ieu.next(1)) {
                try {
                    IDispatch idisp = (IDispatch) iunks[0].queryInterface(IUnknown.IID_IWebBrowser2);
                    if (idisp != null) {
                        IDispatch d = (IDispatch) Helper.get(idisp, "Document");
                        if (d == null)
                            continue;
                        List<Node> r2 = getElementsByIdInFrames(d, id);
                        if (r2 != null)
                            r.addAll(r2);
                        IDispatch in = (IDispatch) d.invoke1("getElementById", id);
                        if (in == null)
                            continue;
                        Node n = newNode(in, Node.ELEMENT_NODE);
                        if (n == null)
                            continue;
                        r.add(n);
                    }
                } catch (DispatchException ex) {
                }
            }
        }
        return r;
    }

    private boolean hasFrames(IDispatch htmlDocument) {
        IDispatch r = (IDispatch) htmlDocument.invoke1("getElementsByTagName", "frame");
        if (r != null) {
            NodeListImpl nl = new NodeListImpl(this, r);
            if (nl.getLength() > 0)
                return true;
        }
        
        r = (IDispatch) htmlDocument.invoke1("getElementsByTagName", "iframe");
        if (r != null) {
            NodeListImpl nl = new NodeListImpl(this, r);
            if (nl.getLength() > 0)
                return true;
        }
        
        return false;
    }

    public String getInputEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXmlEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getXmlStandalone() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        // TODO Auto-generated method stub

    }

    public String getXmlVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setXmlVersion(String xmlVersion) throws DOMException {
        // TODO Auto-generated method stub

    }

    public boolean getStrictErrorChecking() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setStrictErrorChecking(boolean strictErrorChecking) {
        // TODO Auto-generated method stub

    }

    public String getDocumentURI() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDocumentURI(String documentURI) {
        // TODO Auto-generated method stub

    }

    public Node adoptNode(Node source) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public DOMConfiguration getDomConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    public void normalizeDocument() {
        // TODO Auto-generated method stub

    }

    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public Element getTargetElement(String target) {
        NodeList list = getElementsByTagName("A");
        for (int i = 0; i < list.getLength(); i++) {
            Element el = (Element) list.item(i);
            if (target.equals(el.getAttribute("name"))) {
                return el;
            }
        }

        return getElementById(target);
    }

    // --------------------------------------------------------------------------------
    // private extension
    // --------------------------------------------------------------------------------

    public StyleSheetImpl createStyleSheet() {
        try {
            if (inode == null)
                return null;
            IDispatch r = (IDispatch) inode.invoke0("createStyleSheet");
            if (r == null)
                return null;
            return new StyleSheetImpl(r);
        } catch (DispatchException e) {
            return null;
        }
    }

    public Element createElement(String tagName) throws DOMException {
        return HTMLElementFactory.createElement(this, inode, tagName);
    }

    public String getCompatMode() {
        return (String) Helper.get(htmlDocument, "compatMode");
    }

    public IDispatch getWindow() {
        return (IDispatch) Helper.get(inode, "parentWindow");
    }

    public IDispatch getDocument() {
        return (IDispatch) Helper.get(inode, "documentElement");
    }

}

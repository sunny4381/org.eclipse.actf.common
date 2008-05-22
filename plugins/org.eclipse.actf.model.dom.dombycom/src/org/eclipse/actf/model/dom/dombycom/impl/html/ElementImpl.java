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

import java.util.HashSet;

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.DomByCom;
import org.eclipse.actf.model.dom.dombycom.IDocumentEx;
import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.dom.dombycom.impl.AttrImpl;
import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NamedNodeMapImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeListImpl;
import org.eclipse.actf.model.dom.dombycom.impl.StyleImpl;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.vocab.Vocabulary;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;



public class ElementImpl extends NodeImpl implements IElementEx {
    protected ElementImpl(NodeImpl baseNode, IDispatch inode) {
        super(baseNode, inode, Node.ELEMENT_NODE);
    }

    protected ElementImpl(DocumentImpl baseDoc, IDispatch inode) {
        super(baseDoc, inode, Node.ELEMENT_NODE);
    }

    public String getTagName() {
        return (String) Helper.get(inode, "tagName");
    }

    private static final Integer getSetAttributeFlag = Integer.valueOf(2);

    public String getAttribute(String name) {
        String r = (String) inode.invoke("getAttribute", new Object[] { name, getSetAttributeFlag });
        return r;
    }

    public void setAttribute(String name, String value) throws DOMException {
        inode.invoke("setAttribute", new Object[] { name, value, getSetAttributeFlag });
    }

    public void removeAttribute(String name) throws DOMException {
        inode.invoke1("removeAttribute", name);
    }

    protected String getSpecifiedAttribute(String name) {
        IDispatch r = (IDispatch) inode.invoke1("getAttributeNode", name);
        if (r == null)
            return null;
        Boolean b = (Boolean) Helper.get(r, "specified");
        if (!b.booleanValue())
            return null;
        return (String) Helper.get(r, "value");
    }

    public Attr getAttributeNode(String name) {
        IDispatch r = (IDispatch) inode.invoke1("getAttributeNode", name);
        if (r == null)
            return null;
        return (Attr) newNode(r, Node.ATTRIBUTE_NODE);
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        if (!(newAttr instanceof AttrImpl)) {
            Helper.notSupported();
        }
        AttrImpl a = (AttrImpl) newAttr;
        IDispatch r = (IDispatch) inode.invoke1("setAttributeNode", a.getINode());
        if (r == null)
            return null;
        return (Attr) newNode(r, Node.ATTRIBUTE_NODE);
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (!(oldAttr instanceof AttrImpl)) {
            Helper.notSupported();
        }
        AttrImpl a = (AttrImpl) oldAttr;
        IDispatch r = (IDispatch) inode.invoke1("removeAttributeNode", a.getINode());
        if (r == null)
            return null;
        return (Attr) newNode(r, Node.ATTRIBUTE_NODE);
    }

    public NodeList getElementsByTagName(String name) {
        IDispatch r = (IDispatch) inode.invoke1("getElementsByTagName", name);
        if (r == null)
            return null;
        return new NodeListImpl(this, r);
    }

    private NamedNodeMapImpl cachedAttributes;

    private void initCache() {
        if (cachedAttributes != null)
            return;
        cachedAttributes = (NamedNodeMapImpl) getAttributes();
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        initCache();
        Attr a = (Attr) cachedAttributes.getNamedItemNS(namespaceURI, localName);
        if (a == null)
            return null;
        return a.getValue();
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        initCache();
        Attr a = (Attr) cachedAttributes.getNamedItemNS(namespaceURI, localName);
        if (a == null)
            return;
        removeAttributeNode(a);
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        initCache();
        Attr a = (Attr) cachedAttributes.getNamedItemNS(namespaceURI, localName);
        if (a == null)
            return null;
        return a;
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        throw new UnsupportedOperationException();
    }

    public boolean hasAttribute(String name) {
        String val = getAttribute(name);
        if (val == null)
            return false;
        return true;
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) {
        String val = getAttributeNS(namespaceURI, localName);
        if (val == null)
            return false;
        return true;
    }

    public TypeInfo getSchemaTypeInfo() {
        throw new UnsupportedOperationException();
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    // --------------------------------------------------------------------------------
    // INodeEx interface
    // --------------------------------------------------------------------------------

    public boolean doClick() {
        try {
            if ("OPTION".equals(getLocalName())) {
                Helper.put(inode, "selected", Boolean.valueOf(true));
                return true;
            } else if ("SELECT".equals(getLocalName())) {
                return false;
            }
            inode.invoke0("click");
            return true;
        } catch (DispatchException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String originalBorder;

    public boolean highlight() {
        if (DomByCom.BORDER_MODE == DomByCom.STYLE_BORDER) {
            String className = (String) Helper.get(inode, "className");
            if (className == null)
                return false;
            className += " CSStoHighlight";
            Helper.put(inode, "className", className);
        } else if (DomByCom.BORDER_MODE == DomByCom.DIV_BORDER) {
            Element div = getOwnerDocument().getElementById(DomByCom.ID_DIV);
            IStyle style = ((IElementEx) div).getStyle();

            Rectangle r = Helper.getLocation(inode);

            String compatMode = ((IDocumentEx) getOwnerDocument()).getCompatMode();
            int w = 0;
            if (compatMode.equals(IDocumentEx.CSS1_COMPAT)) {
                w = 0;
            } else if (compatMode.equals(IDocumentEx.BACK_COMPAT)) {
                w = DomByCom.DIV_BORDER_WIDTH * 2;
            }

            style.put("left", (r.x - DomByCom.DIV_BORDER_WIDTH) + "px");
            style.put("top", (r.y - DomByCom.DIV_BORDER_WIDTH) + "px");
            style.put("width", (r.width + w) + "px");
            style.put("height", (r.height + w) + "px");
        } else if (DomByCom.BORDER_MODE == DomByCom.STYLE_BORDER2) {
            IDispatch style = (IDispatch) Helper.get(inode, "style");
            if (style == null)
                return false;
            originalBorder = (String) Helper.get(style, "cssText");
            String border = originalBorder + DomByCom.BORDER_STYLE_STRING;
            Helper.put(style, "cssText", border);
        }

        try {
            inode.invoke0("scrollIntoView");
            inode.invoke0("onmouseover");
            return true;
        } catch (DispatchException e) {
        }
        return false;
    }

    public boolean unhighlight() {
        if (DomByCom.BORDER_MODE == DomByCom.STYLE_BORDER) {
            String className = (String) Helper.get(inode, "className");
            if (className == null)
                return false;
            String newClassName = className.replaceAll(" CSStoHighlight", "");
            Helper.put(inode, "className", newClassName);
        } else if (DomByCom.BORDER_MODE == DomByCom.DIV_BORDER) {
            Element div = getOwnerDocument().getElementById(DomByCom.ID_DIV);
            IStyle style = ((IElementEx) div).getStyle();
            style.put("left", "-1000px");
            style.put("top", "-1000px");
            style.put("width", "100px");
            style.put("height", "100px");
        } else if (DomByCom.BORDER_MODE == DomByCom.STYLE_BORDER2) {
            IDispatch style = (IDispatch) Helper.get(inode, "style");
            if (style == null)
                return false;
            Helper.put(style, "cssText", originalBorder);
        }

        try {
            inode.invoke0("onmouseout");
        } catch (DispatchException e) {
        }
        return false;
    }

    static class InputType {
        public enum InputClass {
            EDIT, BUTTON, RADIO, CHECKBOX, COMBOBOX, HIDDEN, PASSWORD, FILE
        }

        private final InputClass inputClass;

        private final String uiString;

        String getUIString() {
            return uiString;
        }

        boolean isEdit() {
            return (inputClass == InputClass.EDIT);
        }

        boolean isFileEdit() {
            return (inputClass == InputClass.FILE);
        }

        boolean isButton() {
            return (inputClass == InputClass.BUTTON);
        }

        boolean isRadio() {
            return (inputClass == InputClass.RADIO);
        }

        boolean isCheckBox() {
            return (inputClass == InputClass.CHECKBOX);
        }

        boolean isComboBox() {
            return (inputClass == InputClass.COMBOBOX);
        }

        boolean isHidden() {
            return (inputClass == InputClass.HIDDEN);
        }

        boolean isPassword() {
            return (inputClass == InputClass.PASSWORD);
        }

        boolean isClickable() {
            return (isButton() || isRadio() || isCheckBox() || isComboBox());
        }

        final String htmlType;

        InputType(String uiString, String htmlType, InputClass inputClass) {
            this.uiString = uiString;
            this.htmlType = htmlType;
            this.inputClass = inputClass;
        }

    }

    private static final InputType[] HTMLINPUTCLASSES = new InputType[] {
            new InputType("edit", "text", InputType.InputClass.EDIT),
            new InputType("password edit", "password", InputType.InputClass.PASSWORD),
            new InputType("radio button", "radio", InputType.InputClass.RADIO),
            new InputType("checkbox", "checkbox", InputType.InputClass.CHECKBOX),
            new InputType("file upload edit", "file", InputType.InputClass.FILE),
            new InputType("", "hidden", InputType.InputClass.HIDDEN),
            new InputType("button", "submit", InputType.InputClass.BUTTON),
            new InputType("reset button", "reset", InputType.InputClass.BUTTON),
            new InputType("button", "button", InputType.InputClass.BUTTON),
            new InputType("button", "image", InputType.InputClass.BUTTON) };

    InputType getInputType() {
        Object o = Helper.get(inode, "type");
        if (!(o instanceof String))
            return null;
        String type = (String) o;
        for (int i = 0; i < HTMLINPUTCLASSES.length; i++) {
            if (type.equals(HTMLINPUTCLASSES[i].htmlType)) {
                return HTMLINPUTCLASSES[i];
            }
        }
        return null;
    }

    private String getInputValue() {
        return (String) Helper.get(inode, "value");
    }

    private String modifySrcStr(String src) {
        int st = src.lastIndexOf('/');
        int end = src.lastIndexOf('?');
        if (end == -1)
            end = src.length();
        if (st < 0) {
            if (end < 0)
                return src;
            st = 0;
        } else {
            st++;
        }
        if (end <= st) {
            return src.substring(st);
        } else {
            return src.substring(st, end);
        }
    }

    public String extractString() {
        String name = getLocalName();
        if ("IMG".equals(name)) {

            String alt = getSpecifiedAttribute("alt");
            if (alt != null) {
                alt = Helper.trimHTMLStr(alt);
            }
            String title = getSpecifiedAttribute("title");
            if (title != null) { 
                title = Helper.trimHTMLStr(title);
                if (alt == null || alt.length() == 0)
                    alt = title;
            }
            
            String src = getAttribute("src");
            if (src != null) {
                src = modifySrcStr(src);
            }
            
            boolean noAlt = (alt == null);
            boolean nullAlt = ((alt != null) && (alt.length() == 0));
            boolean isLink = Vocabulary.isLink().eval(this);

            if (isLink) {
                if (noAlt) {
                    if (Vocabulary.isReadNoAltImageLink()) {
                        return src;
                    } else {
                        return "";
                    }
                } else if (nullAlt && Vocabulary.isReadNullAltImageLink()) {
                    if (false) {
                    	if (anyTextInParentLink())
                        	return alt;
                    }
                    return src;
                }
                return alt;
            } else {
                if (noAlt) {
                    if (Vocabulary.isReadNoAltImage()) {
                        return src;
                    } else {
                        return "";
                    }
                } else if (nullAlt && Vocabulary.isReadNullAltImage()) {
                    return src;
                }
                return alt;
            }
        } else if (Vocabulary.isEmbeddedObject().eval(this)) {
            return "(Embedded Object)";
        } else if ("INPUT".equals(name)) {
            InputType type = getInputType();
            if (type != null) {
                if (type.isHidden())// || type.isCheckBox() || type.isRadio())
                    return "";
                String alt = getAttribute("alt");
                if (alt != null) {
                    alt = Helper.trimHTMLStr(alt);
                    if (alt.length() > 0)
                        return alt;
                }
                String value = getInputValue();
                if (type.isPassword())
                    value = value.replaceAll(".", "*");
                if (value != null && value.length() > 0) {
                    return value;
                }
                String title = getAttribute("title");
                if (title != null)
                    return title;

            }
        } else if ("SELECT".equals(name)) {
            return "Select";
        } else if ("AREA".equals(name)) {
            String alt = getAttribute("alt");
            if (alt != null) {
                alt = Helper.trimHTMLStr(alt);
                if (alt.length() > 0)
                    return alt;
            }
        }

        if (false) {
            // title attribute can be added to any element accoding to XHTML2.
            String title = getSpecifiedAttribute("title");
            if (title != null) { 
                title = Helper.trimHTMLStr(title);
                return title;
            }
        }

        return "";
    }

    private boolean anyTextInParentLink() {
        Node current = this.getParentNode();
        if (current == null)
            return false;
        while(current != null) {
            if ("A".equals(current.getNodeName())) {
                break;
            }
            current = current.getParentNode();
        }
        if (current == null)
            return false;

        return anyTextInSiblings(current.getFirstChild());
    }
    
    private boolean anyTextInSiblings(Node element) {
        StringBuffer buff = new StringBuffer();
        Node next = element;
        while (next != null) {
            if (next instanceof ImageElementImpl) {
                next = next.getNextSibling();
                continue;
            }
            if (next instanceof Element) {
                if (anyTextInSiblings(next.getFirstChild())) {
                    return true;
                }
            }
            if (next instanceof INodeEx) {
                INodeEx nex = (INodeEx) next;
                buff.append(nex.extractString());
            }
            next = next.getNextSibling();
        }
        
        return Helper.trimHTMLStr(buff.toString()).length() > 0;
    }

    @Override
    public short getHeadingLevel() {
        String name = getLocalName();
        if (name.length() != 2)
            return super.getHeadingLevel();
        if (name.charAt(0) != 'H')
            return super.getHeadingLevel();
        switch (name.charAt(1)) {
        case '1':
            return 1;
        case '2':
            return (short) 2;
        case '3':
            return (short) 3;
        case '4':
            return (short) 4;
        case '5':
            return (short) 5;
        case '6':
            return (short) 6;
        }
        return super.getHeadingLevel();
    }

    public boolean setFocus() {
        try {
            inode.invoke0("focus");
            return true;
        } catch (DispatchException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setTextContent(String text) {
        try {
            Helper.put(inode, "innerText", text);
        } catch (DispatchException e) {
            e.printStackTrace();
        }
    }

    public IStyle getStyle() {
        IDispatch style = (IDispatch) Helper.get(inode, "currentStyle");
        return (IStyle) new StyleImpl(style);
    }

    public AbstractTerms getTerms() {
        return HTMLTerms.getInstance();
    }

    public Rectangle getLocation() {
        try {
            return Helper.getLocation(inode);
        } catch (Exception e) {
            //System.out.println("getLocation error "+this.extractString());
        }
        return null;
    }

    public Position getRadioPosition() {
        if (getInputType() == null)
            return null;
        if (!getInputType().isRadio())
            return null;

        Element form = getParentElement(new String[] { "FORM" });
        if (form == null)
            return null;

        String radioName = (String) Helper.get(inode, "name");

        Position p = new Position();
        searchRadioPosition(form, radioName, p, false);
        return p;
    }

    private void searchRadioPosition(Element parent, String radioName, Position p, boolean found) {
        NodeList list = parent.getChildNodes();
        int len = list.getLength();
        for (int i = 0; i < len; i++) {
            Node n = list.item(i);
            if (n instanceof ElementImpl) {
                ElementImpl e = (ElementImpl) n;
                if (e.hasChildNodes()) {
                    searchRadioPosition(e, radioName, p, found);
                }
                if (e.getInputType() == null)
                    continue;
                if (!e.getInputType().isRadio())
                    continue;

                String name = (String) Helper.get(e.getINode(), "name");
                if (radioName.equals(name)) {
                    if (!found)
                        p.index++;
                    p.total++;
                }
                if (e.isSameNode(this)) {
                    found = true;
                }
            }
        }
    }

    public Position getListPosition() {
        Element list = getParentElement(new String[] { "UL", "OL", "DL" });
        Element item = getParentElement(new String[] { "LI", "DT" });
        if (list == null)
            return null;
        Position p = new Position();
        searchListPosition(list, item, p, false);
        return p;
    }

    private void searchListPosition(Element parent, Element item, Position p, boolean found) {
        NodeList list = parent.getChildNodes();
        int len = list.getLength();
        for (int i = 0; i < len; i++) {
            Node n = list.item(i);
            if (n instanceof ElementImpl) {
                ElementImpl e = (ElementImpl) n;
                String name = e.getNodeName();
                if (e.hasChildNodes() 
                        && !("UL".equals(name) || "OL".equals(name) || "DL".equals(name))) {
                    searchListPosition(e, item, p, found);
                }
                if (!"LI".equals(e.getNodeName()) && !"DT".equals(e.getNodeName()))
                    continue;

                if (!found)
                    p.index++;
                p.total++;
                if (e.isSameNode(item)) {
                    found = true;
                }
            }
        }
    }

    private Element getParentElement(String[] tagNames) {
        Element parent = this;
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < tagNames.length; i++) {
            set.add(tagNames[i].intern());
        }
        String name = parent.getNodeName();
        while (!set.contains(name.intern())) {
            Node temp = parent.getParentNode();
            if (temp == null || !(temp instanceof Element))
                return null;
            parent = (Element) temp;
            name = parent.getNodeName();
        } 

        return parent;
    }

    public Element getFormLabel() {
        NodeList nl = getOwnerDocument().getElementsByTagName("LABEL");
        Object myId = Helper.get(inode, "id");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof ElementImpl) {
                ElementImpl label = (ElementImpl) node;
                String id = (String) Helper.get(label.inode, "htmlFor");

                if (id == null)
                    continue;
                if (id.equals(myId))
                    return label;
            }
        }
        return null;
    }

    public char getAccessKey() {
        String key = null;

        String name = getNodeName();
        if ("LABEL".equals(name))
            return 0;

        Element label = getFormLabel();
        if (label != null && label instanceof ElementImpl) {
            key = (String) Helper.get(((ElementImpl) label).inode, "accessKey");
        } else {
            key = (String) Helper.get(inode, "accessKey");
        }

        if (key != null && key.length() == 1)
            return key.toUpperCase().charAt(0);

        return 0;
    }

    @Override
    public AnalyzedResult analyze(AnalyzedResult ar) {
        char key = getAccessKey();
        if (key != 0)
            ar.addAccessKey(this);

        return super.analyze(ar);
    }

}

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

package org.eclipse.actf.model.dom.dombycom;

import org.eclipse.actf.ai.comclutch.win32.ComPlugin;
import org.eclipse.actf.ai.comclutch.win32.ComService;
import org.eclipse.actf.ai.comclutch.win32.IDispatch;
import org.eclipse.actf.ai.comclutch.win32.ResourceManager;
import org.eclipse.actf.model.dom.dombycom.dom.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.dom.StyleSheetImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class DomByCom {
    private ResourceManager resourceManager;

    private final IDispatch iWebBrowser2;

    public static final String ID_DIV = "__org_eclipse_actf_highlight_div__";

    public static final int DIV_BORDER_WIDTH = 3;

    public static final int STYLE_BORDER = 0;

    public static final int DIV_BORDER = 1;
    
    public static final int STYLE_BORDER2 = 2;

    public static final int BORDER_MODE = STYLE_BORDER2;
    
    public static final String BORDER_STYLE_STRING = "border: 3px solid #071; background: #fdd;";

    private void addStyles(DocumentImpl doc) {
        StyleSheetImpl style = doc.createStyleSheet();
        if (style != null) {
            style.addRule(".CSStoHighlight", BORDER_STYLE_STRING);
        }
    }

    private void addDivs(DocumentImpl doc) {
        Element el = doc.getElementById(ID_DIV);
        if (el == null) {
            el = doc.createElement("div");
            el.setAttribute("id", ID_DIV);

            NodeList body = doc.getElementsByTagName("body");
            if (body != null && body.getLength() > 0) {
                System.out.println("insert");
                body.item(0).appendChild(el);

                try {
                    IElementEx elx = (IElementEx) el;
                    IStyle st = elx.getStyle();

                    st.put("position", "absolute");
                    st.put("backgroundColor", "transparent");
                    st.put("border", DIV_BORDER_WIDTH + "px solid green");
                    st.put("zIndex", "10000");
                    st.put("left", "-1000px");
                    st.put("top", "-1000px");
                    st.put("width", "100px");
                    st.put("height", "100px");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialize(DocumentImpl doc) {
        if (BORDER_MODE == STYLE_BORDER) {
            addStyles(doc);
        } else if (BORDER_MODE == DIV_BORDER) {
            addDivs(doc);
        } else if (BORDER_MODE == STYLE_BORDER2) {
            // do nothing;
        }
    }

    public void release() {
        resourceManager.releaseAll(iWebBrowser2);
    }

    public IDocumentEx getDocument() {
        IDispatch iDocument = (IDispatch) iWebBrowser2.get("Document");
        DocumentImpl doc = new DocumentImpl(iDocument);
        initialize(doc);
        return doc;
    }

    public DomByCom(long iWebBrowser2) {
        this.resourceManager = ComPlugin.getDefault().newResourceManager(null);
        this.iWebBrowser2 = ComService.newIDispatch(resourceManager, iWebBrowser2, true);
    }
}

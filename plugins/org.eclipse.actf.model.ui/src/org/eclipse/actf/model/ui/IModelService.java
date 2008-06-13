/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Node;



public interface IModelService {

    // TODO
    public static final String[] MIMETYPES_HTML = { "text/html", "application/xhtml+xml" };

    public static final String[] MIMETYPES_ODF = { "application/vnd.oasis.opendocument.presentation",
            "application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.spreadsheet" };

    public static final String[] EXTS_HTML = { "html", "htm", "xhtml", "mht" };

    public static final String[] EXTS_ODF = { "odt", "ods", "odp" };

    public static final String ATTR_WINDOWHANDLE = "WindowHandle";
    
    String[] getSupportMIMETypes();

    //TODO use editor extension info
    String[] getSupportExtensions();

    String getCurrentMIMEType();
    
    void open(String url);

    void open(File target);

    String getURL();
    
    String getTitle();
    
    String getID();

    //TODO support other model
    Document getDocument();

    Document getLiveDocument();
    
    Composite getTargetComposite();

    File saveOriginalDocument(String file);
    
    File saveDocumentAsHTMLFile(String file);

    void jumpToNode(Node target);

    IModelServiceScrollManager getScrollManager();

    ImagePositionInfo[] getAllImagePosition();
    
    IModelServiceHolder getModelServiceHolder();
    
    //for extension
    Object getAttribute(String name);
    // clearhighlight
    // size

}

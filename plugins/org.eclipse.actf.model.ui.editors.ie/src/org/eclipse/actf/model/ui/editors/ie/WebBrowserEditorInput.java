/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.editors.ie;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;



public class WebBrowserEditorInput implements IEditorInput {

    private String name = "Web Browser";

    private String url = "about:blank";

    public WebBrowserEditorInput(){
    }
    
    
    public WebBrowserEditorInput(String url) {
        setUrl(url);
    }

    public WebBrowserEditorInput(String url, String name) {
        setUrl(url);
        if (name != null) {
            this.name = name;
        }

    }

    public boolean exists() {
        System.out.println("exists");
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return name;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return "";
    }

    @SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
        return null;
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        if (url != null) {
            this.url = url;
        }
    }

}

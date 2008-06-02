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

package org.eclipse.actf.model.ui.editors.ie.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTFEventListener;
import org.eclipse.actf.model.ui.editors.ie.BrowserIE_Plugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;



public class WebBrowserEventExtension {
    private static final String TAG_LISTENER = "listener";
    // private static final String ATTR_ID = "id";
    private static final String ATTR_CLASS = "class";

    private static WebBrowserEventExtension[] cachedExtensions;

    public static WebBrowserEventExtension[] getExtensions() {
        if (cachedExtensions != null) return cachedExtensions;

        IExtension[] extensions = Platform.getExtensionRegistry()
            .getExtensionPoint(BrowserIE_Plugin.PLUGIN_ID, "WebBrowserEventListener")
            .getExtensions();
        List<WebBrowserEventExtension> l = new ArrayList<WebBrowserEventExtension>();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement[] configElements =
                extensions[i].getConfigurationElements();
            for (int j = 0; j < configElements.length; j++) {
                WebBrowserEventExtension ex = parseExtension(configElements[j], l.size());
                if (ex != null) l.add(ex);
            }
        }
        cachedExtensions = (WebBrowserEventExtension[]) l.toArray(new WebBrowserEventExtension[l.size()]);
        return cachedExtensions;
    }

    private static WebBrowserEventExtension parseExtension(IConfigurationElement configElement, int idx) {
        if (!configElement.getName().equals(TAG_LISTENER))
            return null;
        try {
            return new WebBrowserEventExtension(configElement, idx);
        } catch (Exception e) {
        }
        return null;
    }

    public static void disposeExtensions() {
        if (cachedExtensions == null) return;
        for (int i = 0; i < cachedExtensions.length; i++) {
            cachedExtensions[i].dispose();
        }
        cachedExtensions = null;
    }

    public static void navigateComplete(IWebBrowserACTF iWebBrowser, String url) {
        WebBrowserEventExtension[] exs = getExtensions();
        if (exs == null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().navigateComplete(iWebBrowser, url);
        }
    }

    public static void titleChange(IWebBrowserACTF iWebBrowser, String title) {
        WebBrowserEventExtension[] exs = getExtensions();
        if (exs == null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().titleChange(iWebBrowser, title);
        }
    }
    
    public static void progressChange(IWebBrowserACTF iWebBrowser, int progress, int progressMax){
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs == null) return;
        for (int i=0; i < exs.length; i++){
            exs[i].getListener().progressChange(iWebBrowser, progress, progressMax);
        }
    }
    
    public static void myDocumentComplete(IWebBrowserACTF iWebBrowser){
        WebBrowserEventExtension[] exs = getExtensions();
        
        //System.out.println("myDocumentComplete");
        
        if (exs == null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().myDocumentComplete(iWebBrowser);
        }        
    }
    
    public static void focusChange(IWebBrowserACTF iWebBrowser){
        WebBrowserEventExtension[] exs = getExtensions();
        if (exs == null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().focusChange(iWebBrowser);
        }        
    }
    
    public static void browserDisposed(IWebBrowserACTF iWebBrowser, String title){
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().browserDisposed(iWebBrowser, title);
        }        
        //System.out.println("close:"+iWebBrowser);
    }
    
    public static void beforeNavigate(IWebBrowserACTF iWebBrowser, String url, String targetFrameName, boolean isInNavigation){
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().beforeNavigate(iWebBrowser, url, targetFrameName, isInNavigation);
        }        
    }
    
    public static void myRefresh(IWebBrowserACTF webBrowser) {
        WebBrowserEventExtension[] exs = getExtensions();
        
        //System.out.println("myRefresh");
        
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().myRefresh(webBrowser);
        }                
    }

    public static void myRefreshComplete(IWebBrowserACTF webBrowser) {
        WebBrowserEventExtension[] exs = getExtensions();

        //System.out.println("myRefreshComplete");
        
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().myRefreshComplete(webBrowser);
        }                        
    }

    public static void navigateStop(IWebBrowserACTF webBrowser) {
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().navigateStop(webBrowser);
        }                        
    }
    
    public static void focusGainedOfAddressText(IWebBrowserACTF webBrowser) {
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().focusGainedOfAddressText(webBrowser);
        }                        
    }
    
    public static void focusLostOfAddressText(IWebBrowserACTF webBrowser) {
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().focusLostOfAddressText(webBrowser);
        }                        
    }
    
    public static void newWindow(IWebBrowserACTF webBrowser){
        WebBrowserEventExtension[] exs = getExtensions();
        if(exs ==null) return;
        for (int i = 0; i < exs.length; i++) {
            exs[i].getListener().newWindow(webBrowser);
        }                                
    }
    

    private final IConfigurationElement configElement;
    private IWebBrowserACTFEventListener listener;

    private WebBrowserEventExtension(IConfigurationElement configElement, int idx) {
        this.configElement = configElement;
    }

    private IWebBrowserACTFEventListener getListener() {
        if (listener != null) return listener;
        try {
            listener = (IWebBrowserACTFEventListener) configElement.createExecutableExtension(ATTR_CLASS);
        } catch (Exception e) {
        }
        return listener;
    }

    public void dispose() {
        if (listener == null) return;
        listener.dispose();
        listener = null;
    }
}

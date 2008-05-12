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

package org.eclipse.actf.model.ui.editor.browser;




public interface IWebBrowserACTFEventListener {
    void navigateComplete(IWebBrowserACTF webBrowser, String url);
    void titleChange(IWebBrowserACTF webBrowser, String title);
    void progressChange(IWebBrowserACTF webBrowser, int progress, int progressMax);
    void myDocumentComplete(IWebBrowserACTF webBrowser);
    void dispose();
    void focusChange(IWebBrowserACTF webBrowser);
    void browserDisposed(IWebBrowserACTF webBrowser, String title);
    void beforeNavigate(IWebBrowserACTF webBrowser, String url, String targetFrameName, boolean isInNavigation);
    void myRefresh(IWebBrowserACTF webBrowser);
    void myRefreshComplete(IWebBrowserACTF webBrowser);
    void navigateStop(IWebBrowserACTF webBrowser);
    void focusGainedOfAddressText(IWebBrowserACTF webBrowser);
    void focusLostOfAddressText(IWebBrowserACTF webBrowser);
    void newWindow(IWebBrowserACTF webBrowser);
}
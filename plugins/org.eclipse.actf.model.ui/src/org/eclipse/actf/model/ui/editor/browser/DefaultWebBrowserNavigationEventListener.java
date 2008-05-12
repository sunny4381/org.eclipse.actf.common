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

package org.eclipse.actf.model.ui.editor.browser;




public class DefaultWebBrowserNavigationEventListener implements IWebBrowserNavigationEventListener {

    public void goBack(WebBrowserNavigationEvent e) {
        IWebBrowserACTF browser = e.getBrowser();
        if(browser !=null){
            browser.goBackward();
        }
    }

    public void goForward(WebBrowserNavigationEvent e) {
        IWebBrowserACTF browser = e.getBrowser();
        if(browser !=null){
            browser.goForward();
        }
    }

    public void refresh(WebBrowserNavigationEvent e) {
        IWebBrowserACTF browser = e.getBrowser();
        if(browser !=null){
            browser.navigateRefresh();
        }
    }

    public void stop(WebBrowserNavigationEvent e) {
        IWebBrowserACTF browser = e.getBrowser();
        if(browser !=null){
            browser.navigateStop();
        }
    }

}

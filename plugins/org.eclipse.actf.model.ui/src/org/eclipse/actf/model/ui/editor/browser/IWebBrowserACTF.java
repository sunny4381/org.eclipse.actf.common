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
package org.eclipse.actf.model.ui.editor.browser;

import org.eclipse.actf.model.ui.IModelService;


public interface IWebBrowserACTF extends IModelService {
	
    public static final int READYSTATE_UNINITIALIZED = 0;

    public static final int READYSTATE_LOADING = 1;

    public static final int READYSTATE_LOADED = 2;

    public static final int READYSTATE_INTERACTIVE = 3;

    public static final int READYSTATE_COMPLETE = 4;

	
    public class WebBrowserNavigationEventListnerHolder {
        public static IWebBrowserNavigationEventListener LISTENER = null;
    }
    
	public abstract void setFocusAddressText(boolean selectAll);
    void showAddressText(boolean flag);

	/*
	 * browse commands
	 */
	public abstract void navigate(String url);

	public abstract void goBackward();

	public abstract void goForward();

	public abstract void navigateStop();

	public abstract void navigateRefresh();

	/*
	 * navigation result
	 */

	public abstract int getReadyState();

	public abstract boolean isReady();

	public abstract String getLocationName();

	public abstract boolean isUrlExists();

	public abstract int getNavigateErrorCode();

	/*
	 * browser property
	 */
	public abstract void setHlinkStop(boolean bStop);

	public abstract void setWebBrowserSilent(boolean bSilent);

	public abstract void setDisableScriptDebugger(boolean bDisable);

	public abstract void setDisplayImage(boolean display);

	public abstract boolean isDisableScriptDebugger();

	/*
	 * highligt element
	 * 
	 */
	public abstract void highlightElementById(String id);

	public abstract void hightlightElementByAttribute(String name, String value);

	public abstract void recoveryHighlight();

	/*
	 * font size, color, etc.
	 * 
	 */
	public abstract void setFontSize(int fontSize);

	public abstract int getFontSize();

	public abstract IWebBrowserStyleInfo getStyleInfo();
	
	int getBrowserAddress();
        
}

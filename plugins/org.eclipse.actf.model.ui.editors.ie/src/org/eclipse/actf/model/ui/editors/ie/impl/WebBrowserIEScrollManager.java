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

package org.eclipse.actf.model.ui.editors.ie.impl;

import org.eclipse.actf.model.ui.IModelServiceScrollManager;
import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editors.ie.editor.WebBrowserIEImpl;



public class WebBrowserIEScrollManager implements IModelServiceScrollManager {

    int scrollBarWidth;
    WebBrowserIEImpl browserIE;

    public WebBrowserIEScrollManager(WebBrowserIEImpl browserIE) {
        this.browserIE = browserIE;
    }
    
    //do not have to wait rendering    
    
    public void absoluteCoordinateScroll(int y, boolean waitRendering) {
        browserIE.scrollY(y);
    }

    public void absoluteCoordinateScroll(int x, int y, boolean waitRendering) {
        browserIE.scrollTo(x, y);
    }

    public int decrementScrollX(boolean waitRendering) {
        return -1;
    }

    public int decrementScrollY(boolean waitRendering) {
        return -1;
    }

    public int incrementScrollX(boolean waitRendering) {
        return -1;
    }

    public int incrementScrollY(boolean waitRendering) {
        return -1;
    }

    public int decrementLargeScrollX(boolean waitRendering) {
        return -1;
    }

    public int decrementLargeScrollY(boolean waitRendering) {
        return -1;
    }

    public int incrementLargeScrollX(boolean waitRendering) {
        return -1;
    }

    public int incrementLargeScrollY(boolean waitRendering) {
        return -1;
    }

    public int decrementPageScroll(boolean waitRendering) {
        return -1;
    }

    public int incrementPageScroll(boolean waitRendering) {
        return -1;
    }            
    
    public int jumpToPage(int pageNumber, boolean waitRendering) {
        return -1;
    }

    public int getCurrentPageNumber() {
        return -1;
    }

    public int getLastPageNumber() {
        return -1;
    }
    
    public int getScrollType() {
        return ABSOLUTE_COORDINATE;
    }

    public ModelServiceSizeInfo getSize(boolean isWhole) {
        return browserIE.getBrowserSize(isWhole);
    }

    public void setScrollBarWidth(int width) {
        scrollBarWidth = width;
    }

}

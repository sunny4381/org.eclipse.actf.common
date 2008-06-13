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

package org.eclipse.actf.model.ui;




public interface IModelServiceScrollManager {
    static int NONE = -1;
    static int ABSOLUTE_COORDINATE = 0;
    static int INCREMENTAL = 1;
    static int PAGE = 2;
    
    int getScrollType();
    
    void absoluteCoordinateScroll(int y, boolean waitRendering);
    void absoluteCoordinateScroll(int x, int y, boolean waitRendering);
    
    int incrementScrollX(boolean waitRendering);
    int decrementScrollX(boolean waitRendering);
    int incrementScrollY(boolean waitRendering);
    int decrementScrollY(boolean waitRendering);
    int incrementLargeScrollX(boolean waitRendering);
    int decrementLargeScrollX(boolean waitRendering);
    int incrementLargeScrollY(boolean waitRendering);
    int decrementLargeScrollY(boolean waitRendering);
    
    int incrementPageScroll(boolean waitRendering);
    int decrementPageScroll(boolean waitRendering);
    
    int jumpToPage(int pageNumber, boolean waitRendering);
    int getCurrentPageNumber();
    int getLastPageNumber();
    
    ModelServiceSizeInfo getSize(boolean isWhole);
}

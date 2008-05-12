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



public class ModelServiceSizeInfo {

    int viewSizeX;

    int viewSizeY;

    int wholeSizeX;

    int wholeSizeY;

    public ModelServiceSizeInfo(int viewSizeX, int viewSizeY, int wholeSizeX, int wholeSizeY) {
        this.viewSizeX = viewSizeX;
        this.viewSizeY = viewSizeY;
        this.wholeSizeX = wholeSizeX;
        this.wholeSizeY = wholeSizeY;
    }

    public int getViewSizeX() {
        return viewSizeX;
    }

    public int getViewSizeY() {
        return viewSizeY;
    }

    public int getWholeSizeX() {
        return wholeSizeX;
    }

    public int getWholeSizeY() {
        return wholeSizeY;
    }

    public int[] toArray() {
        return new int[] { viewSizeX, viewSizeY, wholeSizeX, wholeSizeY };
    }

}

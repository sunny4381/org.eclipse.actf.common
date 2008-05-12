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

import java.util.HashMap;

import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.swt.graphics.RGB;



public class BrowserAndStyleInfo {
    private HashMap nodeStyles;

    private ModelServiceSizeInfo size;
    
    private String unvisited;

    private String visited;

    
    
    /**
     * @param nodeStyles
     * @param rgb2
     * @param rgb
     * @param pageSize
     */
    public BrowserAndStyleInfo(ModelServiceSizeInfo size, HashMap nodeStyles, RGB unvisited, RGB visited) {
        this.size = size;
        this.unvisited = parseRGB(unvisited);
        this.visited = parseRGB(visited);
        this.nodeStyles = nodeStyles;
    }

    /**
     * @return Returns the nodeStyles.
     */
    public HashMap getNodeStyles() {
        return this.nodeStyles;
    }

    /**
     * @return Returns the pageSizeX.
     */
    public int getPageSizeX() {
        return size.getWholeSizeX();
    }

    /**
     * @return Returns the pageSizeY.
     */
    public int getPageSizeY() {
        return size.getWholeSizeY();
    }

    /**
     * @return Returns the screenSizeX.
     */
    public int getScreenSizeX() {
        return size.getViewSizeX();
    }

    /**
     * @return Returns the screenSizeY.
     */
    public int getScreenSizeY() {
        return size.getViewSizeY();
    }

    /**
     * @return Returns the unvisited.
     */
    public String getUnvisited() {
        return unvisited;
    }

    /**
     * @return Returns the visited.
     */
    public String getVisited() {
        return visited;
    }

    /**
     * @param unvisited2
     * @return
     */
    private String parseRGB(RGB rgbColor) {
        String r = "0" + Integer.toHexString(rgbColor.red);
        String g = "0" + Integer.toHexString(rgbColor.green);
        String b = "0" + Integer.toHexString(rgbColor.blue);
        String color = r.substring(r.length() - 2) + g.substring(g.length() - 2) + b.substring(b.length() - 2);
        // System.out.println(color);
        return color;
    }
}

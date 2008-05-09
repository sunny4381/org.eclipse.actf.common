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

package org.eclipse.actf.model.dom.dombycom.impl;

import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.DOMException;



public class Helper {
    static public Object get(IDispatch idisp, String prop) {
        try {
            if (idisp == null) return null;
            return idisp.get(prop);
        } catch (DispatchException e) {
            //e.printStackTrace();
            return null;
        }
    }
    static public boolean put(IDispatch idisp, String prop, Object val) {
        try {
            idisp.put(prop, val);
            return true;
        } catch (DispatchException e) {
            e.printStackTrace();
            return false;
        }
    }

    static public boolean hasProperty(IDispatch idisp, String prop) {
        try {
            Object r = idisp.get(prop);
            if (r != null) return true;
        } catch (DispatchException e) {
        }
        return false;
    }

    static public String trimHTMLStr(String src) {
        src = src.trim();
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (c == '\u00a0') {
                // &nbsp;
            } else {
                ret.append(c);
            }
        }
        String r = ret.toString();
        // if (r.matches("\\A([\\uff5c\\u30fb|])\\z")) return "";
        return r;
    }

    static public void notSupported() throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR,
                               "The specified node must be created by DomByCom"); // $NON-NLS-1$
    }
    
    static public Rectangle getLocation(IDispatch idisp) {
        Rectangle r = new Rectangle(0, 0, 0, 0);
        getOffset(idisp, r);

        r.width = (Integer) Helper.get(idisp, "offsetWidth");
        r.height = (Integer) Helper.get(idisp, "offsetHeight");

        return r;
    }
    
    private static void getOffset(IDispatch inode, Rectangle r) {
        int ol = (Integer) Helper.get(inode, "offsetLeft");
        int ot = (Integer) Helper.get(inode, "offsetTop");

        r.x += ol;
        r.y += ot;

        IDispatch parent = (IDispatch) Helper.get(inode, "offsetParent");
        //System.out.println(r);
        if (parent == null)
            return;
        getOffset(parent, r);
    }
}

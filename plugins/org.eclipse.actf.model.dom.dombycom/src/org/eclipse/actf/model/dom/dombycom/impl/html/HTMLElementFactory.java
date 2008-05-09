/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.html;

import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.Element;




public class HTMLElementFactory {
    public static NodeImpl create(NodeImpl base, IDispatch inode, String tagName){
        try {
            if (tagName.toLowerCase().equals("framenode")) {
                return new FrameNodeImpl(base, inode);
            } else if(tagName.toLowerCase().equals("text")){
                return new TextImpl(base, inode);
            } else if(tagName.toLowerCase().equals("select")){
                return new SelectElementImpl(base, inode);
            } else if(tagName.toLowerCase().equals("img")){
                return new ImageElementImpl(base, inode);
            }
            return new ElementImpl(base, inode);
        } catch (DispatchException e) {
        }
        return null;
    }

    public static Element createElement(DocumentImpl base, IDispatch inode, String tagName) {
        try {
            IDispatch r;
            r = (IDispatch) inode.invoke1("createElement", tagName);
            if (r == null)
                return null;

            if (tagName.toLowerCase().equals("iframe")) {
                return new FrameNodeImpl(base, r);
            } 
            return new ElementImpl(base, r);
        } catch (DispatchException e) {
        }
        return null;
    }
}

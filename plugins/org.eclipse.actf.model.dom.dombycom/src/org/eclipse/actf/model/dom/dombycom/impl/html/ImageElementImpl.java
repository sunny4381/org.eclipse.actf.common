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

import org.eclipse.actf.model.dom.dombycom.IImageElement;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class ImageElementImpl extends ElementImpl implements IImageElement {

    public ImageElementImpl(NodeImpl baseNode, IDispatch inode) {
        super(baseNode, inode);
    }

    public boolean hasUsemap() {
        String usemap = (String) Helper.get(inode, "usemap");
        return (usemap != null && usemap.length() >= 2);
    }

    public Element getMap() {
        String usemap = (String) Helper.get(inode, "usemap");
        if (usemap == null)
            return null;

        String name;
        if (usemap.startsWith("#")) {
            name = usemap.substring(1);
        } else {
            name = usemap;
        }
        NodeList nl = getOwnerDocument().getElementsByTagName("MAP");
        for (int i = 0; i < nl.getLength(); i++){
            Node node = nl.item(i);
            if ("MAP".equals(node.getNodeName())) {
                Element area = (Element) node;
                if (name.equals(area.getAttribute("name")))
                    return area;
            }
        }
        return null;
    }
}

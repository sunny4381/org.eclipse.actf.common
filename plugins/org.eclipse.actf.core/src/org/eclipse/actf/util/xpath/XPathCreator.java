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

package org.eclipse.actf.util.xpath;

import org.w3c.dom.Node;


public class XPathCreator {
    public static String childPathSequence(Node target) {
        StringBuffer tmpSB = new StringBuffer();
        
        Node owner = null;
        if(target!=null){
            owner = target.getOwnerDocument();
        }
        while (target != null && target!=owner) {
            //short currentType = target.getNodeType();            
            String currentName = target.getNodeName();
            int count = countSiblingByName(target, currentName);

            if (currentName.equals("#text")){
                currentName = "text()";
            }
            
            if (count > 0) {
                count = count+1;
                tmpSB.insert(0,"/" + currentName + "[" + count + "]");
            } else {
                tmpSB.insert(0,"/" + currentName);
            }
            target = target.getParentNode();
        }
        
        return (tmpSB.toString());
    }

    private static int countSiblingByName(Node target, String name) {
        int count = 0;
        if (target != null) {
            target = target.getPreviousSibling();
        }
        while (target != null) {
            if (target.getNodeName().equals(name)) {
                count++;
            }
            target = target.getPreviousSibling();
        }
        return count;
    }

}

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

package org.eclipse.actf.util.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public abstract class XPathService {
    public abstract Object compile(String path);
    public abstract NodeList evalForNodeList(Object compiled, Node ctx);
    public abstract String evalForString(Object compiled, Node ctx);
    
    public NodeList evalPathForNodeList(String path, Node ctx) {
        Object compiled = compile(path);
        if (compiled == null) return null;
        return evalForNodeList(compiled, ctx);
    }

    public String evalPathForString(String path, Node ctx) {
        Object compiled = compile(path);
        if (compiled == null) return null;
        return evalForString(compiled, ctx);
    }
}

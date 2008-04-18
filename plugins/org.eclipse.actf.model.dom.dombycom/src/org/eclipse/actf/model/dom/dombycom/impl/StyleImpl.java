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

package org.eclipse.actf.model.dom.dombycom.impl;

import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.util.comclutch.win32.IDispatch;




public class StyleImpl implements IStyle {
    
    IDispatch style;
    
    public StyleImpl(IDispatch style){
        this.style = style;
    }

    public Object get(String name) {
        return Helper.get(style, name);
    }

    public boolean put(String name, String value) {
        return Helper.put(style, name, value);
    }

}

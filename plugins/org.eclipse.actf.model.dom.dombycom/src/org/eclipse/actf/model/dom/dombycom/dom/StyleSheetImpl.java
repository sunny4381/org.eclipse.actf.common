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

package org.eclipse.actf.model.dom.dombycom.dom;

import org.eclipse.actf.util.comclutch.win32.IDispatch;


public class StyleSheetImpl {
    private IDispatch idispatch;
    
    public StyleSheetImpl(IDispatch idispatch) {
        this.idispatch = idispatch;
    }

    public void addRule(String selector, String style) {
        idispatch.invoke("addRule", new Object[]{selector, style});
    }
}

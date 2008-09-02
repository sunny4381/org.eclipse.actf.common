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
package org.eclipse.actf.util.win32.comclutch;


public interface IDispatch extends IUnknown {
    void cacheDispIDs(String[] names);

    Object invoke(String method, Object[] args);
    Object invoke0(String method);
    Object invoke1(String method, Object arg1);

    Object get(String prop);
    void put(String prop, Object val);

    IDispatch newIDispatch(long ptr);
}

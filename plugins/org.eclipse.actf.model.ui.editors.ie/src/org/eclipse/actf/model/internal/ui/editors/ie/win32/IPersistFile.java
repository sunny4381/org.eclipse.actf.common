/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie.win32;

import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.IPersist;



public class IPersistFile extends IPersist {
    private int address;
    public IPersistFile(int address) {
        super(address);
        this.address = address;
    }
    public int IsDirty() {
        return COM.VtblCall(4, address);
    }
    public int Load(int pszFilename, int dwMode) {
        return COM.VtblCall(5, address, pszFilename, dwMode);
    }
    public int Save(int pszFilename, boolean fRemember) {
        return COM.VtblCall(6, address, pszFilename, fRemember);
    }
    public int SaveCompleted(int pszFilename) {
        return COM.VtblCall(7, address, pszFilename);
    }
    public int GetCurFile(int ppszFilename){
        return COM.VtblCall(9, address, ppszFilename);
    }
}

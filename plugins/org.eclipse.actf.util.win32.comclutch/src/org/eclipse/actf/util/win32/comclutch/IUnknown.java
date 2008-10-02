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

import java.util.UUID;

/**
 * Wrapper for IUnknown object
 * see http://msdn.microsoft.com/en-us/library/ms680509(VS.85).aspx
 */
public interface IUnknown extends IResource {
    UUID IID_IOleContainer = UUID.fromString("0000011b-0000-0000-C000-000000000046");
    UUID IID_IWebBrowser2 = UUID.fromString("D30C1661-CDAF-11D0-8A3E-00C04FC9E26E");
    UUID IID_IServiceProvider = UUID.fromString("6d5140c1-7436-11ce-8034-00aa006009fa");
    UUID IID_IHTMLElement = UUID.fromString("3050f1ff-98b5-11cf-bb82-00aa00bdce0b");
    UUID IID_IAccessible = UUID.fromString("618736E0-3C3D-11CF-810C-00AA00389B71");

    void addRef(long ptr);
    void release();
    IUnknown queryInterface(UUID iid);

    
    /**
	 * It will be called from native code
     */
    IUnknown newIUnknown(long ptr);
    
    /**
     * @return the total reference count
     */
    int getTotalRefCount();
}

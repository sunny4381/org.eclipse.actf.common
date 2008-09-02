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

public interface IUnknown extends IResource {
    UUID IID_IOleContainer = UUID.fromString("0000011b-0000-0000-C000-000000000046");
    UUID IID_IWebBrowser2 = UUID.fromString("D30C1661-CDAF-11D0-8A3E-00C04FC9E26E");
    UUID IID_IServiceProvider = UUID.fromString("6d5140c1-7436-11ce-8034-00aa006009fa");
    UUID IID_IHTMLElement = UUID.fromString("3050f1ff-98b5-11cf-bb82-00aa00bdce0b");
    UUID IID_IAccessible = UUID.fromString("618736E0-3C3D-11CF-810C-00AA00389B71");

    // UUID getGUID();
    void release();
    int getTotalRefCount();
    IUnknown queryInterface(UUID iid);

    IUnknown newIUnknown(long ptr);
    void addRef(long ptr);
}

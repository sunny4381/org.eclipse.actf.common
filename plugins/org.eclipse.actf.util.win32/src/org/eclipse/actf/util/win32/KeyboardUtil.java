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

package org.eclipse.actf.util.win32;

import org.eclipse.swt.internal.win32.OS;



public class KeyboardUtil {

    public static final int VK_CONTROL = OS.VK_CONTROL; // 0x11

    public static boolean IsKeyDown(int nVirtKey) {
        return OS.GetKeyState(nVirtKey) < 0;
    }

}

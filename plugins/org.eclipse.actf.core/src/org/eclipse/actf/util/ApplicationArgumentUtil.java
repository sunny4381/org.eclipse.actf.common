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

package org.eclipse.actf.util;

import org.eclipse.core.runtime.Platform;



public class ApplicationArgumentUtil {
    public static boolean isAvailable(String arg) {
        String[] args = Platform.getApplicationArgs();
        for (int i = 0; i < args.length; i++) {
            if (arg.equals(args[i])) return true;
        }
        return false;
    }
}

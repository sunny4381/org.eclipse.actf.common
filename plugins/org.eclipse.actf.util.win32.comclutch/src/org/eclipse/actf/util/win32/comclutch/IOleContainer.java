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

package org.eclipse.actf.util.win32.comclutch;


public interface IOleContainer extends IUnknown {
    public static final int OLECONTF_EMBEDDINGS    = 1; 
    public static final int OLECONTF_LINKS         = 2; 
    public static final int OLECONTF_OTHERS        = 4; 
    public static final int OLECONTF_ONLYUSER      = 8; 
    public static final int OLECONTF_ONLYIFRUNNING = 16; 

    IEnumUnknown enumObjects(int flags);
    boolean lockContainer(boolean lock);
}

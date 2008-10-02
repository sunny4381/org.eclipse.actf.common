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


/**
 * Wrapper for IUrlHistoryStg2 object
 * see http://msdn.microsoft.com/en-us/library/aa767716(VS.85).aspx
 */
public interface BrowserHistory {
    /**
     * @param url the URL to be checked
     * @return whether the URL is already visited or not 
     */
    boolean isVisited(String url);
}

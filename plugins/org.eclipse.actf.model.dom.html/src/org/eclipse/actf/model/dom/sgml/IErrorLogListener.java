/*******************************************************************************
 * Copyright (c) 1998, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Goh KONDOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.sgml;

/**
 *  Implementation class of this interface is added to {@link SGMLParser 
 * SGMLParser} instance by {@link 
 * SGMLParser#addErrorLogListener(ErrorLogListener) 
 * addErrorLogListener}.
 */
public interface IErrorLogListener {
    /**
     *  Records error messages.
     */
    public void errorLog(int errorCode, String msg);
}

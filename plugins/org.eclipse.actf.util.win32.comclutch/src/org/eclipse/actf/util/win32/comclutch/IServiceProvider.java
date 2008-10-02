/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch;

import java.util.UUID;

/**
 * Wrapper for IServiceProvider object
 * see http://msdn.microsoft.com/en-us/library/system.iserviceprovider.aspx
 */
public interface IServiceProvider extends IUnknown {
	IUnknown queryService(UUID guidService, UUID riid);
}

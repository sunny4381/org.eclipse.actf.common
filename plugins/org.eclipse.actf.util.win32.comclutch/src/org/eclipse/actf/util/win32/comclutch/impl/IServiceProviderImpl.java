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
package org.eclipse.actf.util.win32.comclutch.impl;

import java.util.UUID;

import org.eclipse.actf.util.win32.comclutch.IServiceProvider;
import org.eclipse.actf.util.win32.comclutch.IUnknown;

public class IServiceProviderImpl extends IUnknownImpl implements
		IServiceProvider {

	protected IServiceProviderImpl(IUnknownImpl base) {
		super(base);
	}

	public IUnknown queryService(UUID guidService, UUID riid) {
		//System.out.println("queryS\t"+getPtr());
		return _QueryService(getPtr(), guidService.getMostSignificantBits(), guidService
				.getLeastSignificantBits(), riid.getMostSignificantBits(), riid
				.getLeastSignificantBits());
	}

	private native IUnknown _QueryService(long ptr, long msb1, long lsb1, long msb2,
			long lsb2);

}

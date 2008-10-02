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

import org.eclipse.actf.util.win32.comclutch.impl.IUnknownImpl;

/**
 * @see RefContainer
 */
public class RefIUnknown extends RefContainer {
	public RefIUnknown(ResourceManager rm) {
		super(rm, SIZEOF_IUNKOWN);
	}

	public IUnknown getValue() {
		return new IUnknownImpl(getResourceManager(), _getValueByIUnknown(getPtr()), true);
	}

	public void setValue(IUnknown value) {
		_setValueForIUnknown(getPtr(), value.getPtr());
	}
}

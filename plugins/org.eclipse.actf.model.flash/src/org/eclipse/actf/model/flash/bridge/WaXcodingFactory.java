/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash.bridge;

import org.eclipse.actf.model.internal.flash.bridge.WaXcodingImpl;

/**
 * Factory class for {@link IWaXcoding}
 */
public class WaXcodingFactory {
	/**
	 * Create new {@link IWaXcoding} instance
	 * 
	 * @return new instance of {@link IWaXcoding}
	 */
	public static IWaXcoding getWaXcoding() {
		return WaXcodingImpl.getInstance();
	}

}

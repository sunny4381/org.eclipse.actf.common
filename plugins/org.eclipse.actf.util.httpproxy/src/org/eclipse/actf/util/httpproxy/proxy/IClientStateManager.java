/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.proxy;

/**
 * Interface to hold client state in {@link IHTTPProxyConnection}
 */
public interface IClientStateManager {

	// We should use read-write lock instead of mutex.
	/**
	 * Put client state as a set of key and value
	 * 
	 * @param stateKey
	 *            key of the state
	 * @param stateValue
	 *            value of the state
	 */
	public abstract void put(Object stateKey, Object stateValue);

	/**
	 * Get client state value
	 * 
	 * @param stateKey
	 *            key of the target state
	 * @return value of the target state
	 */
	public abstract Object get(Object stateKey);

}
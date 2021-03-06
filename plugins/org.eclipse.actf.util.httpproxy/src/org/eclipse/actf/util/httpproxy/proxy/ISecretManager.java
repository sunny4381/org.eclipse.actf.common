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
 * Utility class used for confirmation of correct server-client combination.
 */
public interface ISecretManager {

	/**
	 * Get secret String that is used for confirmation of correct server-client
	 * combination
	 * 
	 * @param id
	 *            target key to obtain the secret
	 * @param remove
	 *            if true, the secret will be removed after invoking this method
	 * @return secret, or null if the corresponding value is not available for
	 *         the target key
	 */
	public abstract String getSecret(String id, boolean remove);

	/**
	 * Request to generate secret
	 * 
	 * @return secret as array of byte
	 */
	public abstract byte[] requestSecret();

}
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
package org.eclipse.actf.util.httpproxy.proxy;

/**
 * Factory of HTTP local server objects.
 */
public interface IHTTPLocalServerFactory {
	/**
	 * Creates new instance of HTTP local server.
	 * 
	 * @return a HTTP local server object
	 * @see IHTTPLocalServer
	 */
	public IHTTPLocalServer newInstance();
}

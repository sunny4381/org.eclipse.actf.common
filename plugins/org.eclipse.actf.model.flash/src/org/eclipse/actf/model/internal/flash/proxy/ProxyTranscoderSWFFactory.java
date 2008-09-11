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
package org.eclipse.actf.model.internal.flash.proxy;

import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoder;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoderFactory;


public class ProxyTranscoderSWFFactory implements IHTTPProxyTranscoderFactory {

	public ProxyTranscoderSWFFactory() {
	}
	
	public IHTTPProxyTranscoder newInstance(int id) {
		return ProxyTranscoderSWF.newInstance(id);
	}

}

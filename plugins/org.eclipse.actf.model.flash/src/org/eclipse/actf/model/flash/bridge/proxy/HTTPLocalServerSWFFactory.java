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
package org.eclipse.actf.model.flash.bridge.proxy;

import java.io.InputStream;

import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServer;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServerFactory;

public class HTTPLocalServerSWFFactory implements IHTTPLocalServerFactory {

	public static void setBridgeInitSwf(InputStream is){
		HTTPLocalServerSWF.setBridgeInitSwf(is);
	}
	
	public HTTPLocalServerSWFFactory() {
	}
	
	public IHTTPLocalServer newInstance() {
		return new HTTPLocalServerSWF();
	}

}

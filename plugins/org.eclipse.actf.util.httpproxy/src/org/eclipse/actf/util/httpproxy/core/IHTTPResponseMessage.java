/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

public interface IHTTPResponseMessage extends IHTTPMessage{

	public static final byte[] EMPTY_BODY = new byte[0];

	public abstract String getStatusCodeAsString();

	public abstract byte[] getStatusCodeAsBytes();

	public abstract String getReasonPhraseAsString();

	public abstract byte[] getReasonPhraseAsBytes();

}
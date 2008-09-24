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

/**
 * A HTTP response message.
 */
public interface IHTTPResponseMessage extends IHTTPMessage {

	public static final byte[] EMPTY_BODY = new byte[0];

	/**
	 * Gets the status code of this response message.
	 * @return status code as a String
	 */
	public abstract String getStatusCodeAsString();

	/**
	 * Gets the status code of this response message.
	 * @return status code as an array of bytes
	 */
	public abstract byte[] getStatusCodeAsBytes();

	/**
	 * Gets the reason phrase of this response message.
	 * @return reason phrase as a String
	 */
	public abstract String getReasonPhraseAsString();

	/**
	 * Gets the reason phrase of this response message.
	 * @return reason phrase as an array of bytes
	 */
	public abstract byte[] getReasonPhraseAsBytes();

}
/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.actf.util.httpproxy.util.TimeoutException;

public interface IHTTPMessage {

	public static final int INIT_NUM_HEADERS = 50;
	public static final int DEFAULT_INITIAL_BUFFER_SIZE = 1024;
	public static final char CR = 0x0d;
	public static final char LF = 0x0a;
	public static final char SP = 0x20;
	public static final char HT = 0x09;

	public abstract long getSerial();

	public abstract String getHTTPVersionAsString();

	public abstract byte[] getHTTPVersionAsBytes();

	public abstract IMessageBody getMessageBody();

	public abstract List<IHTTPHeader> getHeaders();

	public abstract IHTTPHeader removeHeader(byte[] name);

	public abstract void setHeader(byte[] name, byte[] value);

	public abstract void addTrailingHeader(IHTTPHeader trailer);

	public abstract IHTTPHeader getHeader(byte[] name);

	public abstract byte[] getHeaderAsBytes(byte[] name);

	public abstract boolean isChunkedEncoding();

	public abstract void setChunkedEncoding(boolean isChunked);

	public abstract void setTid(long tid);

	public abstract long getTid();

	public abstract void writeBody(long timeout, IMessageBody msgBody,
			OutputStream out) throws IOException, TimeoutException;

	public abstract void write(long timeout, OutputStream out)
			throws IOException, TimeoutException;

	public abstract boolean isHTTPVersion1_1();

	public abstract void setConnectionHeader(boolean keepalive);

	public abstract boolean isConnectionToBeClosed();

}
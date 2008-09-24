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

/**
 * A HTTP request/response message.
 */
public interface IHTTPMessage {

	public static final int INIT_NUM_HEADERS = 50;
	public static final int DEFAULT_INITIAL_BUFFER_SIZE = 1024;
	public static final char CR = 0x0d;
	public static final char LF = 0x0a;
	public static final char SP = 0x20;
	public static final char HT = 0x09;

	public abstract long getSerial();

	/**
	 * Gets the value of HTTP version as a String.
	 * @return HTTP version of this HTTP message
	 */
	public abstract String getHTTPVersionAsString();

	/**
	 * Gets the value of HTTP version as an array of bytes.
	 * @return HTTP version of this HTTP message
	 */
	public abstract byte[] getHTTPVersionAsBytes();

	/**
	 * Gets the body of this HTTP message.
	 * @return body of this HTTP message
	 */
	public abstract IMessageBody getMessageBody();

	/**
	 * Gets all HTTP headers defined in this HTTP message.
	 * @return list of HTTP header objects
	 */
	public abstract List<IHTTPHeader> getHeaders();

	/**
	 * Removes a HTTP header of the specified name.
	 * @param name name of a HTTP header
	 * @return the removed HTTP header, or null if no such HTTP header was defined
	 */
	public abstract IHTTPHeader removeHeader(byte[] name);

	/**
	 * Updates value of a HTTP header, or a HTTP header is added if no HTTP header of the specified name is defined. 
	 * @param name name of HTTP header
	 * @param value value of HTTP header
	 */
	public abstract void setHeader(byte[] name, byte[] value);

	/**
	 * Adds a HTTP trailing header.
	 * @param trailer a HTTP trailing header
	 */
	public abstract void addTrailingHeader(IHTTPHeader trailer);

	/**
	 * Gets a HTTP header of the specified name.
	 * @param name name of a HTTP header 
	 * @return HTTP header of the specified name
	 */
	public abstract IHTTPHeader getHeader(byte[] name);

	/**
	 * Gets value of a HTTP header as an array of bytes.
	 * @param name name of a HTTP header
	 * @return value of the HTTP header as an array of bytes
	 */
	public abstract byte[] getHeaderAsBytes(byte[] name);

	/**
	 * Returns whether the encoding of this message is chunked or not.
	 * @return true if the encoding of this message is chunked.
	 */
	public abstract boolean isChunkedEncoding();

	/**
	 * Sets whether this message is in chunked encoding.
	 * @param isChunked whether this message is in chunked encoding
	 */
	public abstract void setChunkedEncoding(boolean isChunked);

	/**
	 * Sets the TID for this message. TID is unique long value assigned to each HTTP request.
	 * @param tid TID as a long value
	 */
	public abstract void setTid(long tid);

	/**
	 * Gets the TID assigned to this message.
	 * @return TID as a long value
	 */
	public abstract long getTid();

	/**
	 * Writes the message body to the specified output stream.
	 * @param timeout number of milliseconds to wait until the write operation is done, or 0 if it does not need to timeout 
	 * @param msgBody message body to be written
	 * @param out an output stream to which the message body is written
	 * @throws IOException
	 * @throws TimeoutException if it failed to write the message body within the specified time
	 */
	public abstract void writeBody(long timeout, IMessageBody msgBody,
			OutputStream out) throws IOException, TimeoutException;

	/**
	 * Writes this message (headers and message body) to the specified output stream.
	 * @param timeout number of milliseconds to wait until the write operation is done, or 0 if it does not need to timeout
	 * @param out an output stream to which this message is written 
	 * @throws IOException
	 * @throws TimeoutException if it failed to write this message within the specified time 
	 */
	public abstract void write(long timeout, OutputStream out)
			throws IOException, TimeoutException;

	/**
	 * Returns whether the HTTP protocol version of this message is 1.1 or not.
	 * @return true if the HTTP protocol version of this message is 1.1
	 */
	public abstract boolean isHTTPVersion1_1();

	/**
	 * Sets "Connection" HTTP header for this message. 
	 * @param keepalive true to set the value of "Connection" header to "Keep-Alive", false to set the value of "Connection header to "close"  
	 * @see IHTTPHeader#CONNECTION
	 */
	public abstract void setConnectionHeader(boolean keepalive);

	/**
	 * Tells that the connection for exchanging this message needs to be closed or not.
	 * @return true if the connection needs to be closed, or false if it can be reused for the following messages
	 */
	public abstract boolean isConnectionToBeClosed();

}
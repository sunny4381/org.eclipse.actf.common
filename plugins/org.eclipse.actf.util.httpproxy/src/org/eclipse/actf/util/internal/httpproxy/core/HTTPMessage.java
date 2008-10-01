/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.internal.httpproxy.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPMessage;
import org.eclipse.actf.util.httpproxy.core.IMessageBody;
import org.eclipse.actf.util.httpproxy.core.TimeoutException;
import org.eclipse.actf.util.httpproxy.util.Logger;

public abstract class HTTPMessage implements IHTTPMessage {
	static final Logger LOGGER = Logger.getLogger(HTTPMessage.class);

	private transient long fSerial;
	private HTTPMessageBuffer fBuffer;
	private IMessageBody fOriginalMessageBody;
	private IMessageBody fTransformedMessageBody = null;
	private List<IHTTPHeader> fHeaders = new ArrayList<IHTTPHeader>(
			INIT_NUM_HEADERS);
	private List<IHTTPHeader> fTrailingHeaders;
	private boolean isChunkedEncoding = false;

	private long fTid = 0;

	public HTTPMessage(long serial) {
		this(serial, DEFAULT_INITIAL_BUFFER_SIZE);
	}

	public HTTPMessage(long serial, int initBufferSize) {
		fSerial = serial;
		fBuffer = new HTTPMessageBuffer(initBufferSize);
		fOriginalMessageBody = null;
	}

	protected HTTPMessage(long serial, byte[] body) {
		fSerial = serial;
		fBuffer = new HTTPMessageBuffer();
		setOriginalMessageBody(new MessageBody(new ByteArrayInputStream(body),
				body.length));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getSerial()
	 */
	public long getSerial() {
		return fSerial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getHTTPVersionAsString()
	 */
	public abstract String getHTTPVersionAsString();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getHTTPVersionAsBytes()
	 */
	public abstract byte[] getHTTPVersionAsBytes();

	protected abstract boolean isBodyEmpty();

	final void setOriginalMessageBody(IMessageBody msgBody) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("setOriginalMessageBody: " + msgBody);
		}
		updateContentLengthHeader(msgBody);
		fOriginalMessageBody = msgBody;
	}

	final void updateContentLengthHeader(IMessageBody msgBody) {
		int oldContentLength = (fOriginalMessageBody == null) ? -1
				: fOriginalMessageBody.getContentLength();
		int contentLength = (msgBody == null) ? -1 : msgBody.getContentLength();
		if (contentLength >= 0) {
			setHeader(IHTTPHeader.CONTENT_LENGTH_A, Integer.toString(
					contentLength).getBytes());
		} else if (oldContentLength >= 0) {
			removeHeader(IHTTPHeader.CONTENT_LENGTH_A);
		}
	}

	protected IMessageBody getOriginalMessageBody() {
		return fOriginalMessageBody;
	}

	protected IMessageBody getTransformedMessageBody() {
		return fTransformedMessageBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getMessageBody()
	 */
	public IMessageBody getMessageBody() {
		return (fTransformedMessageBody != null) ? fTransformedMessageBody
				: fOriginalMessageBody;
	}

	protected abstract void writeFirstLine(OutputStream out) throws IOException;

	protected HTTPMessageBuffer getBuffer() {
		return fBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getHeaders()
	 */
	public final List<IHTTPHeader> getHeaders() {
		return fHeaders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#removeHeader(byte[])
	 */
	public final IHTTPHeader removeHeader(byte[] name) {
		for (IHTTPHeader h : fHeaders) {
			if (!h.isRemoved() && h.isFieldNameEqualsTo(name)) {
				h.setRemoved(true);
				return h;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#setHeader(byte[],
	 *      byte[])
	 */
	public final void setHeader(byte[] name, byte[] value) {
		HeaderToAdd header = new HeaderToAdd();
		header.init(name, value);
		removeHeader(name);
		fHeaders.add(header);
	}

	final void addHeader(HeaderInBuffer header) {
		fHeaders.add(header);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#addTrailingHeader(org.eclipse.actf.util.httpproxy.core.IHTTPHeader)
	 */
	public final void addTrailingHeader(IHTTPHeader trailer) {
		if (fTrailingHeaders == null) {
			fTrailingHeaders = new ArrayList<IHTTPHeader>(INIT_NUM_HEADERS);
		}
		fTrailingHeaders.add(trailer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getHeader(byte[])
	 */
	public final IHTTPHeader getHeader(byte[] name) {
		for (IHTTPHeader header : fHeaders) {
			if (header.isFieldNameEqualsTo(name)) {
				return header;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getHeaderAsBytes(byte[])
	 */
	public final byte[] getHeaderAsBytes(byte[] name) {
		IHTTPHeader header = getHeader(name);
		return (header == null) ? null : header.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#isChunkedEncoding()
	 */
	public final boolean isChunkedEncoding() {
		return isChunkedEncoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#setChunkedEncoding(boolean)
	 */
	public final void setChunkedEncoding(boolean isChunked) {
		isChunkedEncoding = isChunked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#setTid(long)
	 */
	public final void setTid(long tid) {
		fTid = tid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#getTid()
	 */
	public long getTid() {
		return fTid;
	}

	/*
	 * private void DEBUG(int ch) { System.out.print("Buf[" + fIdx + "]="); if
	 * (ch < 0) { System.out.print("" + ch); } else { int high = ch >>> 4; int
	 * low = (ch & 0x0f); if (high < 10) { high = '0' + high; } else { high =
	 * 'a' + high - 10; } if (low < 10) { low = '0' + low; } else { low = 'a' +
	 * low - 10; } System.out.print((char) high); System.out.print((char) low); }
	 * if (ch > 0x20) { System.out.print(" (" + (char) ch + ")"); }
	 * System.out.println(); }
	 */

	protected void writeHeaders(OutputStream out) throws IOException {
		writeFirstLine(out);
		for (IHTTPHeader header : fHeaders) {
			header.writeLine(out);
		}
		out.write(CR);
		out.write(LF);
	}

	protected abstract void writeBodyWithoutContentLength(long timeout,
			IMessageBody msgBody, OutputStream out) throws IOException,
			TimeoutException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#writeBody(long,
	 *      org.eclipse.actf.util.httpproxy.core.impl.MessageBody,
	 *      java.io.OutputStream)
	 */
	public void writeBody(long timeout, IMessageBody msgBody, OutputStream out)
			throws IOException, TimeoutException {
		if (msgBody == null) {
			return;
		}
		InputStream body = msgBody.getMessageBodyInputStream();
		int contentLength = msgBody.getContentLength();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("super.writeBody: contentLength=" + contentLength
					+ ", body=" + body + ", isChunkedEncoding="
					+ msgBody.isChunkedEncoding());
		}
		if (body != null) {
			if (contentLength >= 0) {
				// normal encoding
				for (int i = 0; i < contentLength; i++) {
					int b = body.read();
					if (b < 0) {
						LOGGER.debug("Unexpected connection shutdown...:" + i
								+ ":" + body);
					}
					out.write(b);
				}
			} else if (msgBody.isChunkedEncoding()) {
				// chunked encoding
				ChunkedMessageBodyReader chunkReader = new ChunkedMessageBodyReader(
						body);
				chunkReader.readChunkedMessage(timeout, out, this);
			} else {
				writeBodyWithoutContentLength(timeout, msgBody, out);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("super.writeBody: done");
		}
	}

	protected IMessageBody transformMessageBody(long timeout, IMessageBody src)
			throws IOException, TimeoutException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#write(long,
	 *      java.io.OutputStream)
	 */
	public void write(long timeout, OutputStream out) throws IOException,
			TimeoutException {
		if (fTransformedMessageBody == null) {
			fTransformedMessageBody = transformMessageBody(timeout,
					fOriginalMessageBody);
			if (fTransformedMessageBody != null) {
				updateContentLengthHeader(fTransformedMessageBody);
			}
		}

		writeHeaders(out);

		writeBody(timeout, getMessageBody(), out);
	}

	/*
	 * public final void writeTrailingHeaders(OutputStream out) throws
	 * IOException { boolean output = false; for (Iterator it =
	 * fTrailingHeaders.iterator(); it.hasNext(); ) { HTTPHeader header =
	 * (HTTPHeader) it.next(); header.write(out); output = true; } if (output) {
	 * out.write(CR); out.write(LF); } }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#isHTTPVersion1_1()
	 */
	public boolean isHTTPVersion1_1() {
		if (Arrays.equals(IHTTPHeader.HTTP_VERSION_1_1_A,
				getHTTPVersionAsBytes()))
			return true;
		return false;
	}

	private static final byte[] CLOSE_A = "close".getBytes();
	private static final byte[] KEEP_ALIVE_A = "Keep-Alive".getBytes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#setConnectionHeader(boolean)
	 */
	public void setConnectionHeader(boolean keepalive) {
		if (keepalive) {
			setHeader(IHTTPHeader.CONNECTION_A, "Keep-Alive".getBytes());
		} else {
			setHeader(IHTTPHeader.CONNECTION_A, "close".getBytes());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPMessage#isConnectionToBeClosed()
	 */
	public boolean isConnectionToBeClosed() {
		IHTTPHeader h = getHeader(IHTTPHeader.CONNECTION_A);
		if (isHTTPVersion1_1()) {
			// When HTTP version is 1.1, we assume the connection is reused by
			// default.
			if (h == null)
				return false;
			return h.compareValueIgnoreCase(CLOSE_A);
		} else {
			// When HTTP version is 1.0, we assume the connection will be
			// shutdown by default.
			if (h == null)
				return true;
			if (h.compareValueIgnoreCase(KEEP_ALIVE_A))
				return false;
			return true;
		}
	}

	public String toString() {
		return "HTTPMessage " + fSerial + ":" + fBuffer.toString();
	}
}

/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.flash.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;

import org.eclipse.actf.model.flash.transcoder.ISwfTranscoder;
import org.eclipse.actf.model.flash.transcoder.ISwfTranscoderFactory;
import org.eclipse.actf.model.internal.flash.bridge.WaXcodingConfig;
import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.core.IPushbackMessageBody;
import org.eclipse.actf.util.httpproxy.core.TimeoutException;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoder;
import org.eclipse.actf.util.httpproxy.util.HTTPUtil;
import org.eclipse.actf.util.httpproxy.util.Logger;

public class ProxyTranscoderSWF implements IHTTPProxyTranscoder {
	private static final Logger LOGGER = Logger.getLogger(ProxyTranscoderSWF.class);

	private static ISwfTranscoderFactory getSwfTranscoderFactory() {
		try {
			// TODO: maybe we should rename the factory package.
			Class<?> clazz = Class
					.forName("org.eclipse.actf.util.swftranscoder.SwfTranscoderFactory"); //$NON-NLS-1$
			return (ISwfTranscoderFactory) clazz.newInstance();
		} catch (Exception e) {
		}
		return null;
	}

	private static final ISwfTranscoderFactory swfTranscoderFactory = getSwfTranscoderFactory();

	private final ISwfTranscoder swfTranscoder;

	private ProxyTranscoderSWF(int id) {
		this.swfTranscoder = swfTranscoderFactory.newInstance(id);
	}

	protected static ProxyTranscoderSWF newInstance(int id) {
		if (swfTranscoderFactory == null)
			return null;
		return new ProxyTranscoderSWF(id);
	}

	private static Object imposedSWF;

	public static void setSWFTranscodingImposedFile(InputStream is) {
		if (swfTranscoderFactory == null)
			return;
		ISwfTranscoder st = swfTranscoderFactory.newInstance(0);
		try {
			imposedSWF = st.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File getTemporaryDestSWF(File src) throws IOException {
		return new File(src.getAbsolutePath() + ".out"); //$NON-NLS-1$
	}

	private File getTemporarySrcSWF() throws IOException {
		return File.createTempFile("SWFTC-", ".swf"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean isTranscodeTarget(int id, IHTTPRequestMessage request,
			IHTTPResponsePushbackMessage response) {
		if (request.getHeader(SWFUtil.X_FLASH_VERSION_A) == null)
			return false;
		if (!SWFUtil.isPossiblySWFContentType(response))
			return false;

		IPushbackMessageBody body = response.getPushbackMessageBody();
		if (body == null)
			return false;
		PushbackInputStream bodyInputStream = body
				.getMessageBodyPushBackInputStream();
		int version = SWFUtil.isSWF(bodyInputStream);
		//
		if (version >= WaXcodingConfig.getInstance()
				.getSWFTranscodingMinimumVersion()) {
			LOGGER.info("[id:" + id + "] SWTTranscoder: SWF to be transcoded (" //$NON-NLS-1$ //$NON-NLS-2$
					+ request.getOriginalRequestURIString() + ")- Version:" //$NON-NLS-1$
					+ version);
			return true;
		} else {
			if (version > 0)
				LOGGER.info("[id:" + id //$NON-NLS-1$
						+ "] SWTTranscoder: SWF not to be transcoded (" //$NON-NLS-1$
						+ request.getOriginalRequestURIString() + ")- Version:" //$NON-NLS-1$
						+ version);
			return false;
		}
	}

	private void outputFailureLog(StringBuffer buf, IHTTPRequestMessage request,
			File outFile, Throwable e) {
		buf.append("("); //$NON-NLS-1$
		buf.append(request.getOriginalRequestURIString());
		buf.append(")\n"); //$NON-NLS-1$
		buf.append(stackTraceToString(e));
		if (LOGGER.isDebugEnabled() && (outFile != null)) {
			buf.append("The SWF file is secured in "); //$NON-NLS-1$
			buf.append(outFile.getAbsolutePath());
			buf.append("\n"); //$NON-NLS-1$
		}
		LOGGER.fatal(buf.toString());
	}

	private File saveSWFFile(File f, byte[] target) throws IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(f);
			os.write(target);
			return f;
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}
	
	private String stackTraceToString(Throwable t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        t.printStackTrace(ps);
        return os.toString();
    }

	public IHTTPResponseMessage transcode(int id, IHTTPRequestMessage request,
			IHTTPResponseMessage response) {
		if (!WaXcodingConfig.getInstance().getSWFTranscodingFlag())
			return response;
		if (imposedSWF == null)
			return response;
		IHTTPResponsePushbackMessage newResponse = HTTPUtil.createHTTPResponsePushbackMessage(
				response, SWFUtil.FLASH_MAGIC_NUMBER_SIZE);

		if (!isTranscodeTarget(id, request, newResponse))
			return newResponse;

		byte[] transcoded;
		File srcSWFFile = null;
		File destSWFFile = null;
		try {
			srcSWFFile = getTemporarySrcSWF();
			byte[] targetBytes;
			try {
				targetBytes = newResponse.readBody(0, false);
			} catch (TimeoutException e) {
				LOGGER.fatal("[id:" + id //$NON-NLS-1$
						+ "] Message body cannot be read by timeout."); //$NON-NLS-1$
				// This should be integrated with sendGatewayTimeout...
				return HTTPUtil.createHTTPResponseInMemoryMessage(request.getSerial(),
						IHTTPHeader.HTTP_VERSION_1_0_A, "504".getBytes(), //$NON-NLS-1$
						"Gateway Timeout".getBytes(), //$NON-NLS-1$
						IHTTPResponseMessage.EMPTY_BODY);
			}
			if (targetBytes == null)
				return response;

			if (LOGGER.isDebugEnabled()) {
				saveSWFFile(srcSWFFile, targetBytes);
			}
			Object srcSwf = swfTranscoder.parse(new ByteArrayInputStream(
					targetBytes));
			Object destSwf = swfTranscoder.impose(imposedSWF, srcSwf);
			transcoded = swfTranscoder.generate(destSwf, false, 8);

			destSWFFile = getTemporaryDestSWF(srcSWFFile);
			if (LOGGER.isDebugEnabled()) {
				saveSWFFile(destSWFFile, transcoded);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return response;
		} catch (Exception e) {
			StringBuffer buf = new StringBuffer("SWF transcoding failed "); //$NON-NLS-1$
			outputFailureLog(buf, request, srcSWFFile, e);
			return response;
		}

		IHTTPResponseMessage msg = HTTPUtil.createHTTPResponseInMemoryMessage(
				response, transcoded);
		return msg;
	}
}

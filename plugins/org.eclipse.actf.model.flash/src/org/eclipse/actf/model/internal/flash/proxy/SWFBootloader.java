/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and Others
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.eclipse.actf.model.flash.util.AsVersionChecker;
import org.eclipse.actf.model.flash.util.SwfInfo;
import org.eclipse.actf.model.internal.flash.bridge.WaXcodingConfig;
import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.core.IPushbackMessageBody;
import org.eclipse.actf.util.httpproxy.core.TimeoutException;
import org.eclipse.actf.util.httpproxy.proxy.IClientStateManager;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider;
import org.eclipse.actf.util.httpproxy.util.CacheMap;
import org.eclipse.actf.util.httpproxy.util.HTTPUtil;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.ParseURI;

public class SWFBootloader implements IHTTPSessionOverrider {

	private static final String MUST_REVALIDATE = "must-revalidate"; //$NON-NLS-1$
	private static final String NO_CACHE = "no-cache"; //$NON-NLS-1$
	private static final String OK = "OK"; //$NON-NLS-1$
	private static final String RESPONSE_200 = "200"; //$NON-NLS-1$

	private static final Logger LOGGER = Logger.getLogger(SWFBootloader.class);

	private static byte[] bootLoaderSWF;
	private static byte[] bootLoaderSWFv9;

	private boolean replacedFlag;
	private boolean bootloaderRequestingFlag;
	private boolean hasXFlashVersionHeader;

	private IHTTPRequestMessage sessionRequest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider#getSessionRequest
	 * ()
	 */
	public IHTTPRequestMessage getSessionRequest() {
		return sessionRequest;
	}

	private IHTTPResponseMessage sessionResponse;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider#
	 * getSessionResponse()
	 */
	public IHTTPResponseMessage getSessionResponse() {
		return sessionResponse;
	}

	private final int id;

	public SWFBootloader(int id) {
		this.id = id;
	}

	private static byte[] readSWFBootLoaderFile(InputStream is) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int b;
			while (true) {
				b = is.read();
				if (b < 0)
					break;
				os.write(b);
			}
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	public static void setSWFBootLoaderFile(InputStream is) {
		bootLoaderSWF = readSWFBootLoaderFile(is);
	}

	public static void setSWFBootLoaderFileV9(InputStream is) {
		bootLoaderSWFv9 = readSWFBootLoaderFile(is);
	}

	private static class CachedSlot {
		public final String uri;
		public IHTTPRequestMessage request;
		public IHTTPResponseMessage response;

		CachedSlot(String uri) {
			this.uri = uri;
		}
	}

	private CachedSlot sessionCachedSlot;

	private static final int MAX_REFERER_LENGTH = 128;

	private Object getCachedObject(IClientStateManager csm, String uri) {
		CacheMap map = getCacheMap(csm);
		if (uri.length() > MAX_REFERER_LENGTH) {
			synchronized (map) {
				return map.matchStartsWith(uri);
			}
		} else {
			synchronized (map) {
				return map.get(uri);
			}
		}
	}

	private void cacheObject(IClientStateManager csm, String uri, Object cobj) {
		CacheMap map = getCacheMap(csm);
		synchronized (map) {
			map.put(uri, cobj);
		}
	}

	private Object removeObject(IClientStateManager csm, String uri) {
		CacheMap map = getCacheMap(csm);
		synchronized (map) {
			return (map.remove(uri));
		}
	}

	private CacheMap getCacheMap(IClientStateManager csm) {
		CacheMap map = (CacheMap) csm.get(SWFBootloader.class);
		if (map == null) {
			map = new CacheMap(256, 20);
			csm.put(SWFBootloader.class, map);
		}
		return map;
	}

	private static IHTTPResponseMessage bootloaderResponseMessage(
			IHTTPRequestMessage request) {
		IHTTPResponseMessage msg = HTTPUtil.createHTTPResponseInMemoryMessage(
				request.getSerial(), IHTTPHeader.HTTP_VERSION_1_0_A,
				RESPONSE_200.getBytes(), OK.getBytes(), bootLoaderSWF);
		msg.setHeader(IHTTPHeader.CACHE_CONTROL_A, MUST_REVALIDATE.getBytes());
		msg.setHeader(IHTTPHeader.CONTENT_TYPE_A,
				SWFUtil.MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH_A);
		return msg;
	}

	/**
	 * Resizes bootloader and returns response message including it
	 * 
	 * @param request
	 *            Request message from client
	 * @param w
	 *            width of bootloader in 'twips'
	 * @param h
	 *            height of bootloader in 'twips'
	 * @return Response message
	 */
	private static IHTTPResponseMessage bootloaderResponseMessageV9(
			IHTTPRequestMessage request, int w, int h) {
		IHTTPResponseMessage msg = HTTPUtil.createHTTPResponseInMemoryMessage(
				request.getSerial(), IHTTPHeader.HTTP_VERSION_1_0_A,
				RESPONSE_200.getBytes(), OK.getBytes(), SwfStageResizer.resize(
						bootLoaderSWFv9, w, h));
		msg.setHeader(IHTTPHeader.CACHE_CONTROL_A, MUST_REVALIDATE.getBytes());
		msg.setHeader(IHTTPHeader.CONTENT_TYPE_A,
				SWFUtil.MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH_A);
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider#replaceRequest
	 * (org.eclipse.actf.util.httpproxy.proxy.ClientStateManager,
	 * org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage)
	 */
	@SuppressWarnings({ "nls", "unused" })
	public boolean replaceRequest(IClientStateManager csm,
			IHTTPRequestMessage request) throws IOException {
		replacedFlag = false;
		if (!WaXcodingConfig.getInstance().getSWFBootloaderFlag())
			return false;

		IHTTPHeader uaHeader = request.getHeader(IHTTPHeader.USER_AGENT_A);
		hasXFlashVersionHeader = uaHeader != null
				&& (new String(uaHeader.getValue()).contains(SWFUtil.MSIE));

		if (hasXFlashVersionHeader
				&& request.getHeader(SWFUtil.X_FLASH_VERSION_A) == null)
			return false;

		String uriStr = request.getRequestURIString();
		String method = request.getMethodAsString();
		Object cobj = getCachedObject(csm, uriStr);
		if (LOGGER.isDebugEnabled()) {
			StringBuffer buf = new StringBuffer("method:");
			buf.append(method);
			buf.append(' ');
			buf.append(uriStr);
			for (IHTTPHeader h : request.getHeaders()) {
				if (!h.isRemoved()) {
					buf.append(h.toString());
					buf.append('\n');
				}
			}
			DEBUG(buf.toString());
		}

		if (IHTTPHeader.METHOD_POST.equals(method)) {
			if (cobj instanceof CachedSlot) {
				ByteArrayOutputStream ob = new ByteArrayOutputStream();
				try {
					request.write(0, ob);
				} catch (TimeoutException e) {
				}
				if (LOGGER.isDebugEnabled()) {
					DEBUG("Bootloader's POST command:"
							+ new String(ob.toByteArray()));
				}
				INFO("bootloader requests the original SWF:" + uriStr);
				this.replacedFlag = true;
				this.bootloaderRequestingFlag = true;
				this.sessionCachedSlot = (CachedSlot) cobj;
				this.sessionRequest = sessionCachedSlot.request;
				this.sessionRequest
						.removeHeader("If-Modified-Since".getBytes());
				this.sessionResponse = sessionCachedSlot.response;
				return true;
			}
		}

		IHTTPHeader referer = request.getHeader(IHTTPHeader.REFERER_A);
		if (referer != null) {
			String refStrBase;
			String uriStrBase;

			if (false) {
				refStrBase = ParseURI.eliminateQuery(new String(referer
						.getValue()).trim());
				uriStrBase = ParseURI.eliminateQuery(uriStr);
				// System.err.println("R: " + refStrBase);
				// System.err.println("U: " + uriStrBase);
			} else {
				refStrBase = new String(referer.getValue()).trim();
				uriStrBase = uriStr;
			}

			Object o = getCachedObject(csm, refStrBase);
			if (o != null) {
				INFO(uriStrBase + " seems to be referred by " + refStrBase);
				if (cobj == null) {
					cacheObject(csm, uriStrBase, Boolean.valueOf(true));
				}
				return false;
			}
		} else if (!hasXFlashVersionHeader) {
			// requests from SWF do not have referer in the case of FireFox
			// 2.0.0.14/FlashPlayer 9.0.124

			INFO(uriStr + " seems to be referred by other swf");

			// System.err.println("R: null\tU: " + uriStr);
			return false;
		}

		// send bootloader swf

		CachedSlot cs;
		if (cobj instanceof CachedSlot) {
			cs = (CachedSlot) cobj;
		} else {
			cs = new CachedSlot(uriStr);
			if (false) {
				cacheObject(csm, ParseURI.eliminateQuery(uriStr), cs);
			} else {
				cacheObject(csm, uriStr, cs);
			}
		}
		cs.request = request;
		/*
		 * INFO("bootloader is used for " + uriStr); HTTPResponseMessage msg =
		 * bootloaderResponseMessage(request); this.sessionResponse = msg;
		 */
		this.replacedFlag = true;
		this.bootloaderRequestingFlag = false;
		this.sessionCachedSlot = cs;
		this.sessionRequest = request;
		this.sessionRequest.removeHeader(IHTTPHeader.IF_MODIFIED_SINCE_A);
		this.sessionRequest.removeHeader(IHTTPHeader.ACCEPT_ENCODING_A);
		this.sessionResponse = null;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider#replaceResponse
	 * (org.eclipse.actf.util.httpproxy.proxy.ClientStateManager,
	 * org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage,
	 * org.eclipse.actf.util.httpproxy.core.HTTPResponseMessage, int)
	 */
	@SuppressWarnings("nls")
	public boolean replaceResponse(IClientStateManager csm,
			IHTTPRequestMessage request, IHTTPResponseMessage response,
			int timeout) throws IOException, TimeoutException {
		if (!replacedFlag)
			return false;

		if (bootloaderRequestingFlag) {
			if (response != sessionCachedSlot.response) {
				response.setHeader(IHTTPHeader.CACHE_CONTROL_A, NO_CACHE
						.getBytes());
			}
			this.sessionResponse = response;
			return true;
		} else {
			if (hasXFlashVersionHeader) {
				if (!SWFUtil.isPossiblySWFContentType(response))
					return false;
			} else {
				if (!SWFUtil.isSWFContentType(response)) {
					removeObject(csm, sessionCachedSlot.uri);
					return false;
				}
			}

			IHTTPResponsePushbackMessage newResponse = HTTPUtil
					.createHTTPResponsePushbackMessage(response,
					// SWFUtil.FLASH_MAGIC_NUMBER_SIZE);
							AsVersionChecker.READBUFFER_SIZE);

			IPushbackMessageBody body = newResponse.getPushbackMessageBody();
			if (body == null)
				return false;
			PushbackInputStream bodyInputStream = body
					.getMessageBodyPushBackInputStream();
			int version = SWFUtil.isSWF(bodyInputStream);
			SwfInfo swfInfo = null;

			INFO("The incoming SWF is version " + version);
			String uriStr = request.getRequestURIString();
			if ((version >= WaXcodingConfig.getInstance()
					.getSWFTranscodingMinimumVersion())
					&& (version < 10)) {
				// Need to test Flash Version 10 or later

				IHTTPResponseMessage msg;
				if (version < 9) {
					msg = bootloaderResponseMessage(request);
					INFO("bootloader is used for " + uriStr);
				} else {
					DEBUG("Checking AS version...");

					AsVersionChecker asChecker = new AsVersionChecker();
					asChecker.setSwfFile(bodyInputStream);
					swfInfo = asChecker.getSwfInfo();
					int asVersion = swfInfo.getAsVersion();
					// note that frame size is in 'twips' (= 1/20 pixel)
					int frameX = swfInfo.getFrameSizeX();
					int frameY = swfInfo.getFrameSizeY();

					INFO("AS version of SWF: " + asVersion);

					switch (asVersion) {
					case 3:
						INFO("Resizing bootloader...");
						msg = bootloaderResponseMessageV9(request, frameX,
								frameY);
						INFO("bootloader v9 is used for " + uriStr);
						break;
					case 2:
						msg = bootloaderResponseMessage(request);
						INFO("bootloader (v9-as2) is used for " + uriStr);
						break;
					default:
						INFO("Flash V9 (AS version:" + asVersion
								+ "). The bootloader is not applied to "
								+ uriStr);
						this.sessionResponse = newResponse;
						return true;
					}
				}

				this.sessionResponse = msg;
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				newResponse.writeBody(timeout, body, os);
				IHTTPResponseMessage cachedResponse = HTTPUtil
						.createHTTPResponseInMemoryMessage(response, os
								.toByteArray());
				cachedResponse.setHeader(IHTTPHeader.CACHE_CONTROL_A,
						"no-cache,must-revalidate".getBytes());
				cachedResponse.setHeader(IHTTPHeader.PRAGMA_A, NO_CACHE
						.getBytes());
				cachedResponse
						.setHeader(IHTTPHeader.EXPIRES_A, "-1".getBytes());

				INFO("The original response is cached for later use.");
				if (LOGGER.isDebugEnabled()) {
					StringBuffer buf = new StringBuffer("CachedMessage: ");
					for (IHTTPHeader h : cachedResponse.getHeaders()) {
						if (!h.isRemoved()) {
							buf.append(h.toString());
							buf.append('\n');
						}
					}
					DEBUG(buf.toString());
				}
				sessionCachedSlot.response = cachedResponse;
				return true;
			} else {
				INFO("Due to the unsupported version, the bootloader is not applied to "
						+ uriStr);
				this.sessionResponse = newResponse;
				return true;
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[id:").append(id).append("] SWFBootloader"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}

	private final void INFO(String msg) {
		// StringBuffer sb = new StringBuffer();
		// sb.append(this.toString()).append(": ").append(msg);
		// LOGGER.info(sb.toString());
		LOGGER.info(msg);
	}

	private final void DEBUG(String msg) {
		// StringBuffer sb = new StringBuffer();
		// sb.append(this.toString()).append(": ").append(msg);
		// LOGGER.debug(sb.toString());
		LOGGER.debug(msg);
	}

}

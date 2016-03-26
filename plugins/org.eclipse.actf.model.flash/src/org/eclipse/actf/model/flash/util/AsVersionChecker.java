/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class AsVersionChecker {

	public static final int READBUFFER_SIZE = 1048576;

	private static final Logger LOGGER = Logger
			.getLogger(AsVersionChecker.class.getName());

	private URL url;
	private InputStream in;

	private class MyBufferedInputStream extends BufferedInputStream {
		public MyBufferedInputStream(InputStream in, int size) {
			super(in, size);
			super.mark(size);
		}

		public byte[] getBuffer() {
			if (markpos == 0) {
				byte[] result = new byte[count];
				System.arraycopy(buf, 0, result, 0, count);
				return result;
			}
			LOGGER.severe("load size over: " + buf.length); //$NON-NLS-1$
			return new byte[0];
		}

	}

	public AsVersionChecker() {
	}

	public void setSwfFile(String path) {
		try {
			File f = new File(path);
			if (f.exists()) {
				url = f.toURI().toURL();
			} else {
				url = new URL(path);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			url = null;
		}
	}

	public void setSwfFile(URL _url) {
		url = _url;
	}

	public void setSwfFile(InputStream _in) {
		in = _in;
		url = null;
	}

	public SwfInfo getSwfInfo() {
		SwfInfo swfInfo = new SwfInfo();
		try {
			InputStream is;
			if (url == null) {
				if (in == null)
					return swfInfo;
				else
					is = in;
			} else {
				is = url.openConnection().getInputStream();
			}

			MyBufferedInputStream mbis = new MyBufferedInputStream(is,
					READBUFFER_SIZE);

			SwfHeaderParser shp = new SwfHeaderParser(mbis);
			shp.parse();

			swfInfo.setFrameSizeX(shp.getFrameSizeX());
			swfInfo.setFrameSizeY(shp.getFrameSizeY());

			is = shp.getInputStream();
			// byte[] headerBytes = shp.getHeaderBytes();
			byte[] buf;

			LOGGER.fine("searching AS related tag..."); //$NON-NLS-1$
			while (true) {
				buf = readBytes(is, 2);
				int typelen = (buf[0] & 0xff) | ((buf[1] & 0xff) << 8);
				LOGGER.fine("**" + Integer.toHexString(typelen)); //$NON-NLS-1$
				int type = typelen >> 6;
				int length = typelen & 0x3f;
				if (length == 0x3f) {
					buf = readBytes(is, 4);
					length = (buf[0] & 0xff) | ((buf[1] & 0xff) << 8)
							| ((buf[2] & 0xff) << 16) | ((buf[3] & 0xff) << 24);
				}
				LOGGER.fine("type=" + type + ", length=" + length); //$NON-NLS-1$ //$NON-NLS-2$
				// System.out.println("type=" + type + ", length=" + length);
				if (type == 0) {
					pushBack(mbis.getBuffer());
					swfInfo.setAsVersion(-1);
					return swfInfo;
				} else if (type == 69) {
					// type 69 is usually at first
					buf = readBytes(is, 1);
					if ((buf[0] & 0x08) > 0) {
						pushBack(mbis.getBuffer());
						swfInfo.setAsVersion(3);
						return swfInfo;
					} else {
						pushBack(mbis.getBuffer());
						swfInfo.setAsVersion(2);
						return swfInfo;
					}
					// items below are backup method for non-standard format
					// content
				} else if (type == 59 || type == 12) {// DoInitAction,
														// DoAction
					pushBack(mbis.getBuffer());
					swfInfo.setAsVersion(2);
					return swfInfo;
				} else if (type == 82) {// DoABC
					pushBack(mbis.getBuffer());
					swfInfo.setAsVersion(3);
				} else {
					// is.skip(length);
					readBytes(is, length);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			swfInfo.setAsVersion(-1);
			return swfInfo;
		}
	}

	private byte[] readBytes(InputStream is, int length) throws IOException {
		byte[] buf = new byte[length];
		int count = 0;
		while (count < length) {
			int read = is.read(buf, count, length - count);
			if (read > 0) {
				count += read;
			}
		}
		return buf;
	}

	private void pushBack(byte[] header) throws IOException {
		if (in instanceof PushbackInputStream) {
			PushbackInputStream pis = (PushbackInputStream) in;
			pis.unread(header);
		}
	}

	public static void main(String[] args) throws IOException {
		LOGGER.info("started"); //$NON-NLS-1$
		System.out.println("1 - AS3, 2 - AS3 no compress, 3 - AS2\n?"); //$NON-NLS-1$
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		int choice = Integer.parseInt(r.readLine());
		AsVersionChecker checker = new AsVersionChecker();
		if (choice == 1)
			checker.setSwfFile("checker/f9as3.swf"); //$NON-NLS-1$
		else if (choice == 2)
			checker.setSwfFile("checker/f9as3-noc.swf"); //$NON-NLS-1$
		else
			checker.setSwfFile("checker/f9as2.swf"); //$NON-NLS-1$
		int ver = checker.getSwfInfo().getAsVersion();
		LOGGER.info("version=" + ver); //$NON-NLS-1$
	}
}

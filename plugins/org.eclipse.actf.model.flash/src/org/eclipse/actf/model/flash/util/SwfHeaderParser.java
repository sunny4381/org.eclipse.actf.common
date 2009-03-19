/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and Others
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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

public class SwfHeaderParser {
	private static final Logger LOGGER = Logger.getLogger(SwfHeaderParser.class
			.getName());

	private static int COMPRESSED_FLAG = 0x43; // 'C'

	private int bitBuf = 0;

	private int bitPos = 0;

	private int byteCount = 0;

	private byte[] buf1 = new byte[8];

	private byte[] buf2 = new byte[17];

	private byte[] buf3 = new byte[4];
	
	private InputStream in;

	private boolean isCompressed = false;

	private int version;

	private int length;

	private int rate;

	private int framecount;

	public SwfHeaderParser(InputStream _in) {
		in = _in;
	}

	private int readSBits(int numBits) throws IOException {
		if (numBits > 32) {
			throw new IOException("Number of bits > 32"); //$NON-NLS-1$
		}

		int num = readUBits(numBits);
		int shift = 32 - numBits;
		// sign extension
		num = (num << shift) >> shift;
		return num;
	}

	private int readUBits(int numBits) throws IOException {
		if (numBits == 0) {
			return 0;
		}

		int bitsLeft = numBits;
		int result = 0;
		byte[] buf;

		if (bitPos == 0) // no value in the buffer - read a byte
		{
			buf = new byte[1];
			in.read(buf);			
			buf2[byteCount] = buf[0];
			byteCount++;
			
			bitBuf = buf[0] & 0xFF;
			bitPos = 8;
		}

		while (true) {
			int shift = bitsLeft - bitPos;
			if (shift > 0) {
				// Consume the entire buffer
				result |= bitBuf << shift;
				bitsLeft -= bitPos;

				// Get the next byte from the input stream
				buf = new byte[1];
				in.read(buf);
				buf2[byteCount] = buf[0];
				byteCount++;
				
				bitBuf = buf[0] & 0xFF;
				bitPos = 8;
			} else {
				// Consume a portion of the buffer
				result |= bitBuf >> -shift;
				bitPos -= bitsLeft;
				bitBuf &= 0xff >> (8 - bitPos); // mask off the consumed bits

				// if (print) System.out.println(" read"+numBits+" " + result);
				return result;
			}
		}		
	}

	public void parse() throws IOException {
		LOGGER.fine("skipping header..."); //$NON-NLS-1$
		in.read(buf1);
		if (buf1[0] == COMPRESSED_FLAG) {
			isCompressed = true;
		}
		LOGGER.fine("compressed=" + isCompressed); //$NON-NLS-1$
		version = buf1[3] & 0xFF;
		LOGGER.fine("version=" + version); //$NON-NLS-1$
		length = buf1[4] & 0xFF | (buf1[5] & 0xFF) << 8
				| (buf1[6] & 0xFF) << 16 | buf1[7] << 24;
		LOGGER.fine("length=" + length); //$NON-NLS-1$

		if (isCompressed) {
			in = new InflaterInputStream(in);
		}

		skipRect();

		// if (false) {
		// in.skip(4);
		// } else {
		in.read(buf3);
		rate = ((buf3[0] & 0xff) << 8) | (buf3[1] & 0xff);
		framecount = (buf3[2] & 0xff) | ((buf3[3] & 0xff) << 8);
		LOGGER.fine("rate=" + rate + ", framecount=" + framecount); //$NON-NLS-1$ //$NON-NLS-2$
		// }
		// return in;
						
//		for(byte b : headerBytes){
//			System.out.println(b);
//		}
				
	}

	private void skipRect() throws IOException {
		int nBits = readUBits(5);
		LOGGER.fine(Integer.toString(readSBits(nBits)));
		LOGGER.fine(Integer.toString(readSBits(nBits)));
		LOGGER.fine(Integer.toString(readSBits(nBits)));
		LOGGER.fine(Integer.toString(readSBits(nBits)));
	}

	public boolean isCompressed() {
		return isCompressed;
	}

	public int getVersion() {
		return version;
	}

	public int getLength() {
		return length;
	}

	public InputStream getInputStream() {
		return in;
	}

}

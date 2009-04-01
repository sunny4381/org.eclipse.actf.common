/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

class SwfStageResizer {
	private static final Logger LOGGER = Logger.getLogger(SwfStageResizer.class
			.getName());
	private static final int COMPRESSED_FLAG = 0x43; // 'C'

	private int bitBuf = 0;
	private int bitPos = 0;
	private int byteCount = 0;
	private byte[] buf2 = new byte[17];

	private ByteArrayOutputStream bo;
	private InputStream bi;

	static public byte[] resize(byte[] swf, int w, int h) {
		return new SwfStageResizer().doResize(swf, w, h);
	}

	public byte[] doResize(byte[] swf, int w, int h) {
		try {
			LOGGER
					.info("Resizing SWF to " + (w / 20) + "x" + (h / 20) + " pixels..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bo = new ByteArrayOutputStream();
			bi = new ByteArrayInputStream(swf);

			int data;
			byte[] buf;

			data = bi.read();
			bo.write('F');
			boolean isCompressed = (data == COMPRESSED_FLAG);
			LOGGER.fine("compressed? = " + isCompressed); //$NON-NLS-1$

			bo.write(bi.read());
			bo.write(bi.read());

			data = bi.read();
			bo.write(data);
			int swfVersion = data & 0xFF;
			LOGGER.fine("SWF version = " + swfVersion); //$NON-NLS-1$

			buf = new byte[4];
			bi.read(buf);
			int length = buf[0] & 0xFF | (buf[1] & 0xFF) << 8
					| (buf[2] & 0xFF) << 16 | buf[3] << 24;
			LOGGER.fine("length = " + length); //$NON-NLS-1$
			bo.write(buf); // writes overwritten data

			if (isCompressed) {
				bi = new InflaterInputStream(bi);
			}

			int[] size = readFrameSize();
			LOGGER
					.fine("original frame size (in twips) = " + size[0] + "x" + size[1]); //$NON-NLS-1$ //$NON-NLS-2$
			writeFrameSize(w, h);

			data = bi.read();
			int rate = (data & 0xff) << 8;
			bo.write(data);
			data = bi.read();
			rate |= data & 0xff;
			bo.write(data);
			LOGGER.fine("framerate = " + rate); //$NON-NLS-1$

			data = bi.read();
			int framecount = data & 0xff;
			bo.write(data);
			data = bi.read();
			framecount |= (data & 0xff) << 8;
			bo.write(data);
			LOGGER.fine("framecount = " + framecount); //$NON-NLS-1$
			// header finished

			// main body. consumes tags
			while ((data = bi.read()) >= 0) {
				bo.write(data);
			}

			// modify file size field
			byte[] array = bo.toByteArray();
			int fileSize = array.length;
			LOGGER
					.info("Modifying file size field from " + length + " to " + fileSize); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 4; i <= 7; i++) {
				array[i] = (byte) (fileSize & 0xFF);
				fileSize = fileSize >> 8;
			}

			LOGGER.fine("done"); //$NON-NLS-1$
			return array;
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	private int[] readFrameSize() throws IOException {
		int[] size = new int[2];

		int nBits = readUBits(5);

		LOGGER.fine("frame size X min = " + Integer.toString(readSBits(nBits))); //$NON-NLS-1$
		size[0] = readSBits(nBits);
		LOGGER.fine("frame size X max (twips) = " + Integer.toString(size[0])); //$NON-NLS-1$
		LOGGER.fine("frame size Y min = " + Integer.toString(readSBits(nBits))); //$NON-NLS-1$
		size[1] = readSBits(nBits);
		LOGGER.fine("frame size Y max (twips) = " + Integer.toString(size[1])); //$NON-NLS-1$
		return size;
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
			bi.read(buf);
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
				bi.read(buf);
				buf2[byteCount] = buf[0];
				byteCount++;

				bitBuf = buf[0] & 0xFF;
				bitPos = 8;
			} else {
				// Consume a portion of the buffer
				result |= bitBuf >> -shift;
				bitPos -= bitsLeft;
				bitBuf &= 0xff >> (8 - bitPos); // mask off the consumed bits

				return result;
			}
		}
	}

	private void writeFrameSize(int w, int h) throws IOException {
		int dw = numDigit(w) + 1;
		int dh = numDigit(h) + 1;
		LOGGER.fine("required number of digits = " + dw + ", " + dh); //$NON-NLS-1$ //$NON-NLS-2$
		int d = Math.max(dw, dh);
		BitBuffer bb = new BitBuffer();
		bb.append(d, 5);
		bb.append(0, d);
		bb.append(w, d);
		bb.append(0, d);
		bb.append(h, d);
		bo.write(bb.toByteArray());
	}

	private int numDigit(int v) {
		if (v < 0) {
			return -1;
		} else {
			int d = 0;
			while (v > 0) {
				v = v >> 1;
				d++;
			}
			return d;
		}
	}
}

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


class BitBuffer {
//	private static final Logger LOGGER = Logger.getLogger(BitBuffer.class
//			.getName());

	/**
	 * Byte array where encoded bits are stored.
	 */
	private byte[] m_value;

	/**
	 * Bit position in allocated byte array. Position of the left-most bit is 0.
	 */
	private int bitpos;

	public BitBuffer() {
		m_value = new byte[0];
		bitpos = 0;
	}

	public BitBuffer append(int i, int len) {
		if (bitpos + len > m_value.length * 8) {
			expandCapacity(bitpos + len - 1);
		}

		i = i << (32 - len);
		int totalusebit = 0;
		int byteindex = bitpos / 8;

		while (len - totalusebit + bitpos % 8 > 8) {
			int usebit = 8 - bitpos % 8;
			int shiftedvalue = i >>> (32 - usebit);
			m_value[byteindex++] += shiftedvalue;
			i = i << usebit;
			totalusebit += usebit;
			bitpos += usebit;
			byteindex = bitpos / 8;
		}

		int shiftedvalue = (i >>> (24 + bitpos % 8)) & 0xFF;
		m_value[byteindex] += shiftedvalue;
		bitpos += len - totalusebit;

		return this;
	}

	public byte[] toByteArray() {
		return m_value;
	}

	private void expandCapacity(int newbitlength) {
		// LOGGER.info("bitpos = " + bitpos);
		// LOGGER.info("new bit length = " + newbitlength);
		int newbytelength = newbitlength / 8 + 1;
		assert newbytelength > m_value.length;
		byte[] newvalue = new byte[newbytelength];
		System.arraycopy(m_value, 0, newvalue, 0, m_value.length);
		m_value = newvalue;
	}

	public static void main(String[] args) {
		BitBuffer bb = new BitBuffer();
		bb.append(255, 8);
		bb.append(3, 3);
		bb.append(13, 5);

		byte[] bytes = bb.toByteArray();
		for (int i = 0; i < bytes.length; i++) {
			System.out.println("byte[" + i + "] = " //$NON-NLS-1$ //$NON-NLS-2$
					+ Integer.toBinaryString(bytes[i] & 0xFF));
		}
	}
}

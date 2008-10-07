/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32;

import org.eclipse.swt.internal.win32.OS;

/**
 * Utility class to send/receive messages with other window
 */
@SuppressWarnings("restriction")
public class COPYDATASTRUCT {

	public static final int WM_COPYDATA = 0x4a;
	public static final int sizeof = 12;

	public int dwData;
	public int cbData;
	public int lpData;
	public byte[] data;

	/**
	 * Create a COPYDATASTRUCT object using String data
	 * 
	 * @param dwData
	 * @param strData
	 */
	public COPYDATASTRUCT(int dwData, String strData) {
		this(dwData, strData.getBytes());
	}

	/**
	 * Create a COPYDATASTRUCT object using binary data
	 * 
	 * @param dwData
	 * @param data
	 */
	public COPYDATASTRUCT(int dwData, byte[] data) {
		this.dwData = dwData;
		if (null != data) {
			this.cbData = data.length;
			this.data = new byte[data.length];
			System.arraycopy(data, 0, this.data, 0, data.length);
		}
	}

	/**
	 * Create a COPYDATASTRUCT object using lParam
	 * 
	 * @param lParam
	 */
	public COPYDATASTRUCT(int lParam) {
		int[] pEntries = new int[3];
		OS.MoveMemory(pEntries, lParam, sizeof);
		dwData = pEntries[0];
		cbData = pEntries[1];
		lpData = pEntries[2];
		if (0 != lpData && cbData > 0) {
			data = new byte[cbData];
			OS.MoveMemory(data, lpData, cbData);
		} else {
			data = new byte[0];
		}
	}

	/**
	 * Copy a COPYDATASTRUCT object to memory
	 * 
	 * @param pData
	 */
	private void setData(int pData) {
		int p = 0;
		if (null != data) {
			p = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, cbData);
			OS.MoveMemory(p, data, cbData);
		}
		OS.MoveMemory(pData, new int[] { dwData, cbData, p }, sizeof);
	}

	/**
	 * Send a COPYDATASTRUCT object to another window using WM_COPYDATA
	 * 
	 * @param hwndTo
	 *            target window to send data
	 * @param hwndFrom
	 *            send data from this window
	 * @return result code
	 */
	public int sendMessage(int hwndTo, int hwndFrom) {
		int lpData = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, sizeof);
		try {
			setData(lpData);
			return OS.SendMessage(hwndTo, WM_COPYDATA, hwndFrom, lpData);
		} finally {
			OS.GlobalFree(lpData);
		}
	}

	/**
	 * Retrieve a String data
	 * 
	 * @return data as new String
	 */
	public String getStringData() {
		try {
			return new String(data);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "dwData=" + dwData + ", cbData=" + cbData + ", lpData=0x" + Integer.toHexString(lpData) + ", data=\"" + new String(data) + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}
}

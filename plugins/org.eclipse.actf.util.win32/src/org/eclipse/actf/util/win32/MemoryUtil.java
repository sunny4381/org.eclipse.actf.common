/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32;

import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.win32.OS;

/**
 * Utility class to manipulate memory.
 */
@SuppressWarnings("restriction")
public class MemoryUtil {

	/**
	 * GlobalAlloc the memory
	 * 
	 * @param dwBytes
	 *            size
	 * @return pointer
	 */
	public static int GlobalAlloc(int dwBytes) {
		return OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, dwBytes);
	}

	/**
	 * GlobalFree target memory
	 * 
	 * @param hMem
	 *            target memory
	 * @return return code
	 */
	public static int GlobalFree(int hMem) {
		return OS.GlobalFree(hMem);
	}

	/**
	 * Allocate OLE String
	 * 
	 * @param sz
	 *            target String as array of char
	 * @return BSTR pointer
	 */
	public static int SysAllocString(char[] sz) {
		return COM.SysAllocString(sz);
	}

	/**
	 * Free OLE String
	 * 
	 * @param bstr
	 *            target BSTR pointer
	 */
	public static void SysFreeString(int bstr) {
		COM.SysFreeString(bstr);
	}

	/**
	 * MoveMemory
	 * 
	 * @param Destination
	 * @param Source
	 * @param Length
	 */
	public static void MoveMemory(int[] Destination, int Source, int Length) {
		OS.MoveMemory(Destination, Source, Length);
	}

	/**
	 * MoveMemory
	 * 
	 * @param Destination
	 * @param SourcePtr
	 * @param Length
	 */
	public static void MoveMemory(short[] Destination, int SourcePtr, int Length) {
		OS.MoveMemory(Destination, SourcePtr, Length);
	}

	/**
	 * MoveMemory
	 * 
	 * @param Destination
	 * @param SourcePtr
	 * @param Length
	 */
	public static void MoveMemory(char[] Destination, int SourcePtr, int Length) {
		OS.MoveMemory(Destination, SourcePtr, Length);
	}

	/**
	 * MoveMemory
	 * 
	 * @param DestinationPtr
	 * @param Source
	 * @param Length
	 */
	public static void MoveMemory(int DestinationPtr, short[] Source, int Length) {
		OS.MoveMemory(DestinationPtr, Source, Length);
	}

	/**
	 * MoveMemory
	 * 
	 * @param DestinationPtr
	 * @param Source
	 * @param Length
	 */
	public static void MoveMemory(int DestinationPtr, int[] Source, int Length) {
		OS.MoveMemory(DestinationPtr, Source, Length);
	}

	/**
	 * MoveMemory
	 * 
	 * @param DestinationPtr
	 * @param Source
	 * @param Length
	 */
	public static void MoveMemory(int DestinationPtr, char[] Source, int Length) {
		OS.MoveMemory(DestinationPtr, Source, Length);
	}

}

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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.internal.win32.TCHAR;

/**
 * Utility class for window handle.
 */
@SuppressWarnings("restriction")
public class WindowUtil {

	/**
	 * Get window text.
	 * 
	 * @param hWnd
	 *            target window
	 * @return window text
	 */
	public static String GetWindowText(int hWnd) {
		int size = OS.GetWindowTextLength(hWnd);
		if (0 == size) {
			return ""; //$NON-NLS-1$
		}
		TCHAR buffer = new TCHAR(0, size + 1);
		return buffer.toString(0, OS.GetWindowText(hWnd, buffer, buffer
				.length()));
	}

	/**
	 * Get window class name
	 * 
	 * @param hWnd
	 *            target window
	 * @return window class name
	 */
	public static String GetWindowClassName(int hWnd) {
		TCHAR buffer = new TCHAR(0, 256);
		return buffer.toString(0, OS
				.GetClassName(hWnd, buffer, buffer.length()));
	}

	/**
	 * Get window rectangle
	 * 
	 * @param hWnd
	 *            target window
	 * @return window rectangle
	 */
	public static Rectangle GetWindowRectangle(int hWnd) {
		RECT rect = new RECT();
		OS.GetWindowRect(hWnd, rect);
		return new Rectangle(rect.left, rect.top, rect.right - rect.left,
				rect.bottom - rect.top);
	}

	/**
	 * Check if target window is visible
	 * 
	 * @param hWnd
	 *            target window
	 * @return true if the target window is visible
	 */
	public static boolean IsWindowVisible(int hWnd) {
		return OS.IsWindowVisible(hWnd);
	}

	/**
	 * Get desktop window
	 * 
	 * @return desktop window
	 */
	public static int GetDesktopWindow() {
		return OS.GetDesktopWindow();
	}

	/**
	 * Get child window
	 * 
	 * @param hWnd
	 *            target window
	 * @return child window of the target
	 */
	public static int GetChildWindow(int hWnd) {
		return OS.GetWindow(hWnd, OS.GW_CHILD);
	}

	/**
	 * Get next window
	 * 
	 * @param hWnd
	 *            target window
	 * @return next window of the target
	 */
	public static int GetNextWindow(int hWnd) {
		return OS.GetWindow(hWnd, OS.GW_HWNDNEXT);
	}

	/**
	 * Get owner window
	 * 
	 * @param hWnd
	 *            target window
	 * @return owner window of the target
	 */
	public static int GetOwnerWindow(int hWnd) {
		return OS.GetWindow(hWnd, OS.GW_OWNER);
	}

	/**
	 * Get parent window
	 * 
	 * @param hWnd
	 *            target window
	 * @return parent window of the target
	 */
	public static int GetParentWindow(int hWnd) {
		return OS.GetParent(hWnd);
	}

	/**
	 * Check if target is popup menu
	 * 
	 * @param hwnd
	 *            target window
	 * @return true if the target is popup menu
	 */
	public static boolean isPopupMenu(int hwnd) {
		if ("#32768".equals(GetWindowClassName(hwnd))) { //$NON-NLS-1$
			return 0 == GetOwnerWindow(hwnd);
		}
		return false;
	}

	static {
		try {
			System.loadLibrary("AccessibiltyWin32Library"); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int WS_EX_LAYERED = 0x80000;

	public static final int LWA_COLORKEY = 0x01;

	public static final int LWA_ALPHA = 0x02;

	protected static final native int SetLayeredWindowAttributes(int hwnd,
			int crKey, char bAlpha, int dwFlags);

}

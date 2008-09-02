/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32.keyhook;

/**
 * ISendEvent interface defines the methods to send window events.
 */
public interface ISendEvent {
	/**
	 * This method send a key event to the focused window.
	 * 
	 * @param vkey
	 *            the virtual key code to be sent.
	 * @param isUp
	 *            the key status of up/down.
	 * @return if this function is succeeded then it return true.
	 */
	boolean postKey(int vkey, boolean isUp);

	/**
	 * This method send a mouse event to the focuesd window.
	 * 
	 * @param x
	 *            The x position of the mouse cursor to be set.
	 * @param y
	 *            The y position of the mouse cursor to be set.
	 * @param isUp
	 *            The mouse status of up/down.
	 * @return If this function is succeeded then it return true.
	 */
	boolean postMouse(int x, int y, boolean isUp);

	/**
	 * @param hwnd
	 *            The window handle to be focused.
	 * @return If this function is succeeded then it return true.
	 */
	boolean focusWindow(long hwnd);

	/**
	 * @param hwnd
	 *            The window handle to which the key event will be sent.
	 * @param vkey
	 *            The virtual key code to be sent.
	 * @param isUp
	 *            The key status of up/down.
	 * @return If this function is succeeded then it return true.
	 */
	boolean postKeyToWindow(long hwnd, int vkey, boolean isUp);

	/**
	 * @param hwnd
	 *            The window handle to which the mouse event will be sent.
	 * @param x
	 *            The x position of the mouse cursor to be set.
	 * @param y
	 *            The y position of the mouse cursor to be set.
	 * @param isUp
	 *            The mouse status of up/down.
	 * @return If this function is succeeded then it return true.
	 */
	boolean postMouseToWindow(long hwnd, int x, int y, boolean isUp);

	/**
	 * @param className
	 *            The class name of the window to be searched.
	 * @param windowName
	 *            The window name of the window to be searched.
	 * @return If this function is succeeded then it return true.
	 */
	long findWindow(String className, String windowName);
}

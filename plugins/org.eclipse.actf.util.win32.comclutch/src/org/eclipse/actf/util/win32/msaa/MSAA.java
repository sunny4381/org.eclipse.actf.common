/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.msaa;

import org.eclipse.actf.util.win32.comclutch.IDispatch;


/**
 * MSAA is utility for Microsoft Active Accessibility Interface
 * see http://msdn.microsoft.com/en-us/library/ms697707.aspx
 */
public class MSAA {

	public static final int CHILDID_SELF = 0;
	public static final int NAVDIR_DOWN = 2;
	public static final int NAVDIR_FIRSTCHILD = 7;
	public static final int NAVDIR_LASTCHILD = 8;
	public static final int NAVDIR_LEFT = 3;
	public static final int NAVDIR_NEXT = 5;
	public static final int NAVDIR_PREVIOUS = 6;
	public static final int NAVDIR_RIGHT = 4;
	public static final int NAVDIR_UP = 1;
	
	public static final int SELFLAG_TAKEFOCUS = 1;
	public static final int SELFLAG_REMOVESELECTION = 16;

	public static final int STATE_READONLY = 0x40;
    public static final int STATE_INVISIBLE = 0x8000;
    public static final int STATE_OFFSCREEN = 0x10000;

	public static final int ROLE_SYSTEM_WINDOW = 0x09;
    public static final int ROLE_SYSTEM_CLIENT = 0x0a;
    public static final int ROLE_SYSTEM_LINK = 0x1e;
    public static final int ROLE_SYSTEM_TEXT = 0x2a;
    public static final int ROLE_SYSTEM_PUSHBUTTON = 0x2b;
    public static final int ROLE_SYSTEM_CHECKBUTTON = 0x2c;
    public static final int ROLE_SYSTEM_RADIOBUTTON = 0x2d;
	
	
	/**
	 * Wrapper for GetRoleText method
	 * see http://msdn.microsoft.com/en-us/library/ms696193(VS.85).aspx
	 * @param accRole the object role constants
	 * @return the text string
	 */
	public static String getRoleText(int accRole) {
		return _getRoleText(accRole);
	}

	/**
	 * Wrapper for WindowFromAccessibleObject method
	 * see http://msdn.microsoft.com/en-us/library/ms697201(VS.85).aspx
	 * @param ptr the pointer to the IAccessible 
	 * @return the window handle to be retrieved from the pointer
	 */
	public static long WindowFromAccessibleObject(long ptr) {
		return _WindowFromAccessibleObject(ptr);
	}

	/**
	 * Wrapper for AccessibleChildren method
	 * see http://msdn.microsoft.com/en-us/library/ms697243(VS.85).aspx
	 * @param idisp the parent object
	 * @param start the index of the first child
	 * @param count the amount of children to retrieve
	 * @return the array of children
	 */
	public static Object[] getAccessibleChildren(IDispatch idisp, int start, int count) {
		return _getAccessibleChildren(idisp, idisp.getPtr(), start, count);
	} 
	
	/**
	 * Wrapper for AccessibleObjectFromWindow method
	 * see http://msdn.microsoft.com/en-us/library/ms696137(VS.85).aspx
	 * @param hwnd the window handle
	 * @return the pointer to the IAccessible object
	 */
	public static long getAccessibleObjectFromWindow(long hwnd) {
		return _AcessibleObjectFromWindow(hwnd);
	}

	private static native long _WindowFromAccessibleObject(long ptr);
	private static native Object[] _getAccessibleChildren(IDispatch idisp, long ptr, int start, int end);
	private static native String _getRoleText(int accRole);
	private static native long _AcessibleObjectFromWindow(long ptr);

}

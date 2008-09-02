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
	
	
	public static String getRoleText(int accRole) {
		return _getRoleText(accRole);
	}

	public static long WindowFromAccessibleObject(long ptr) {
		return _WindowFromAccessibleObject(ptr);
	}

	public static Object[] getAccessibleChildren(IDispatch idisp, int start, int count) {
		return _getAccessibleChildren(idisp, idisp.getPtr(), start, count);
	} 
	
	public static long getAccessibleObjectFromWindow(long hwnd) {
		return _AcessibleObjectFromWindow(hwnd);
	}

	private static native long _WindowFromAccessibleObject(long ptr);
	private static native Object[] _getAccessibleChildren(IDispatch idisp, long ptr, int start, int end);
	private static native String _getRoleText(int accRole);
	private static native long _AcessibleObjectFromWindow(long ptr);

}

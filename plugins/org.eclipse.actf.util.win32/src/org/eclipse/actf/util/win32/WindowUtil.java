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
import org.eclipse.swt.widgets.Control;



public class WindowUtil {

    public static String GetWindowText(int hWnd) {
        int size = OS.GetWindowTextLength(hWnd);
        if (0 == size) {
            return ""; //$NON-NLS-1$
        }
        TCHAR buffer = new TCHAR(0, size + 1);
        return buffer.toString(0, OS.GetWindowText(hWnd, buffer, buffer.length()));
    }

    public static String GetWindowClassName(int hWnd) {
        TCHAR buffer = new TCHAR(0, 256);
        return buffer.toString(0, OS.GetClassName(hWnd, buffer, buffer.length()));
    }

    public static Rectangle GetWindowRectangle(int hWnd) {
        RECT rect = new RECT();
        OS.GetWindowRect(hWnd, rect);
        return new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }

    public static boolean IsWindowVisible(int hWnd) {
        return OS.IsWindowVisible(hWnd);
    }

    public static int GetDesktopWindow() {
        return OS.GetDesktopWindow();
    }

    public static int GetChildWindow(int hWnd) {
        return OS.GetWindow(hWnd, OS.GW_CHILD);
    }

    public static int GetNextWindow(int hWnd) {
        return OS.GetWindow(hWnd, OS.GW_HWNDNEXT);
    }

    public static int GetOwnerWindow(int hWnd) {
        return OS.GetWindow(hWnd, OS.GW_OWNER);
    }
    
    public static int GetParentWindow(int hWnd) {
        return OS.GetParent(hWnd);
    }
    
    public static boolean isPopupMenu(int hwnd) {
        if( "#32768".equals(GetWindowClassName(hwnd)) ) { //$NON-NLS-1$
            return 0 == GetOwnerWindow(hwnd);
        }
        return false;
    }

    public static void setLayered(Control control, boolean transparent) {
        int ws = OS.GetWindowLongW(control.handle, OS.GWL_EXSTYLE);
        ws |= WindowUtil.WS_EX_LAYERED;
        if (transparent) {
            ws |= OS.WS_EX_TRANSPARENT;
        }
        OS.SetWindowLong(control.handle, OS.GWL_EXSTYLE, ws);
        WindowUtil.SetLayeredWindowAttributes(control.handle, control.getBackground().handle, (char) 0,
                WindowUtil.LWA_COLORKEY);
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

    public static final native int SetLayeredWindowAttributes(int hwnd, int crKey, char bAlpha, int dwFlags);

}

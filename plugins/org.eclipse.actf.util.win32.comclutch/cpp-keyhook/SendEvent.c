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

#include <windows.h>
#include <jni.h>
#include "org_eclipse_actf_util_win32_keyhook_impl_SendEventImpl.h"

jboolean JNICALL
Java_org_eclipse_actf_util_win32_keyhook_impl_SendEventImpl__1postKeyMessage
(JNIEnv *env, jobject thisObj, jlong hwndLong, jint vkey, jboolean isUp)
{
    HWND hwnd;
    UINT message;
    jboolean result;
    UINT scan = MapVirtualKey(vkey, 0);
    LPARAM lParam = (scan & 0xFF) << 16;

    if (isUp) {
	message = WM_KEYUP;
	lParam |= (1 << 29) | (1 << 30) | (1 << 31);
    } else {
	message = WM_KEYDOWN;
    }

    if (hwndLong == 0) {
	hwnd = GetFocus();
    } else {
	hwnd = (HWND) hwndLong;
    }

    stopFilter();
    result = PostMessage(hwnd, message, (WPARAM) vkey, lParam);
    startFilter();
    return result;
}

jboolean JNICALL Java_org_eclipse_actf_util_win32_keyhook_impl_SendEventImpl__1postMouseMessage
(JNIEnv *env, jobject thisObj, jlong hwndLong,
 jint button, jint x, jint y, jboolean isUp, jint flag)
{
    HWND hwnd;
    UINT message;
    WPARAM wParam = 0;
    LPARAM lParam = MAKELPARAM((WORD) x, (WORD) y);
    jboolean result;

    if (isUp) {
	switch (button) {
	  case 0:
	   message = WM_LBUTTONUP;
	   break;
	  case 1:
	   message = WM_MBUTTONUP;
	   break;
	  case 2:
	   message = WM_RBUTTONUP;
	   break;
	  default:
	   return JNI_FALSE;
	}
    } else {
	switch (button) {
	  case 0:
	   message = WM_LBUTTONDOWN;
	   wParam = MK_LBUTTON;
	   break;
	  case 1:
	   message = WM_MBUTTONDOWN;
	   wParam = MK_MBUTTON;
	   break;
	  case 2:
	   message = WM_RBUTTONDOWN;
	   wParam = MK_RBUTTON;
	   break;
	  default:
	   return JNI_FALSE;
	}
    }

    if (hwndLong == 0) {
	hwnd = GetFocus();
    } else {
	hwnd = (HWND) hwndLong;
    }

    {
	POINT pt;
	pt.x = x;
	pt.y = y;
	ClientToScreen(hwnd, &pt);
	SetCursorPos(pt.x, pt.y);
    }

    stopFilter();
    if (!isUp) {
	// Some Flash content detects mouse movement to make buttons accept click.
	// The below code simulates mouse movement by waiting 10ms per each mouse movement. (himi)
	int i;
	int last;
	LPARAM lParam2;
	MSG msg;
	SendMessage(hwnd, WM_MOUSEACTIVATE, (WPARAM) GetActiveWindow(),
		    MAKELPARAM(HTCLIENT, message));
	for (i = 3; i >= 0; i--) {
	    lParam2 = MAKELPARAM((WORD) x + i, (WORD) y);
	    last = GetTickCount() + 10;
	    SendMessage(hwnd, WM_MOUSEACTIVATE, (WPARAM) GetActiveWindow(),
			MAKELPARAM(HTCLIENT, message));
	    SendMessage(hwnd, WM_SETCURSOR, (WPARAM) hwnd,
			MAKELPARAM(HTCLIENT, WM_MOUSEMOVE));
	    PostMessage(hwnd, WM_MOUSEMOVE, 0, lParam2);
	    while (last > GetTickCount()) {
		if (PeekMessage(&msg, NULL, 0, 0, PM_REMOVE)) {
		    TranslateMessage(&msg);
		    DispatchMessage(&msg);
		}
	    }
	}
    }

    result = PostMessage(hwnd, message, wParam, lParam);
    startFilter();
    return result;
}

jboolean JNICALL Java_org_eclipse_actf_util_win32_keyhook_impl_SendEventImpl__1focusWindow
(JNIEnv *env, jobject thisObj, jlong hwndLong)
{
    HWND hwnd = (HWND) hwndLong;
    SetFocus(hwnd);
    return JNI_TRUE;
}

static LPWSTR
convert_java_string(JNIEnv* env, jstring jstr)
{
    LPWSTR str;
    const jchar* jc;
    int len;
    if (!jstr) return NULL;
    len = (*env)->GetStringLength(env, jstr);
    jc = (*env)->GetStringChars(env, jstr, NULL);
    str = (LPWSTR) malloc(sizeof(WCHAR) * (len + 1));
    memcpy(str, jc, sizeof(WCHAR) * len);
    str[len] = 0;
    (*env)->ReleaseStringChars(env, jstr, jc);
    return str;
}

jlong JNICALL
Java_org_eclipse_actf_util_win32_keyhook_impl_SendEventImpl__1findWindow
(JNIEnv* env, jobject o, jstring jclassName, jstring jwindowName)
{
    HWND hwnd;
    LPWSTR className, windowName;
    className = convert_java_string(env, jclassName);
    windowName = convert_java_string(env, jwindowName);
    hwnd = FindWindowW(className, windowName);
    free(className);
    free(windowName);
    return (jlong) hwnd;
}


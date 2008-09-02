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
#include "KeyHook.h"

static HHOOK hhook;
static HWND hmainWnd;

LRESULT CALLBACK
hookedWndProcRet(int code, WPARAM wParam, LPARAM lParam)
{
    if (code == HC_ACTION) {
	CWPRETSTRUCT* ret = (CWPRETSTRUCT*) lParam;
	if (ret->hwnd == hmainWnd) {
#ifdef DEBUG
	    fprintf(stderr, "Window Hooked:%d, %d\n", wParam, ret->message);
	    fflush(stderr);
#endif
	    switch (ret->message) {
	      case WM_ACTIVATE:
	       if (LOWORD(ret->wParam) == WA_INACTIVE) {
		   stopFilter();
	       } else {
		   startFilter();
	       }
	       break;
	      case WM_ENTERMENULOOP:
	       stopFilter();
	       break;
	      case WM_EXITMENULOOP:
	       startFilter();
	       break;
	    }
	}
    }

    return CallNextHookEx(hhook, code, wParam, lParam);
}

void
initialize_WindowHook(HWND hookhwnd)
{
    if (!hhook) {
	HINSTANCE hInst;
	DWORD threadId = GetCurrentThreadId();
	hInst = (HINSTANCE) GetModuleHandle(0);
	hmainWnd = hookhwnd;
	hhook = SetWindowsHookEx(WH_CALLWNDPROCRET, hookedWndProcRet, NULL, threadId);
    }
}

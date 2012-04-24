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

package org.eclipse.actf.model.internal.ui.editors.ie;

import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.COMObject;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.win32.MSG;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class WebBrowserIEControlSite extends OleControlSite {

	static final int OLECMDID_SHOWSCRIPTERROR = 40;
	static final String ABOUT_BLANK = "about:blank"; //$NON-NLS-1$

	private static final int S_OK = COM.S_OK;
	private static final int S_FALSE = COM.S_FALSE;
	private static final int E_INVALIDARG = COM.E_INVALIDARG;
	private static final int E_NOTIMPL = COM.E_NOTIMPL;
	private static final int E_NOINTERFACE = COM.E_NOINTERFACE;
	private static final int E_NOTSUPPORTED = COM.E_NOTSUPPORTED;

	private static final int INET_E_DEFAULT_ACTION = 0x800C0011;
	private static final int URLZONE_INTRANET = 1;
	private static final int URLPOLICY_ALLOW = 0x00;
	private static final int URLPOLICY_DISALLOW = 0x03;
	private static final int URLPOLICY_JAVA_PROHIBIT = 0x0;
	private static final int URLPOLICY_JAVA_LOW = 0x00030000;
	private static final int URLACTION_ACTIVEX_RUN = 0x00001200;
	private static final int URLACTION_JAVA_MIN = 0x00001C00;
	private static final int URLACTION_JAVA_MAX = 0x00001Cff;

	COMObject iDocHostUIHandler;
	COMObject iOleCommandTarget;

	COMObject iServiceProvider;
	COMObject iInternetSecurityManager;
	boolean ignoreNextMessage;

	public WebBrowserIEControlSite(Composite parent, int style, String progId) {
		super(parent, style, progId);
	}

	protected void createCOMInterfaces() {
		super.createCOMInterfaces();
		iDocHostUIHandler = new COMObject(new int[] { 2, 0, 0, 4, 1, 5, 0, 0,
				1, 1, 1, 3, 3, 2, 2, 1, 3, 2 }) {
			public int method0(int[] args) {
				return QueryInterface(args[0], args[1]);
			}

			public int method1(int[] args) {
				return AddRef();
			}

			public int method2(int[] args) {
				return Release();
			}

			public int method3(int[] args) {
				return ShowContextMenu(args[0], args[1], args[2], args[3]);
			}

			public int method4(int[] args) {
				return GetHostInfo(args[0]);
			}

			public int method5(int[] args) {
				return ShowUI(args[0], args[1], args[2], args[3], args[4]);
			}

			public int method6(int[] args) {
				return HideUI();
			}

			public int method7(int[] args) {
				return UpdateUI();
			}

			public int method8(int[] args) {
				return EnableModeless(args[0]);
			}

			public int method9(int[] args) {
				return OnDocWindowActivate(args[0]);
			}

			public int method10(int[] args) {
				return OnFrameWindowActivate(args[0]);
			}

			public int method11(int[] args) {
				return ResizeBorder(args[0], args[1], args[2]);
			}

			public int method12(int[] args) {
				return TranslateAccelerator(args[0], args[1], args[2]);
			}

			public int method13(int[] args) {
				return GetOptionKeyPath(args[0], args[1]);
			}

			public int method14(int[] args) {
				return GetDropTarget(args[0], args[1]);
			}

			public int method15(int[] args) {
				return GetExternal(args[0]);
			}

			public int method16(int[] args) {
				return TranslateUrl(args[0], args[1], args[2]);
			}

			public int method17(int[] args) {
				return FilterDataObject(args[0], args[1]);
			}
		};
		iOleCommandTarget = new COMObject(new int[] { 2, 0, 0, 4, 5 }) {
			public int method0(int[] args) {
				return QueryInterface(args[0], args[1]);
			}

			public int method1(int[] args) {
				return AddRef();
			}

			public int method2(int[] args) {
				return Release();
			}

			public int method3(int[] args) {
				return QueryStatus(args[0], args[1], args[2], args[3]);
			}

			public int method4(int[] args) {
				return Exec(args[0], args[1], args[2], args[3], args[4]);
			}
		};

		iServiceProvider = new COMObject(new int[] { 2, 0, 0, 3 }) {
			public int /* long */method0(int /* long */[] args) {
				return QueryInterface(args[0], args[1]);
			}

			public int /* long */method1(int /* long */[] args) {
				return AddRef();
			}

			public int /* long */method2(int /* long */[] args) {
				return Release();
			}

			public int /* long */method3(int /* long */[] args) {
				return QueryService(args[0], args[1], args[2]);
			}
		};
		iInternetSecurityManager = new COMObject(new int[] { 2, 0, 0, 1, 1, 3,
				4, 8, 7, 3, 3 }) {
			public int /* long */method0(int /* long */[] args) {
				return QueryInterface(args[0], args[1]);
			}

			public int /* long */method1(int /* long */[] args) {
				return AddRef();
			}

			public int /* long */method2(int /* long */[] args) {
				return Release();
			}

			public int /* long */method3(int /* long */[] args) {
				return SetSecuritySite(args[0]);
			}

			public int /* long */method4(int /* long */[] args) {
				return GetSecuritySite(args[0]);
			}

			public int /* long */method5(int /* long */[] args) {
				return MapUrlToZone(args[0], args[1], (int) /* 64 */args[2]);
			}

			public int /* long */method6(int /* long */[] args) {
				return GetSecurityId(args[0], args[1], args[2], args[3]);
			}

			public int /* long */method7(int /* long */[] args) {
				return ProcessUrlAction(args[0], (int) /* 64 */args[1],
						args[2], (int) /* 64 */args[3], args[4],
						(int) /* 64 */args[5], (int) /* 64 */args[6],
						(int) /* 64 */args[7]);
			}

			public int /* long */method8(int /* long */[] args) {
				return QueryCustomPolicy(args[0], args[1], args[2], args[3],
						args[4], (int) /* 64 */args[5], (int) /* 64 */args[6]);
			}

			public int /* long */method9(int /* long */[] args) {
				return SetZoneMapping((int) /* 64 */args[0], args[1],
						(int) /* 64 */args[2]);
			}

			public int /* long */method10(int /* long */[] args) {
				return GetZoneMappings((int) /* 64 */args[0], args[1],
						(int) /* 64 */args[2]);
			}
		};

	}

	protected void disposeCOMInterfaces() {
		super.disposeCOMInterfaces();
		if (iDocHostUIHandler != null) {
			iDocHostUIHandler.dispose();
			iDocHostUIHandler = null;
		}
		if (iOleCommandTarget != null) {
			iOleCommandTarget.dispose();
			iOleCommandTarget = null;
		}

		if (iServiceProvider != null) {
			iServiceProvider.dispose();
			iServiceProvider = null;
		}
		if (iInternetSecurityManager != null) {
			iInternetSecurityManager.dispose();
			iInternetSecurityManager = null;
		}
	}

	protected int QueryInterface(int riid, int ppvObject) {
		int result = super.QueryInterface(riid, ppvObject);
		if (result == S_OK)
			return result;
		if (riid == 0 || ppvObject == 0)
			return E_INVALIDARG;
		GUID guid = new GUID();
		COM.MoveMemory(guid, riid, GUID.sizeof);
		if (COM.IsEqualGUID(guid, COM.IIDIDocHostUIHandler)) {
			MemoryUtil.MoveMemory(ppvObject,
					new int[] { iDocHostUIHandler.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return S_OK;
		}
		if (COM.IsEqualGUID(guid, COM.IIDIOleCommandTarget)) {
			MemoryUtil.MoveMemory(ppvObject,
					new int[] { iOleCommandTarget.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return S_OK;
		}

		if (COM.IsEqualGUID(guid, COM.IIDIServiceProvider)) {
			COM.MoveMemory(ppvObject,
					new int /* long */[] { iServiceProvider.getAddress() },
					OS.PTR_SIZEOF);
			AddRef();
			return COM.S_OK;
		}
		if (COM.IsEqualGUID(guid, COM.IIDIInternetSecurityManager)) {
			COM.MoveMemory(ppvObject,
					new int /* long */[] { iInternetSecurityManager
							.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return COM.S_OK;
		}

		MemoryUtil.MoveMemory(ppvObject, new int[] { 0 }, 4);
		return E_NOINTERFACE;
	}

	protected int AddRef() {
		return super.AddRef();
	}

	protected int Release() {
		return super.Release();
	}

	/* IDocHostUIHandler */

	int EnableModeless(int EnableModeless) {
		return E_NOTIMPL;
	}

	int FilterDataObject(int pDO, int ppDORet) {
		return E_NOTIMPL;
	}

	int GetDropTarget(int pDropTarget, int ppDropTarget) {
		return E_NOTIMPL;
	}

	int GetExternal(int ppDispatch) {
		MemoryUtil.MoveMemory(ppDispatch, new int[] { 0 }, 4);
		return S_FALSE;
	}

	int GetHostInfo(int pInfo) {
		int style = getParent().getParent().getStyle();
		int info = 0x00040000;
		// if ((style & SWT.BORDER) == 0) info |= 0x00200000;
		MemoryUtil.MoveMemory(pInfo + 4, new int[] { info }, 4);
		return S_OK;
	}

	int GetOptionKeyPath(int pchKey, int dw) {
		return E_NOTIMPL;
	}

	int HideUI() {
		return E_NOTIMPL;
	}

	int OnDocWindowActivate(int fActivate) {
		return E_NOTIMPL;
	}

	int OnFrameWindowActivate(int fActivate) {
		return E_NOTIMPL;
	}

	int ResizeBorder(int prcBorder, int pUIWindow, int fFrameWindow) {
		return E_NOTIMPL;
	}

	int ShowContextMenu(int dwID, int ppt, int pcmdtReserved, int pdispReserved) {
		/* Show default IE popup menu */
		return S_FALSE;
	}

	int ShowUI(int dwID, int pActiveObject, int pCommandTarget, int pFrame,
			int pDoc) {
		return E_NOTIMPL;
	}

	int TranslateAccelerator(int lpMsg, int pguidCmdGroup, int nCmdID) {
		/*
		 * Handle menu accelerator
		 */
		Menu menubar = getShell().getMenuBar();
		if (menubar != null && !menubar.isDisposed() && menubar.isEnabled()) {
			Shell shell = menubar.getShell();
			int hwnd = shell.handle;
			int hAccel = OS.SendMessage(hwnd, OS.WM_APP + 1, 0, 0);
			if (hAccel != 0) {
				MSG msg = new MSG();
				OS.MoveMemory(msg, lpMsg, MSG.sizeof);
				if (OS.TranslateAccelerator(hwnd, hAccel, msg) != 0) {
					return S_OK;
				}
			}
		}
		/*
		 * Block Ctrl+N (New window)
		 */
		MSG msg = new MSG();
		OS.MoveMemory(msg, lpMsg, MSG.sizeof);
		if (msg.message == OS.WM_KEYDOWN && msg.wParam == OS.VK_N
				&& OS.GetKeyState(OS.VK_CONTROL) < 0) {
			return S_OK;
		}
		return S_FALSE;
	}

	int TranslateUrl(int dwTranslate, int pchURLIn, int ppchURLOut) {
		return E_NOTIMPL;
	}

	int UpdateUI() {
		return E_NOTIMPL;
	}

	/* IOleCommandTarget */
	int QueryStatus(int pguidCmdGroup, int cCmds, int prgCmds, int pCmdText) {
		return E_NOTSUPPORTED;
	}

	int Exec(int pguidCmdGroup, int nCmdID, int nCmdExecOpt, int pvaIn,
			int pvaOut) {
		/*
		 * Disable script error dialog.
		 */
		// System.out.println("pguidCmdGroup="+pguidCmdGroup+", nCmdID="+nCmdID+", nCmdExecOpt="+nCmdExecOpt+", pvaIn="+pvaIn+", pvaOut="+pvaOut);
		if (pguidCmdGroup != 0 && nCmdID == OLECMDID_SHOWSCRIPTERROR) {
			boolean bDisable = ((WebBrowserIEComposite) (getParent()
					.getParent())).bDisableScriptDebugger;
			if (bDisable) {
				GUID guid = new GUID();
				COM.MoveMemory(guid, pguidCmdGroup, GUID.sizeof);
				if (COM.IsEqualGUID(guid, COM.CGID_DocHostCommandHandler)) {
					return S_OK;
				}
			}
		}
		return E_NOTSUPPORTED;
	}

	/* IServiceProvider */

	int QueryService(int /* long */guidService, int /* long */riid,
			int /* long */ppvObject) {
		if (riid == 0 || ppvObject == 0)
			return COM.E_INVALIDARG;
		GUID guid = new GUID();
		COM.MoveMemory(guid, riid, GUID.sizeof);
		if (COM.IsEqualGUID(guid, COM.IIDIInternetSecurityManager)) {
			COM.MoveMemory(ppvObject,
					new int /* long */[] { iInternetSecurityManager
							.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return COM.S_OK;
		}
		COM.MoveMemory(ppvObject, new int /* long */[] { 0 }, OS.PTR_SIZEOF);
		return COM.E_NOINTERFACE;
	}

	/* IInternetSecurityManager */

	int SetSecuritySite(int /* long */pSite) {
		return INET_E_DEFAULT_ACTION;
	}

	int GetSecuritySite(int /* long */ppSite) {
		return INET_E_DEFAULT_ACTION;
	}

	int MapUrlToZone(int /* long */pwszUrl, int /* long */pdwZone, int dwFlags) {
		// TODO about:blank and non trusted text case
		// COM.MoveMemory(pdwZone, new int[] { URLZONE_INTRANET }, 4);
		// return COM.S_OK;
		
		return INET_E_DEFAULT_ACTION;
	}

	int GetSecurityId(int /* long */pwszUrl, int /* long */pbSecurityId,
			int /* long */pcbSecurityId, int /* long */dwReserved) {
		return INET_E_DEFAULT_ACTION;
	}

	int ProcessUrlAction(int /* long */pwszUrl, int dwAction,
			int /* long */pPolicy, int cbPolicy, int /* long */pContext,
			int cbContext, int dwFlags, int dwReserved) {
		ignoreNextMessage = false;

		int policy = URLPOLICY_ALLOW;

		if (dwAction >= URLACTION_JAVA_MIN && dwAction <= URLACTION_JAVA_MAX) {
			// policy = URLPOLICY_JAVA_PROHIBIT;
			policy = URLPOLICY_JAVA_LOW;
			ignoreNextMessage = true;
		}

		if (dwAction == URLACTION_ACTIVEX_RUN) {
			GUID guid = new GUID();
			COM.MoveMemory(guid, pContext, GUID.sizeof);
			if (COM.IsEqualGUID(guid, COM.IIDJavaBeansBridge)
					|| COM.IsEqualGUID(guid, COM.IIDShockwaveActiveXControl)) {
				policy = URLPOLICY_DISALLOW;
				ignoreNextMessage = true;
			}
		}
		if (cbPolicy >= 4)
			COM.MoveMemory(pPolicy, new int[] { policy }, 4);
		return policy == URLPOLICY_ALLOW ? COM.S_OK : COM.S_FALSE;
	}

	int QueryCustomPolicy(int /* long */pwszUrl, int /* long */guidKey,
			int /* long */ppPolicy, int /* long */pcbPolicy,
			int /* long */pContext, int cbContext, int dwReserved) {
		return INET_E_DEFAULT_ACTION;
	}

	int SetZoneMapping(int dwZone, int /* long */lpszPattern, int dwFlags) {
		return INET_E_DEFAULT_ACTION;
	}

	int GetZoneMappings(int dwZone, int /* long */ppenumString, int dwFlags) {
		return COM.E_NOTIMPL;
	}

}

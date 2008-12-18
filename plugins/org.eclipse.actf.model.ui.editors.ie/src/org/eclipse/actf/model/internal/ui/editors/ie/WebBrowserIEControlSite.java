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
    COMObject iDocHostUIHandler;
    COMObject iOleCommandTarget;

    static final int OLECMDID_SHOWSCRIPTERROR = 40;
    
    static final String ABOUT_BLANK = "about:blank"; //$NON-NLS-1$
    
    public WebBrowserIEControlSite(Composite parent, int style, String progId) {
        super(parent, style, progId);
    }

    protected void createCOMInterfaces() {
        super.createCOMInterfaces();
        iDocHostUIHandler = new COMObject(new int[]{2, 0, 0, 4, 1, 5, 0, 0, 1, 1, 1, 3, 3, 2, 2, 1, 3, 2}){
            public int method0(int[] args) {return QueryInterface(args[0], args[1]);}
            public int method1(int[] args) {return AddRef();}
            public int method2(int[] args) {return Release();}
            public int method3(int[] args) {return ShowContextMenu(args[0], args[1], args[2], args[3]);}
            public int method4(int[] args) {return GetHostInfo(args[0]);}
            public int method5(int[] args) {return ShowUI(args[0], args[1], args[2], args[3], args[4]);}
            public int method6(int[] args) {return HideUI();}
            public int method7(int[] args) {return UpdateUI();}
            public int method8(int[] args) {return EnableModeless(args[0]);}
            public int method9(int[] args) {return OnDocWindowActivate(args[0]);}
            public int method10(int[] args) {return OnFrameWindowActivate(args[0]);}
            public int method11(int[] args) {return ResizeBorder(args[0], args[1], args[2]);}
            public int method12(int[] args) {return TranslateAccelerator(args[0], args[1], args[2]);}
            public int method13(int[] args) {return GetOptionKeyPath(args[0], args[1]);}
            public int method14(int[] args) {return GetDropTarget(args[0], args[1]);}
            public int method15(int[] args) {return GetExternal(args[0]);}
            public int method16(int[] args) {return TranslateUrl(args[0], args[1], args[2]);}       
            public int method17(int[] args) {return FilterDataObject(args[0], args[1]);}
        };
        iOleCommandTarget = new COMObject(new int[]{2, 0, 0, 4, 5}) {
            public int method0(int[] args) {return QueryInterface(args[0], args[1]);}
            public int method1(int[] args) {return AddRef();}
            public int method2(int[] args) {return Release();}      
            public int method3(int[] args) {return QueryStatus(args[0], args[1], args[2], args[3]);}        
            public int method4(int[] args) {return Exec(args[0], args[1], args[2], args[3], args[4]);}
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
    }

    protected int QueryInterface(int riid, int ppvObject) {
        int result = super.QueryInterface(riid, ppvObject);
        if (result == COM.S_OK) return result;
        if (riid == 0 || ppvObject == 0) return COM.E_INVALIDARG;
        GUID guid = new GUID();
        COM.MoveMemory(guid, riid, GUID.sizeof);
        if (COM.IsEqualGUID(guid, COM.IIDIDocHostUIHandler)) {
            COM.MoveMemory(ppvObject, new int[] {iDocHostUIHandler.getAddress()}, 4);
            AddRef();
            return COM.S_OK;
        }
        if (COM.IsEqualGUID(guid, COM.IIDIOleCommandTarget)) {
            COM.MoveMemory(ppvObject, new int[] {iOleCommandTarget.getAddress()}, 4);
            AddRef();
            return COM.S_OK;
        }
        COM.MoveMemory(ppvObject, new int[] {0}, 4);
        return COM.E_NOINTERFACE;
    }

    protected int AddRef() {
        return super.AddRef();
    }

    protected int Release() {
        return super.Release();
    }

    /* IDocHostUIHandler */

    int EnableModeless(int EnableModeless) {
        return COM.E_NOTIMPL;
    }

    int FilterDataObject(int pDO, int ppDORet) {
        return COM.E_NOTIMPL;
    }

    int GetDropTarget(int pDropTarget, int ppDropTarget) {
        return COM.E_NOTIMPL;
    }

    int GetExternal(int ppDispatch) {
        MemoryUtil.MoveMemory(ppDispatch, new int[] {0}, 4);
        return COM.S_FALSE;
    }

    int GetHostInfo(int pInfo) {
        int style = getParent().getParent().getStyle();
        int info = 0x00040000;
//        if ((style & SWT.BORDER) == 0) info |= 0x00200000;
        MemoryUtil.MoveMemory(pInfo + 4, new int[] {info}, 4);
        return COM.S_OK;
    }

    int GetOptionKeyPath(int pchKey, int dw) {
        return COM.E_NOTIMPL;
    }

    int HideUI() {
        return COM.E_NOTIMPL;
    }

    int OnDocWindowActivate(int fActivate) {
        return COM.E_NOTIMPL;
    }

    int OnFrameWindowActivate(int fActivate) {
        return COM.E_NOTIMPL;
    }

    int ResizeBorder(int prcBorder, int pUIWindow, int fFrameWindow) {
        return COM.E_NOTIMPL;
    }

    int ShowContextMenu(int dwID, int ppt, int pcmdtReserved, int pdispReserved) {
        /* Show default IE popup menu */
        return COM.S_FALSE;
    }

    int ShowUI(int dwID, int pActiveObject, int pCommandTarget, int pFrame, int pDoc) {
        return COM.E_NOTIMPL;
    }

    int TranslateAccelerator(int lpMsg, int pguidCmdGroup, int nCmdID) {
        /*
         * Handle menu accelerator
         */
        Menu menubar = getShell().getMenuBar();
        if (menubar != null && !menubar.isDisposed() && menubar.isEnabled()) {
            Shell shell = menubar.getShell();
            int hwnd = shell.handle;
            int hAccel = OS.SendMessage(hwnd, OS.WM_APP+1, 0, 0);
            if (hAccel != 0) {
                MSG msg = new MSG();
                OS.MoveMemory(msg, lpMsg, MSG.sizeof);
                if (OS.TranslateAccelerator(hwnd, hAccel, msg) != 0) {
                    return COM.S_OK;
                }
            }
        }
        /*
         * Block Ctrl+N (New window)
         */
        MSG msg = new MSG();
        OS.MoveMemory(msg, lpMsg, MSG.sizeof);
        if (msg.message == OS.WM_KEYDOWN && msg.wParam == OS.VK_N && OS.GetKeyState (OS.VK_CONTROL) < 0) {
            return COM.S_OK;
        }
        return COM.S_FALSE;
    }

    int TranslateUrl(int dwTranslate, int pchURLIn, int ppchURLOut) {
        return COM.E_NOTIMPL;
    }

    int UpdateUI() {
        return COM.E_NOTIMPL;
    }

    /* IOleCommandTarget */
    int QueryStatus(int pguidCmdGroup, int cCmds, int prgCmds, int pCmdText) {
        return COM.E_NOTSUPPORTED;
    }

    int Exec(int pguidCmdGroup, int nCmdID, int nCmdExecOpt, int pvaIn, int pvaOut) {
        /*
         * Disable script error dialog.
         */
//        System.out.println("pguidCmdGroup="+pguidCmdGroup+", nCmdID="+nCmdID+", nCmdExecOpt="+nCmdExecOpt+", pvaIn="+pvaIn+", pvaOut="+pvaOut);
        if (pguidCmdGroup != 0 && nCmdID == OLECMDID_SHOWSCRIPTERROR) {
            boolean bDisable = ((WebBrowserIEComposite)(getParent().getParent())).bDisableScriptDebugger;
            if( bDisable ) {
                GUID guid = new GUID();
                COM.MoveMemory(guid, pguidCmdGroup, GUID.sizeof);
                if (COM.IsEqualGUID(guid, COM.CGID_DocHostCommandHandler)) {
                    return COM.S_OK;
                }
            }
        }
        return COM.E_NOTSUPPORTED;
    }
}

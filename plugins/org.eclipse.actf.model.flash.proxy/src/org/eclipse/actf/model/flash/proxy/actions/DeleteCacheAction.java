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
package org.eclipse.actf.model.flash.proxy.actions;

import org.eclipse.actf.model.flash.proxy.Messages;
import org.eclipse.actf.model.flash.proxy.ProxyPlugin;
import org.eclipse.actf.model.flash.proxy.internal.WSTR;
import org.eclipse.actf.model.flash.proxy.internal.WinInet;
import org.eclipse.actf.util.ui.ProgressContribution;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


public class DeleteCacheAction implements IWorkbenchWindowActionDelegate {

    private static final int BUFFER_SIZE = 32 * 1024;// 4096;

    private IWorkbenchWindow window;

    private static final String NEVER_DELETE = Messages.getString("NO_DELETE"); //$NON-NLS-1$

    private boolean background = false;

    public DeleteCacheAction() {
        this(false);
    }

    public DeleteCacheAction(boolean background) {
        this.background = background;
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    private ProgressContribution pc;

    private IStatusLineManager manager;

    public void run(IAction action) {
        try {
            ProxyPlugin.cacheChecked = true;
            ProgressMonitorDialog dialog;

            if (background) {
                initProgress();
                new Thread(new Runnable() {
                    public void run() {
                        deleteCacheEntries();
                        endProgress();
                    }
                }).start();
            } else {
                if (window != null) {
                    dialog = new ProgressMonitorDialog(window.getShell());
                    dialog.run(true, true, new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) {
                            int total = countCacheEntries();
                            monitor.beginTask(Messages.getString("proxy.deleting_cache"), total); //$NON-NLS-1$
                            deleteCacheEntries(monitor, total);
                            monitor.done();
                        }
                    });
                } else {
                    deleteCacheEntries();
                }
            }
        } catch (Exception e) {
        }
    }

    private void initProgress() {
        try {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    getProgressContribution();
                    if (pc != null && manager != null) {
                        pc.setVisible(true);
                        pc.setText("Deleting Internet Cache");
                        manager.update(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProgressContribution() {
        String id = ProgressContribution.PROGRESS_CONTRIBUTION_ID;
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IViewPart part = window.getActivePage().getViewReferences()[0].getView(false);
        IViewSite viewSite = part.getViewSite();
        IActionBars actionBars = viewSite.getActionBars();
        manager = actionBars.getStatusLineManager();
        pc = (ProgressContribution) manager.find(id);
    }

    private void endProgress() {
        try {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if (pc != null && manager != null) {
                        pc.setVisible(false);
                        manager.update(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    private int countCacheEntries() {
        int count = 1;
        int pCacheEntry = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, BUFFER_SIZE);
        try {
            int[] pSize = new int[] { BUFFER_SIZE };
            OS.MoveMemory(pCacheEntry, pSize, 4);
            int hEnum = WinInet.FindFirstUrlCacheEntryW(0, pCacheEntry, pSize);
            if (0 != hEnum) {
                while (true) {
                    int[] pEntries = new int[20];
                    OS.MoveMemory(pEntries, pCacheEntry, 4 * pEntries.length);
                    if (0 == (pEntries[3] & 0x00300000)) { // Skip Cookie & History
                        String localFileName = new WSTR(pEntries[2]).getString();
                        boolean skip = false;
                        if (localFileName.indexOf('?') < 0) {
                            int pos = localFileName.lastIndexOf('.');
                            if (-1 != pos) {
                                String ext = localFileName.substring(pos + 1).toLowerCase();
                                skip = -1 != NEVER_DELETE.indexOf("|" + ext + "|"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }
                        if (!skip) {
                            count++;
                        }
                    }
                    pSize[0] = BUFFER_SIZE;
                    if (!WinInet.FindNextUrlCacheEntryW(hEnum, pCacheEntry, pSize)) {
                        break;
                    }
                }
                WinInet.FindCloseUrlCache(hEnum);
            }
        } finally {
            OS.GlobalFree(pCacheEntry);
        }
        return count;
    }

    private void deleteCacheEntries() {
        deleteCacheEntries(null, 0);
    }

    private void deleteCacheEntries(IProgressMonitor monitor, int total) {
        int count = 0;

        int pCacheEntry = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, BUFFER_SIZE);
        try {
            int[] pSize = new int[] { BUFFER_SIZE };
            OS.MoveMemory(pCacheEntry, pSize, 4);
            int hEnum = WinInet.FindFirstUrlCacheEntryW(0, pCacheEntry, pSize);
            if (0 != hEnum) {
                while (monitor == null || !monitor.isCanceled()) {
                    int[] pEntries = new int[20];
                    OS.MoveMemory(pEntries, pCacheEntry, 4 * pEntries.length);
                    if (0 == (pEntries[3] & 0x00300000)) { // Skip Cookie & History
                        String localFileName = new WSTR(pEntries[2]).getString();
                        boolean skip = false;
                        if (localFileName.indexOf('?') < 0) {
                            int pos = localFileName.lastIndexOf('.');
                            if (-1 != pos) {
                                String ext = localFileName.substring(pos + 1).toLowerCase();
                                skip = -1 != NEVER_DELETE.indexOf("|" + ext + "|"); //$NON-NLS-1$ //$NON-NLS-2$
                                // if( skip ) System.out.print(ext+"|");
                            }
                        }
                        if (!skip) {
                            WSTR wsSourceUrlName = new WSTR(pEntries[1]);

                            count++;

                            if (monitor != null){
                                monitor.subTask(wsSourceUrlName.getString());
                                monitor.worked(1);
                            }
                            
                            //System.out.println("Delete: " + localFileName); //$NON-NLS-1$
                            WinInet.DeleteUrlCacheEntryW(wsSourceUrlName.getAddress());

                            if (background && total>0) {
                                changeProgress(count * 100 / total);
                            }
                        }
                    }
                    pSize[0] = BUFFER_SIZE;
                    if (!WinInet.FindNextUrlCacheEntryW(hEnum, pCacheEntry, pSize)) {
                        break;
                    }
                }
                WinInet.FindCloseUrlCache(hEnum);
            }
        } finally {
            OS.GlobalFree(pCacheEntry);
        }

    }

    private void changeProgress(final int value) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (pc != null && manager != null) {
                    pc.setValue(value);
                    manager.update(true);
                }
            }
        });
    }

}

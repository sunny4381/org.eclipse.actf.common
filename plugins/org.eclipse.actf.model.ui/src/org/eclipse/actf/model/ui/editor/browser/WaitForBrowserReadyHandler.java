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

package org.eclipse.actf.model.ui.editor.browser;

import org.eclipse.actf.util.timer.WaitExecSyncEventHandler;
import org.eclipse.swt.widgets.Display;




public class WaitForBrowserReadyHandler implements WaitExecSyncEventHandler {
    public static double INTERVAL = 0.5;

    private IWebBrowserACTF browser;

    private Runnable runnable;

    private boolean waitActive;

    private double timeout;

    public WaitForBrowserReadyHandler(IWebBrowserACTF browser, double timeout, boolean waitActive, Runnable runnable) {
        this.browser = browser;
        this.runnable = runnable;
        this.waitActive = waitActive;
        this.timeout = timeout;
    }

    public double getInterval() {
        return INTERVAL;
    }

    public boolean canRun(double elapsed) {
        try {
            // System.out.print("[" + browser.getReadyState() + "]");
            if (browser.isReady() || elapsed > timeout) {
                return !waitActive || null != Display.getCurrent().getActiveShell();
            }
        }
        catch( Exception e ) {
        }
        return false;
    }

    public void run() {
        runnable.run();
    }
}

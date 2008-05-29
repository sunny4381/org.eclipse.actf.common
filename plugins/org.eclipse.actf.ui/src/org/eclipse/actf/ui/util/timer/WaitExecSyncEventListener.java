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

package org.eclipse.actf.ui.util.timer;




public class WaitExecSyncEventListener implements SyncEventListener {

    private WeakSyncTimer timer = WeakSyncTimer.getTimer();

    private WaitExecSyncEventHandler handler;

    private long start;

    private double interval;

    public WaitExecSyncEventListener(WaitExecSyncEventHandler handler) {
        super();
        this.handler = handler;
        startListener();
    }

    public void startListener() {
        start = System.currentTimeMillis();
        interval = handler.getInterval();
        timer.addEventListener(this);
    }
    
    public void stopListener() {
        interval = 0;
        timer.removeEventListener(this);
    }
    
    public double getInterval() {
        return interval;
    }

    public void run() {
        if (0 != interval && handler.canRun((double) (System.currentTimeMillis() - start) / 1000)) {
            stopListener();
            handler.run();
        }
    }
}

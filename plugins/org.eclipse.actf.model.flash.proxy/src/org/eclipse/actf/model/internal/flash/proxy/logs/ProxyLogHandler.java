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

package org.eclipse.actf.model.internal.flash.proxy.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.actf.model.internal.flash.proxy.ProxyPlugin;
import org.eclipse.actf.model.internal.flash.proxy.ui.views.ProxyLogView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;



public class ProxyLogHandler extends ConsoleHandler {

    private static List logRecordList = new ArrayList();
    private static int maxRecordCount = 1000;
    private static Handler handler = null;
    private static Logger logger = LogManager.getLogManager().getLogger(""); //$NON-NLS-1$

    private static Display display = ProxyPlugin.getDefault().getWorkbench().getDisplay();
    private static Level logLevel = Level.INFO;

    public static void configure() {
        
        // Remove current console handler 
        Handler[] handlers = logger.getHandlers();
        for( int i=0; i<handlers.length; i++ ) {
            if( handlers[i] instanceof ConsoleHandler ) {
                logger.removeHandler(handlers[i]);
            }
        }
        
        // Add this class
        if( null == handler ) {
            handler = new ProxyLogHandler();
        }
        logger.addHandler(handler);
        
        // Adjust logging level
        setLogLevel(logLevel);
    }
    
    public static void removeHandler() {
        if( null != handler ) {
            Logger logger = LogManager.getLogManager().getLogger(""); //$NON-NLS-1$
            logger.removeHandler(handler);
        }
    }
    
    public static Handler getHandler() {
        return handler;
    }
    
    public void publish(LogRecord record) {
//        super.publish(record);
        if (!isLoggable(record) || display.isDisposed() ) {
            return;
        }

        // Add LogRecord to the list
        while( logRecordList.size() >= maxRecordCount ) {
            logRecordList.remove(0);
        }
        logRecordList.add(new ProxyLogRecord(record));
        
        // Refresh log view
        display.asyncExec(new Runnable(){
            public void run() {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if( null == window ) return;
                IWorkbenchPage page = window.getActivePage();
                if( null != page ) {
                    IViewPart part = page.findView(ProxyLogView.ID);
                    if( part instanceof ProxyLogView ) {
                        ((ProxyLogView)part).refresh();
                    }
                }
            }
        });
    }
    
    public static List getLogs() {
        return logRecordList;
    }
    
    public static void clear() {
        logRecordList.clear();
    }
    
    public static void setLogLevel(Level newLevel) {
        logLevel = newLevel;
        if( null != handler ) {
            handler.setLevel(logLevel);
            logger.setLevel(logLevel);
        }
    }
    
    public static void resetLogLevel() {
    	setLogLevel(logLevel);
    }
    
    public static void setMaxLogCount(int maxCount) {
        maxRecordCount = maxCount;
    }
    
    public static int getMaxLogCount() {
        return maxRecordCount;
    }
}

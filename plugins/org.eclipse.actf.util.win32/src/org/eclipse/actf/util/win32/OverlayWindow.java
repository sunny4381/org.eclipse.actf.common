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

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



public class OverlayWindow implements Runnable {

    public static final String WINDOW_TEXT = "[Overlay Window]"; //$NON-NLS-1$
    public static final int INDEX_HIGHLIGHT = 0;
    public static final int INDEX_LABELS = 1;
    private static boolean visible = true;
    
    private Shell shell;

    private Display display;

    private static OverlayWindow[] instance = new OverlayWindow[2];

    public OverlayWindow(int index) {
        // Create OverlayWindow shell
        shell = new Shell(SWT.MODELESS | SWT.NO_TRIM | SWT.ON_TOP);
        shell.setText(WINDOW_TEXT);
        shell.setBackground(shell.getBackground()); // Fix for color change
        display = shell.getDisplay();
        Shell activeShell = display.getActiveShell();
        if( null != activeShell ) {
            activeShell.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    shell.dispose();
                }
            });
        }

        // Make shell window transparent
        WindowUtil.setLayered(shell, INDEX_HIGHLIGHT==index);

        // Init the shell
        shell.setLayout(new GridLayout());
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rect = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        shell.setBounds(0, 0, rect.width, rect.height);
    	//shell.setBounds(0, 0, OS.GetSystemMetrics(OS.SM_CXSCREEN), OS.GetSystemMetrics(OS.SM_CYSCREEN));
        shell.setVisible(visible);

        //
        if( INDEX_LABELS == index ) {
            shell.addShellListener(new ShellAdapter(){
                public void shellDeactivated(ShellEvent e) {
                    OverlayLabel.removeAll();
                }
            });
        }
    }
    
    public static OverlayWindow getInstance(int index, boolean create) {
        if( index < instance.length ) {
            if (create && null == instance[index]) {
                instance[index] = new OverlayWindow(index);
            }
            return instance[index];
        }
        return null;
    }
    
    public static boolean getVisible() {
    	return visible;
    }
    
    public static void setVisible(boolean newVisible) {
    	if( visible != newVisible ) {
    		visible = newVisible;
    		for( int i=0; i<instance.length; i++ ) {
    			if( null != instance[i] ) {
        			instance[i].shell.setVisible(visible);
    			}
    		}
    	}
    }

    public Composite getComposite() {
        return shell;
    }
    
    public void run() {
        if (!shell.isDisposed()) {
            Control[] children = shell.getChildren();
            for( int i=0; i<children.length; i++ ) {
                if( children[i] instanceof IIntervalExec ) {
                    int nextInterval = ((IIntervalExec)children[i]).exec();
                    if( nextInterval > 0 ) {
                        display.timerExec(nextInterval, this);
                    }
                }
            }
        }
    }
}

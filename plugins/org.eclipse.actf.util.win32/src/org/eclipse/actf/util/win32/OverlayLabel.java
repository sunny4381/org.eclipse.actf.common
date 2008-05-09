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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;


public class OverlayLabel extends CLabel {

    public Object associatedObject;
    
    private Menu popupMenu;
    
    private static String ownerId = null;
    
    public OverlayLabel(Composite parent, int style) {
        super(parent, style);
        setForeground(getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        setBackground(getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        setLayoutData(new GridData());
        addPaintListener(new PaintListener(){
            public void paintControl(PaintEvent event) {
                Rectangle rect = getClientArea();
                event.gc.drawRectangle(rect.x,rect.y,rect.width-1,rect.height-1);
            }
        });
    }
    
    public static void removeAll() {
        removeAll(true);
    }
    
    public static void removeAll(boolean force) {
        final Composite composite = getOverlayComposite(false);
        if( null != composite ) {
            if( force ) {
                removeChildren(composite);
            }
            else {
                composite.getDisplay().asyncExec(new Runnable(){
                    public void run() {
                        if( !composite.isDisposed() && null == composite.getDisplay().getActiveShell() ) {
                            removeChildren(composite);
                        }
                    }
                });
            }
        }
    }
    
    private static void removeChildren(Composite composite) {
        Control[] controls = composite.getChildren();
        for( int i=0; i<controls.length; i++ ) {
            if( controls[i] instanceof OverlayLabel ) {
                controls[i].dispose();
            }
        }
    }
    
    public static OverlayLabel create(Object associatedObject) {
        Composite composite = getOverlayComposite(true);
        composite.moveAbove(null);
        Control[] controls = composite.getChildren();
        for( int i=0; i<controls.length; i++ ) {
            if( controls[i] instanceof OverlayLabel ) {
                if( associatedObject.equals(((OverlayLabel)controls[i]).associatedObject) ) {
                    return null;
                }
            }
        }
        OverlayLabel label = new OverlayLabel(composite,SWT.NONE);
        label.associatedObject = associatedObject;
        return label;
    }
    
    public static OverlayLabel[] getLabelsAtPosition(Point position) {
        List<OverlayLabel> labelList = new ArrayList<OverlayLabel>();
        Composite composite = getOverlayComposite(false);
        if( null != composite ) {
            Control[] controls = composite.getChildren();
            for( int i=0; i<controls.length; i++ ) {
                if( controls[i] instanceof OverlayLabel ) {
                    if( controls[i].getBounds().contains(position) ) {
                        labelList.add((OverlayLabel) controls[i]);
                    }
                }
            }
        }
        return (OverlayLabel[])labelList.toArray(new OverlayLabel[labelList.size()]);
    }

    public static Composite getOverlayComposite(boolean create) {
        OverlayWindow window = OverlayWindow.getInstance(OverlayWindow.INDEX_LABELS,create);
        if( null != window ) {
            return window.getComposite();
        }
        return null;
    }

    public Menu createPopupMenu() {
        if( null != popupMenu && !popupMenu.isDisposed() ) {
            popupMenu.dispose();
        }
        popupMenu = new Menu(this);
        return popupMenu;
    }
    
    public void dispose() {
        if( null != popupMenu && !popupMenu.isDisposed() ) {
            popupMenu.dispose();
        }
        super.dispose();
    }

    public static String getOwnerId() {
        return ownerId;
    }

    public static void setOwnerId(String id) {
        ownerId = id;
    }
    
    public static final String MENU_SEPARATOR = "\n----------------"; //$NON-NLS-1$

    public void setTooltop(String text, String[][] properties) {
    	text += MENU_SEPARATOR;
    	for( int i=0; i<properties.length; i++ ) {
            if( properties[i][1].length()>0 ) {
                text += "\n"+properties[i][0]+": "+properties[i][1]; //$NON-NLS-1$ //$NON-NLS-2$
            }
    	}
        setToolTipText(text);
    }
    
    public String getMenuText() {
        String text = getToolTipText();
        int sep = text.indexOf(MENU_SEPARATOR);
        if( sep > 0 ) {
        	text = text.substring(0,sep);
        }
        text = text.replaceAll("\n"," | "); //$NON-NLS-1$ //$NON-NLS-2$
        return text;
    	
    }
}

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

package org.eclipse.actf.model.internal.ui.editors.ie.events.impl;

import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleEvent;
import org.eclipse.swt.ole.win32.Variant;



public abstract class AbstractEventParameters {

    protected OleEvent event;
    
    protected AbstractEventParameters(OleEvent event) {
        this.event = event;
    }
    
    protected Variant getVariant(int index) {
        try {
            return event.arguments[index];
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected String getString(int index) {
        try {
            return event.arguments[index].getString();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected int getInteger(int index) {
        try {
            return event.arguments[index].getInt();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }
    
    protected boolean getBoolean(int index) {
        try {
            return event.arguments[index].getBoolean();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }
    
    protected void setBooleanByRef(int index, boolean value) {
        try {
            event.arguments[index].setByRef(value);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    protected int getIntegerByRef(int index) {
        try {
            int byRef = event.arguments[index].getByRef();
            if( 0 != byRef ) {
                int[] pValue = new int[1];
                MemoryUtil.MoveMemory(pValue, byRef, 4);
                return pValue[0];
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }
    
    protected void setIntegerByRef(int index, int value) {
        try {
            event.arguments[index].setByRef(value);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    protected int getDispatchAddress(int index) {
        try {
            return event.arguments[index].getDispatch().getAddress();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }
    
    protected int getControlSiteAddress() {
        if( event.widget instanceof OleControlSite ) {
            Variant varWebSite = new Variant(new OleAutomation((OleControlSite)(event.widget)));
            try {
                return varWebSite.getDispatch().getAddress();
            }
            finally {
                varWebSite.dispose();
            }
        }
        return 0;
    }
}

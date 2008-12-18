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

package org.eclipse.actf.model.flash.proxy.internal;

import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.swt.internal.win32.OS;



public class WSTR {

    private int address = 0;
    private String string = null;
    
    public WSTR() {
    }
    
    public WSTR(String newString) {
        setString(newString);
    }
    
    public WSTR(int newAddress) {
        setData(newAddress);
    }
    
    public void setString(String newString) {
        dispose();
        string = newString;
        if( null != string ) {
            char[] buffer = (string + "\0").toCharArray(); //$NON-NLS-1$
            address = MemoryUtil.GlobalAlloc(buffer.length*2);
            MemoryUtil.MoveMemory(address,buffer,buffer.length*2);
        }
    }
    
    public String getString() {
        return string;
    }
    
    public void setData(int newAddress) {
        if( address != newAddress ) {
            dispose();
            address = newAddress;
        }
        string = null;
        if( 0 != address ) {
            int size = OS.wcslen(address);
            if (size > 0) {
                char[] buffer = new char[size];
                MemoryUtil.MoveMemory(buffer, address, size*2);
                string = new String(buffer);
            }
        }
        
    }
    
    public int getAddress() {
        return address;
    }
    
    public void dispose() {
        if( 0 != address ) {
        	MemoryUtil.GlobalFree(address);
            address = 0;
        }
    }
    
}

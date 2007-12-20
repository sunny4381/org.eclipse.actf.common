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

import org.eclipse.swt.internal.win32.OS;



public class INTERNET_PER_CONN_OPTION {
    public int dwOption;     
    public int dwValue;
    public WSTR strValue = new WSTR();
    public static final int sizeof = 12; // +4 for FILETIME
    
    //
    //  Options used in INTERNET_PER_CONN_OPTON struct
    //
    public static final int INTERNET_PER_CONN_FLAGS                        = 1;
    public static final int INTERNET_PER_CONN_PROXY_SERVER                 = 2;
    public static final int INTERNET_PER_CONN_PROXY_BYPASS                 = 3;
    public static final int INTERNET_PER_CONN_AUTOCONFIG_URL               = 4;
    public static final int INTERNET_PER_CONN_AUTODISCOVERY_FLAGS          = 5;
    public static final int INTERNET_PER_CONN_AUTOCONFIG_SECONDARY_URL     = 6;
    public static final int INTERNET_PER_CONN_AUTOCONFIG_RELOAD_DELAY_MINS = 7;
    public static final int INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_TIME  = 8;
    public static final int INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_URL   = 9;

    //
    //     PER_CONN_FLAGS
    //
    public static final int PROXY_TYPE_DIRECT         = 0x00000001;   // direct to net
    public static final int PROXY_TYPE_PROXY          = 0x00000002;   // via named proxy
    public static final int PROXY_TYPE_AUTO_PROXY_URL = 0x00000004;   // autoproxy URL
    public static final int PROXY_TYPE_AUTO_DETECT    = 0x00000008;   // use autoproxy detection

    //
    // Type of value
    //
    private static final int VALUE_INT = 0;
    private static final int VALUE_STRING = 1;
    private static final int VALUE_FILETIME = 2;

    
    public void getData(int pData) {
        if( 0 != pData ) {
            OS.MoveMemory(pData,new int[]{dwOption,0,0},4*3);
            switch( getValueType() ) {
                case VALUE_INT:
                    OS.MoveMemory(pData+4,new int[]{dwValue},4);
                    break;
                case VALUE_STRING:
                    OS.MoveMemory(pData+4, new int[] {strValue.getAddress()}, 4);
                    break;
            }
        }
    }
    
    public void setData(int pData) {
        if( 0 != pData ) {
            int[] pOption = new int[3];
            OS.MoveMemory(pOption,pData,4*pOption.length);
            dwOption = pOption[0];
            switch( getValueType() ) {
                case VALUE_INT:
                    dwValue = pOption[1];
                    break;
                case VALUE_STRING:
                    strValue.setData(pOption[1]);
                    break;
            }
        }
    }
    
    public void dispose() {
        strValue.dispose();
    }
    
    private int getValueType() {
        switch( dwOption ) {
            case INTERNET_PER_CONN_FLAGS:
            case INTERNET_PER_CONN_AUTODISCOVERY_FLAGS:
                return VALUE_INT;
            case INTERNET_PER_CONN_PROXY_SERVER:
            case INTERNET_PER_CONN_PROXY_BYPASS:
            case INTERNET_PER_CONN_AUTOCONFIG_URL:
            case INTERNET_PER_CONN_AUTOCONFIG_SECONDARY_URL:
            case INTERNET_PER_CONN_AUTOCONFIG_RELOAD_DELAY_MINS:
            case INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_URL:
                return VALUE_STRING;
            case INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_TIME:
                System.out.println("dwOption "+dwOption+" is not supported"); //$NON-NLS-1$ //$NON-NLS-2$
                return VALUE_FILETIME;
        }
        return -1;
    }
    
    public String toString() {
        String s;
        switch( dwOption ) {
            case INTERNET_PER_CONN_FLAGS:
                s = "INTERNET_PER_CONN_FLAGS"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_PROXY_SERVER:
                s = "INTERNET_PER_CONN_PROXY_SERVER"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_PROXY_BYPASS:
                s = "INTERNET_PER_CONN_PROXY_BYPASS"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_AUTOCONFIG_URL:
                s = "INTERNET_PER_CONN_AUTOCONFIG_URL"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_AUTODISCOVERY_FLAGS:
                s = "INTERNET_PER_CONN_AUTODISCOVERY_FLAGS"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_AUTOCONFIG_SECONDARY_URL:
                s = "INTERNET_PER_CONN_AUTOCONFIG_SECONDARY_URL"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_AUTOCONFIG_RELOAD_DELAY_MINS:
                s = "INTERNET_PER_CONN_AUTOCONFIG_RELOAD_DELAY_MINS"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_TIME:
                s = "INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_TIME"; //$NON-NLS-1$
                break;
            case INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_URL:
                s = "INTERNET_PER_CONN_AUTOCONFIG_LAST_DETECT_URL"; //$NON-NLS-1$
                break;
            default:
                s = "INTERNET_PER_CONN_OPTION: dwOption="+dwOption; //$NON-NLS-1$
                break;
        }
        switch( getValueType() ) {
            case VALUE_INT:
                s += " dwValue="+dwValue; //$NON-NLS-1$
                break;
            case VALUE_STRING:
                s += " strValue="+strValue.getString(); //$NON-NLS-1$
        }
        return s;
    }
}

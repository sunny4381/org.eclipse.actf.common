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



public class INTERNET_PER_CONN_OPTION_LIST {
    public WSTR   wsConnection = new WSTR();  // connection name to set/query perConnOptions
    public int    dwOptionCount = 0; // number of perConnOptions to set/query
    public int    dwOptionError = 0; // on error, which option failed
    public INTERNET_PER_CONN_OPTION[] perConnOptions = null;
    public static final int sizeof = 20;
    
    private int optionsAddress;
    
    public INTERNET_PER_CONN_OPTION_LIST(int optionCount) {
        dwOptionCount = optionCount;
        perConnOptions = new INTERNET_PER_CONN_OPTION[dwOptionCount];
        for( int i=0; i<perConnOptions.length; i++ ) {
            perConnOptions[i] = new INTERNET_PER_CONN_OPTION();
        }
    }
       
    public void getData(int pData) {
        if( 0 != pData ) {
            if( 0 == optionsAddress ) {
                optionsAddress = MemoryUtil.GlobalAlloc(INTERNET_PER_CONN_OPTION.sizeof*dwOptionCount); 
            }
            if( null != perConnOptions ) {
                for( int i=0; i<perConnOptions.length; i++ ) {
                    perConnOptions[i].getData(optionsAddress+INTERNET_PER_CONN_OPTION.sizeof*i);
                }
            }
            MemoryUtil.MoveMemory(pData,new int[]{sizeof,wsConnection.getAddress(),dwOptionCount,dwOptionError,optionsAddress},4*5);
        }
    }
    
    public void setData(int pData) {
        if( 0 != pData ) {
            int[] pList = new int[5];
            MemoryUtil.MoveMemory(pList,pData,4*pList.length);
            if( sizeof == pList[0] ) {
                wsConnection.setData(pList[1]);
                dwOptionCount = pList[2];
                dwOptionError = pList[3];
                optionsAddress = pList[4];
                if( null != perConnOptions ) {
                    if( perConnOptions.length ==dwOptionCount ) {
                        for( int i=0; i<perConnOptions.length; i++ ) {
                            perConnOptions[i].setData(optionsAddress+INTERNET_PER_CONN_OPTION.sizeof*i);
                        }
                    }
                }
            }
        }
    }
    
    public void dispose() {
        wsConnection.dispose();
        if( null != perConnOptions ) {
            for( int i=0; i<perConnOptions.length; i++ ) {
                perConnOptions[i].dispose();
            }
            perConnOptions = null;
        }
        if( 0 != optionsAddress ) {
            MemoryUtil.GlobalFree(optionsAddress);
            optionsAddress = 0;
        }
    }
}

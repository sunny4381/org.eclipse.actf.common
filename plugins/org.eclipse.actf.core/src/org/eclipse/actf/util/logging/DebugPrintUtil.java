/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.logging;

import org.eclipse.core.runtime.Platform;

public class DebugPrintUtil {
    private static boolean IN_DEV_OR_DEBUG = (Platform.inDevelopmentMode() || Platform.inDebugMode());

    public static void debugPrintln(Object target) {
        if (Platform.inDebugMode()) {
            System.out.println(target);
        }
    }
    
    public static void devPrintln(Object target){
        if(Platform.inDebugMode()){
            System.out.println(target);
        }
    }
    
    public static void devOrDebugPrintln(Object target){
        if(IN_DEV_OR_DEBUG){
            System.out.println(target);            
        }
    }
    
    public static void debugPrintStackTrace(Exception e){
        if(Platform.inDebugMode()){
            e.printStackTrace();
        }
    }

    public static void devPrintStackTrace(Exception e){
        if(Platform.inDevelopmentMode()){
            e.printStackTrace();
        }
    }

    public static void devOrDebugPrintStackTrace(Exception e){
        if(IN_DEV_OR_DEBUG){
            e.printStackTrace();
        }
    }


}

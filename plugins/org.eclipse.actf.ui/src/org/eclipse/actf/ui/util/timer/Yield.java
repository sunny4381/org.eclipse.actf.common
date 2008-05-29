/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.ui.util.timer;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Display;



public class Yield {
    private static Display display;
    
    public static void initialize() {
        display = Display.getCurrent();
    }
    
    private static class InvocationRunnable implements Runnable {
    	public Object ret;
    	public Method method;
    	public Object target;
    	public Object[] args;
        public void run() {
            try {
                ret = method.invoke(target, args);
            } catch (Exception e) {
                e.printStackTrace();
                ret = null;
            }
        }
    }
    
    private static InvocationRunnable invocationRunnable = new InvocationRunnable(); 
    
    public static Object syncInvoke(Method m, Object o, Object[] args) throws Exception {
    	invocationRunnable.method = m;
    	invocationRunnable.target = o;
    	invocationRunnable.args = args;
    	display.syncExec(invocationRunnable);
    	return invocationRunnable.ret;
    }

    public static void forWhile(int duration) {
        long end = System.currentTimeMillis() + duration;
        while (end > System.currentTimeMillis()) {
            display.readAndDispatch();
        }
    }
    
    public static void once() {
        display.readAndDispatch();
    }
}

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.internal.vocab.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.actf.util.vocab.IEvalTarget;
import org.eclipse.actf.util.vocab.IProposition;




public class Function implements IProposition {
    private String name;

    private Object[] args;

    private Method method;

    public Function(String name, String... args) {
        this.name = name;
        this.args = new Object[args.length+1];
        for(int i=0; i<args.length; i++){
            this.args[i+1] = args[i];
        }
    }

    public boolean eval(IEvalTarget node){
        if (method == null) {
            Class[] classes = new Class[args.length];
            for (int i = 0; i < classes.length; i++) {
                classes[i] = args[i].getClass();
            }
            Class clazz = node.getTerms().getClass();
            try {
                method = clazz.getMethod(getName(), classes);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if(method != null){
            try {
                this.args[0] = node;
                return (Boolean)method.invoke(node.getTerms(), (Object[])args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //throw new RuntimeException("Fails to invoke method \""+getName()+"\" for "+node);
        return false;
    }

    public String getName() {
        return name;
    }

}

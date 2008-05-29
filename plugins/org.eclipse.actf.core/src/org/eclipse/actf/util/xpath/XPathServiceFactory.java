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

package org.eclipse.actf.util.xpath;

import java.lang.reflect.Method;



public abstract class XPathServiceFactory {
    private static XPathServiceFactory enable(String name) {
        try {
            Class clazz = Class.forName(name);
            Method newInstanceMethod = clazz.getMethod("newInstance");
            Object instance = newInstanceMethod.invoke(null);
            if (instance instanceof XPathServiceFactory) {
                return (XPathServiceFactory) instance;
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    static {
        setFactory(enable("org.eclipse.actf.util.xpath.jaxp.XPathServiceFactoryImpl"));
        setFactory(enable("org.eclipse.actf.util.jxpath.XPathServiceFactoryImpl"));
    }
    private static XPathServiceFactory factory;
    public static void setFactory(XPathServiceFactory f) {
        if (f != null) {
            factory = f;
        }
    }

    public static XPathService newService() {
        return factory.getService();
    }
    
    protected abstract XPathService getService();
}

/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ooo.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.actf.model.ui.editors.ooo.initializer.util.OOoEditorInitUtil;

import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lib.uno.helper.UnoUrl;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;



public class BootstrapForOOoComposite extends Bootstrap {
	private static XComponentContext xComponentContext = null;
	private static XMultiServiceFactory xMSF = null;
	private static XDesktop xDesktop = null;
	
    public static final Process bootstrap(ClassLoader classLoader) throws BootstrapException {
        
        Process process  = null;
        
        try {
            xComponentContext = createInitialComponentContext(null);
            if (null == xComponentContext) {
                throw new BootstrapException("no local component context");
            }

            String sOfficeExeName = System.getProperty("os.name").startsWith("Windows") ? "soffice.exe" : "soffice";
            File sOfficeFile = new File(OOoEditorInitUtil.getOpenOfficePath() + "\\" + sOfficeExeName);
            if (null == sOfficeFile) {
                throw new BootstrapException("no office executable found");
            }

            XMultiComponentFactory xMCF = xComponentContext.getServiceManager();
            if (null == xMCF) {
                throw new BootstrapException("no initial service manager");
            }

            XUnoUrlResolver xUnoUrlResolver = UnoUrlResolver.create(xComponentContext);
            String sUnoUrl = "uno:pipe,name=" + getPipeName() + ";urp;StarOffice.ServiceManager";

            UnoUrl unourl = UnoUrl.parseUnoUrl(sUnoUrl);
            String connType = unourl.getConnection();
            String protocol = unourl.getProtocol();
            String initObj = unourl.getRootOid();
            
            String as[] = new String[5];
            as[0] = sOfficeFile.getPath();
            as[1] = "-nologo";
            as[2] = "-nodefault";
            as[3] = "-norestore";            
            if (connType.equals("pipe")) {
                as[4] = "-accept=pipe,name=" + getPipeName() + ";" + protocol + ";" + initObj;
            } else {
                throw new IOException("not connection specified");
            }
            
            process = Runtime.getRuntime().exec(as);
            
            Object oServiceManager;
            do {
                try {
                    oServiceManager = xUnoUrlResolver.resolve("uno:pipe,name=" + getPipeName() + ";urp;StarOffice.ServiceManager");
                    break;
                } catch (NoConnectException nce) {
                    Thread.currentThread();
                    Thread.sleep(500);
                } } while (true);
                
            xMSF = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, oServiceManager);
            xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, xMSF.createInstance("com.sun.star.frame.Desktop"));
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new BootstrapException(e);
        }
        
        return process;
    }

    private static String getPipeName() {
        String s = System.getProperty("user.name") + "_Office";
        s = replaceAll(s, "_", "%B7");
        try {
//        	s = replaceAll(replaceAll(URLEncoder.encode(s), "+", "%20"), "%", "_");
			s = replaceAll(replaceAll(URLEncoder.encode(s,"UTF-8"), "+", "%20"), "%", "_");
		} catch (UnsupportedEncodingException e) {
		}
        return s;
    }

    private static String replaceAll(String s, String s1, String s2) {
        StringBuffer stringbuffer = new StringBuffer(s);
        int i = s.length();
        int j = s1.length();
        while ((i = s.lastIndexOf(s1, i - 1)) > -1) {
            stringbuffer.replace(i, i + j, s2);
        }

        return stringbuffer.toString();
    }
    
	public static XComponentContext getXComponentContext() {return xComponentContext;}
	public static XMultiServiceFactory getXMultiServiceFactory() {return xMSF;}
	public static XDesktop getXDesktop() {return xDesktop;}
}

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
package org.eclipse.actf.model.ui.editors.ooo.internal.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.actf.model.dom.odf.ODFConstants.ContentType;
import org.eclipse.actf.model.dom.odf.office.DocumentContentElement;
import org.eclipse.actf.model.ui.editors.ooo.initializer.util.OOoEditorInitUtil;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.TCHAR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPages;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;



public class ODFUtils {
    public static int getDrawPageCount(XComponent xComp) {
        XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) UnoRuntime.queryInterface(
                XDrawPagesSupplier.class, xComp);
        XDrawPages xDrawPages = xDrawPagesSupplier.getDrawPages();

        return xDrawPages.getCount();
    }

    public static XDrawPage getDrawPageByIndex(XComponent xComp, int index) throws ODFException {

        try {
            XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) UnoRuntime.queryInterface(
                    XDrawPagesSupplier.class, xComp);
            XDrawPages xDrawPages = xDrawPagesSupplier.getDrawPages();

            return (XDrawPage) UnoRuntime.queryInterface(XDrawPage.class, xDrawPages.getByIndex(index));

        } catch (IndexOutOfBoundsException ioobe) {
            ioobe.printStackTrace();
            throw new ODFException(ioobe.getMessage());
        } catch (WrappedTargetException wte) {
            wte.printStackTrace();
            throw new ODFException(wte.getMessage());
        }
    }

    public static ContentType getODFFileType(Document odfContent) {
        if (odfContent!=null) {     
            Element content = odfContent.getDocumentElement();
            if (content instanceof DocumentContentElement) {
                return ((DocumentContentElement)content).getBodyElement().getContent().getContentType();
            }
        }
        return ContentType.NONE;        
    }

    public static XFrame getXFrame(XComponentContext xCompContext) throws ODFException {
        XComponent xComp = getXComponent(xCompContext);
        XModel model = (XModel) UnoRuntime.queryInterface(XModel.class, xComp);
        return model.getCurrentController().getFrame();
    }

    public static XDispatchHelper getXDispatchHelper(XComponentContext xCompContext) throws ODFException {

        XDispatchHelper xDispatchHelper = null;

        XMultiComponentFactory xMCF = xCompContext.getServiceManager();

        try {
            Object oDispatchHelper = xMCF.createInstanceWithContext("com.sun.star.frame.DispatchHelper", xCompContext);
            xDispatchHelper = (XDispatchHelper) UnoRuntime.queryInterface(XDispatchHelper.class, oDispatchHelper);
        } catch (Exception e) {
            throw new ODFException(e.getMessage());
        }

        return xDispatchHelper;
    }

    public static XLayoutManager getXLayoutManager(XFrame xFrame) throws ODFException {

        XLayoutManager xLayoutManager = null;

        XPropertySet framePropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xFrame);
        try {
            xLayoutManager = (XLayoutManager) UnoRuntime.queryInterface(XLayoutManager.class, framePropSet
                    .getPropertyValue("LayoutManager"));
        } catch (UnknownPropertyException upe) {
            throw new ODFException(upe.getMessage());
        } catch (WrappedTargetException wte) {
            throw new ODFException(wte.getMessage());
        }

        return xLayoutManager;
    }

    public static XComponent getXComponent(XComponentContext xComponentContext) throws ODFException {
    	XDesktop xDesktop = BootstrapForOOoComposite.getXDesktop();
        XEnumerationAccess xEnumerationAccess = xDesktop.getComponents();
        XEnumeration xEnumeration = xEnumerationAccess.createEnumeration();
        while (xEnumeration.hasMoreElements()) {
            Object oComp = null;
            try {
                oComp = xEnumeration.nextElement();
            } catch (NoSuchElementException nsee) {
                throw new ODFException(nsee.getMessage());
            } catch (WrappedTargetException wte) {
                throw new ODFException(wte.getMessage());
            }

            if (null != oComp) {                
                return (XComponent) UnoRuntime.queryInterface(XComponent.class, oComp);
            }
        }

        return null;
    }

    public static XDesktop getXDesktop(XComponentContext xComponentContext) throws ODFException {
        XDesktop xDesktop = null;
        try {
        	XMultiComponentFactory xMCF = xComponentContext.getServiceManager();
            Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xComponentContext);
            xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, oDesktop);
        } catch (Exception e) {
            throw new ODFException(e.getMessage());
        }

        return xDesktop;
    }
    
    public static ClassLoader loadOpenOfficeLibs() throws ODFException {
        String openOfficeProgramPath = OOoEditorInitUtil.getOpenOfficePath() + "\\";

        File msvcr71 = new File(openOfficeProgramPath + "msvcr71.dll");
        if (msvcr71.exists()) {
            System.load(msvcr71.getPath());
        }

        if (System.getProperty("java.vm.vendor").equals("IBM Corporation")) {
            String javaHome = System.getProperty("com.ibm.oti.vm.bootstrap.library.path");

            // dummy variable to load awt.dll
            java.awt.Color awtColor = new java.awt.Color(0, 0, 0);
            System.load(javaHome + "\\jawt.dll");
        } else if (System.getProperty("java.vm.vendor").equals("Sun Microsystems Inc.")) {
            String javaHome = System.getProperty("java.home");
            
            //dummy variable to load awt.dll
            java.awt.Color awtColor = new java.awt.Color(0, 0, 0);
            System.load(javaHome + "\\bin\\jawt.dll");
        } else {
        	throw new ODFException("should be launched by using IBM Java");
        }

        System.load(openOfficeProgramPath + "uwinapi.dll");
        System.load(openOfficeProgramPath + "officebean.dll");
        System.load(openOfficeProgramPath + "sal3.dll");
        System.load(openOfficeProgramPath + "jpipe.dll");

        ClassLoader javaClassLoader = null;
        try {
        	URL[] jarList = new URL[] { new File(openOfficeProgramPath + "classes\\juh.jar").toURL(),
        			new File(openOfficeProgramPath + "classes\\jurt.jar").toURL(),
        			new File(openOfficeProgramPath + "classes\\ridl.jar").toURL(),
        			new File(openOfficeProgramPath + "classes\\unoil.jar").toURL() };
        	
        	javaClassLoader = new URLClassLoader(jarList, Bootstrap.class.getClassLoader());

        } catch (MalformedURLException murle) {
            throw new ODFException(murle.getMessage());
        }

        return javaClassLoader;
    }

    private static String getWindowClass(int hwnd) {
		TCHAR buffer = new TCHAR(0, 128);
		OS.GetClassName(hwnd, buffer, buffer.length());
		return buffer.toString(0, buffer.strlen());
	}
	
	private static int getOpenOfficeFrameNum(int hWnd, int frameNum) {
		int result = frameNum;
		int hFrame = 0;
		int hwndChild = OS.GetWindow (hWnd, OS.GW_CHILD);
		while (hwndChild != 0) {
			String winClass = getWindowClass(hwndChild);
			if (winClass.equals("SALTMPSUBFRAME")) {
				result++;
			} else {
				result = getOpenOfficeFrameNum(hwndChild, result);
			}
			hwndChild = OS.GetWindow(hwndChild, OS.GW_HWNDNEXT);			
		}
		return result;
	}
	
	public static int getOpenOfficeFrameNum() {
		int result = 0;
		int hwndChild = OS.GetWindow (OS.GetDesktopWindow(), OS.GW_CHILD);
		while (hwndChild != 0) {
			result = getOpenOfficeFrameNum(hwndChild, result);
			String winClass = getWindowClass(hwndChild);
			if (winClass.equals("SALFRAME")){
				result++;
			} else {
				result = getOpenOfficeFrameNum(hwndChild, result);
			}			
			hwndChild = OS.GetWindow(hwndChild, OS.GW_HWNDNEXT);
		}
		return result;
	}
}

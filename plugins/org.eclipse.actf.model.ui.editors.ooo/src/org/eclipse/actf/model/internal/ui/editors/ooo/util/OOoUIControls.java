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

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDispatch;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XURLTransformer;



public class OOoUIControls {

	public static void hideUIElements(XFrame xFrame, XMultiServiceFactory xMSF) throws ODFException {
		try {
	        XDispatchProvider xDispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface(XDispatchProvider.class, xFrame);			
	        Object oDispatchHelper = xMSF.createInstance("com.sun.star.frame.DispatchHelper");
	    	XDispatchHelper dispatchHelper = (XDispatchHelper)AnyConverter.toObject(XDispatchHelper.class, oDispatchHelper);

	    	executeXDispatch("NormalMultiPaneGUI", Boolean.TRUE, xDispatchProvider, dispatchHelper);
	        executeXDispatch("InputLineVisible", Boolean.FALSE, xDispatchProvider, dispatchHelper);
            executeXDispatch("LeftPaneDraw", Boolean.FALSE, xDispatchProvider, dispatchHelper);
            executeXDispatch("LeftPaneImpress", Boolean.FALSE, xDispatchProvider, dispatchHelper);
	        executeXDispatch("RightPane", Boolean.FALSE, xDispatchProvider, dispatchHelper);
	        executeXDispatch("Ruler", Boolean.FALSE, xDispatchProvider, dispatchHelper);
            executeXDispatch("ShowRuler", Boolean.FALSE, xDispatchProvider, dispatchHelper);
            executeXDispatch("ZoomPage", Boolean.TRUE, xDispatchProvider, dispatchHelper);
            
	        hideXUIElements(xFrame);
		} catch (Exception e) {
			e.printStackTrace();
		}		
    }

    public static boolean setDrawingMode(XMultiServiceFactory xMSF, XController controller) throws ODFException {
    	return executeXDispatchAction(xMSF, controller, "slot:27009");
    }
    
    private static void hideXUIElements(XFrame xFrame) throws ODFException {

        try {
            XLayoutManager xLayoutManager = ODFUtils.getXLayoutManager(xFrame);
            XUIElement[] xUIElems = xLayoutManager.getElements();

            XPropertySet xUIElemPropSet;
            XPropertySetInfo xUIElemPropSetInfo;
            Property[] xUIElemProps;
            for (int i = 0; i < xUIElems.length; i++) {
                xUIElemPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xUIElems[i]);
                xUIElemPropSetInfo = xUIElemPropSet.getPropertySetInfo();
                if(xUIElemPropSetInfo.hasPropertyByName("Persistent")) {
                    xUIElemPropSet.setPropertyValue("Persistent", Boolean.FALSE);
                }
                xUIElemProps = xUIElemPropSetInfo.getProperties();
                for (int j = 0; j < xUIElemProps.length; j++) {
                    if (xUIElemProps[j].Name.equals("ResourceURL")) {
                        xLayoutManager.hideElement(xUIElemPropSet.getPropertyValue(xUIElemProps[j].Name).toString());
                    }
                }
            }
        } catch (UnknownPropertyException upe) {
            throw new ODFException(upe.getMessage());
        } catch (WrappedTargetException wte) {
            throw new ODFException(wte.getMessage());
        } catch (PropertyVetoException pve) {
            throw new ODFException(pve.getMessage());
        } catch (IllegalArgumentException iae) {
            throw new ODFException(iae.getMessage());
        }
    }

    private static void executeXDispatch(String name, Object value, XDispatchProvider xDispatchProvider,
    		XDispatchHelper dispatchHelper) throws ODFException {
    	PropertyValue[] pValue = new PropertyValue[1];
        pValue[0] = new PropertyValue();
        pValue[0].Name = name;
        pValue[0].Value = value;

        dispatchHelper.executeDispatch(xDispatchProvider, ".uno:" + name, "", 0, pValue);
    }
    

	private static boolean executeXDispatchAction(XMultiServiceFactory xMSF, XController controller, String action) {
		try {
			XDispatchProvider dispatchProvider = (XDispatchProvider)UnoRuntime.queryInterface(XDispatchProvider.class, controller);
			if (null != dispatchProvider) {
				com.sun.star.util.URL actionURL[] = new com.sun.star.util.URL[1];
				actionURL[0] = new com.sun.star.util.URL();
				actionURL[0].Complete = action;
				XURLTransformer urlTransformer = (XURLTransformer) UnoRuntime.queryInterface(XURLTransformer.class,
						xMSF.createInstance("com.sun.star.util.URLTransformer"));
				urlTransformer.parseStrict(actionURL);
				XDispatch dispatch = dispatchProvider.queryDispatch(actionURL[0], "", 0);
				if (null != dispatch) {
					dispatch.dispatch(actionURL[0], new PropertyValue[0]);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}    
}

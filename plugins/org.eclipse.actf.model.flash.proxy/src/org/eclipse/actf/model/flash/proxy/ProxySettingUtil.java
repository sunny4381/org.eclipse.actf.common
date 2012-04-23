/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash.proxy;

import org.eclipse.actf.model.internal.flash.proxy.ProxyPlugin;
import org.eclipse.actf.model.internal.flash.proxy.preferences.ProxyPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * The utilities to control Flash Proxy Mode.
 */
public class ProxySettingUtil {

	public static final int PROXY_NONE = 0;
    public static final int PROXY_SESSION = 1;
    public static final int PROXY_GLOBAL = 2;
	
    static IPreferenceStore ps = ProxyPlugin.getDefault().getPreferenceStore();
    
	public static int getCurrentMode(){
		String mode = ps.getString(ProxyPreferenceConstants.P_PROXY_TYPE);
		if(mode !=null){
			if(mode.equals(ProxyPreferenceConstants.PROXY_SESSION)){
				return 1;
			}else if (mode.equals(ProxyPreferenceConstants.PROXY_NONE)){
				return 0;
			}
			return 2;
		}		
		return 1; //default value
	}

	public static boolean setCurrentMode(int mode){
		switch(mode){
		case PROXY_NONE:
			ps.setValue(ProxyPreferenceConstants.P_PROXY_TYPE, ProxyPreferenceConstants.PROXY_NONE);
			break;
		case PROXY_SESSION:
			ps.setValue(ProxyPreferenceConstants.P_PROXY_TYPE, ProxyPreferenceConstants.PROXY_SESSION);
			break;
		case PROXY_GLOBAL:
			ps.setValue(ProxyPreferenceConstants.P_PROXY_TYPE, ProxyPreferenceConstants.PROXY_GLOBAL);
			break;
		default:
			return false;
		}
		return true;		
	}

	
}

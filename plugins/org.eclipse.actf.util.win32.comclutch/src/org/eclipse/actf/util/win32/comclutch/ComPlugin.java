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
package org.eclipse.actf.util.win32.comclutch;

import org.eclipse.actf.util.win32.comclutch.impl.IUrlHistoryStg2Impl;
import org.eclipse.actf.util.win32.keyhook.IKeyHook;
import org.eclipse.actf.util.win32.keyhook.IKeyHookListener;
import org.eclipse.actf.util.win32.keyhook.ISendEvent;
import org.eclipse.actf.util.win32.keyhook.impl.KeyHookImpl;
import org.eclipse.actf.util.win32.keyhook.impl.SendEventImpl;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ComPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ComPlugin plugin;
    private static IUrlHistoryStg2Impl history;
	
	/**
	 * The constructor.
	 */
	public ComPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
        ComService.initialize();
        history = new IUrlHistoryStg2Impl();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
        history.release();
        ComService.uninitialize();
	}

	/**
	 * Returns the shared instance.
	 */
	public static ComPlugin getDefault() {
		return plugin;
	}
	

    public ResourceManager newResourceManager(ResourceManager parent) {
        return ResourceManager.newResourceManager(parent);
    }
    
    public BrowserHistory getBrowserHistory() {
        return history;
    }
    

	/**
	 * @param listener
	 *            The IKeyHookListener to handle the events raised by IKeyHook.
	 * @return An instance of the IKeyHook.
	 * @see IKeyHook
	 */
	public IKeyHook newKeyHook(IKeyHookListener listener) {
		int handle = getWorkbench().getActiveWorkbenchWindow().getShell().handle;
		return new KeyHookImpl(listener, handle);
	}

	/**
	 * @return An instance of the ISendEvent
	 * @see ISendEvent
	 */
	public ISendEvent newSendEvent() {
		return SendEventImpl.getSendEvent();
	}
}

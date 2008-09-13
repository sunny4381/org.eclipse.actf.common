/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.ui.editors.ooo;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.actf.model.ui.editors.ooo.initializer.kicker.IOOoEditorInitializerKicker;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class OOoEditorPlugin extends AbstractUIPlugin implements IOOoEditorInitializerKicker {

	public static final String PLUGIN_ID = "org.eclipse.actf.model.ui.editors.ooo";

	//The shared instance.
	private static OOoEditorPlugin plugin;
	
    private ResourceBundle _resourceBundle;

    private static BundleContext _context;
    
	/**
	 * The constructor.
	 */
	public OOoEditorPlugin() {
		plugin = this;
		
		//OOoEditorInitUtil is called automatically by using dummy interface+LazyStart		
//		if(!OOoEditorInitUtil.isInitialized()){
//			System.err.println("Please call OOoEditorInitUtil.init() before using OOoEditor.");
//		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
        _context = context;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
        _context = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static OOoEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
    
    public static String getResourceString(String key) {
        ResourceBundle bundle = OOoEditorPlugin.getDefault().getResourceBundle();
        try {
            return (null != bundle) ? bundle.getString(key) : key;
        } catch (MissingResourceException mre) {
            return "???" + key + "???";
        }
    }    
    
    public ResourceBundle getResourceBundle() {
        if (null == _resourceBundle && null != _context) {
            Bundle bundle = _context.getBundle();
            if (null != bundle) {
                _resourceBundle = Platform.getResourceBundle(bundle);
            }
        }

        return _resourceBundle;
    }    
}

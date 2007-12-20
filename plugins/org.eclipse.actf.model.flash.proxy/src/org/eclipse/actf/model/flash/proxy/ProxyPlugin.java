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
package org.eclipse.actf.model.flash.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.proxy.actions.DeleteCacheAction;
import org.eclipse.actf.model.flash.proxy.preferences.ProxyPreferenceConstants;
import org.eclipse.actf.model.flash.proxy.ui.CacheClearDialog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class ProxyPlugin extends AbstractUIPlugin implements IPropertyChangeListener {

    // The shared instance.
    private static ProxyPlugin plugin;

    // Proxy manager
    private static ProxyManager proxyManager = new ProxyManager();

    // Preferences change listeners
    private List listeners;

    // Images
    public static final ImageDescriptor IMAGE_CLEAR = getImageDescriptor("icons/action16/clear.gif"); //$NON-NLS-1$

    /**
     * The constructor.
     */
    public ProxyPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        addPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (ProxyPreferenceConstants.P_PROXY_TYPE.equals(event.getProperty())
                        || ProxyPreferenceConstants.P_PROXY_SWF_METHOD.equals(event.getProperty())
                        || ProxyPreferenceConstants.P_SWF_MINIMUM_VERSION.equals(event.getProperty())
                        || ProxyPreferenceConstants.P_TIMEOUT.equals(event.getProperty())) {
                    setProxySettings();
                }
            }
        });
        setProxySettings();
    }

    private void setProxySettings() {
        proxyManager.stopProxy();
        String proxyType = getPreferenceStore().getString(ProxyPreferenceConstants.P_PROXY_TYPE);
        int swfVersion = getPreferenceStore().getInt(ProxyPreferenceConstants.P_SWF_MINIMUM_VERSION);
        int timeout = getPreferenceStore().getInt(ProxyPreferenceConstants.P_TIMEOUT);
        if (!ProxyPreferenceConstants.PROXY_NONE.equals(proxyType)) {
            String proxySwfMethod = getPreferenceStore().getString(ProxyPreferenceConstants.P_PROXY_SWF_METHOD);
            boolean swfBootLoader = false;
            boolean swfTranscoder = false;
            if (ProxyPreferenceConstants.PROXY_SWF_METHOD_BOOTLOADER.equals(proxySwfMethod)) {
                swfBootLoader = true;
            } else if (ProxyPreferenceConstants.PROXY_SWF_METHOD_TRANSCODER.equals(proxySwfMethod)) {
                swfTranscoder = true;
            }
            proxyManager.startProxy(0, swfVersion, timeout * 1000, swfBootLoader, swfTranscoder);

        }
        proxyManager.setInternetOptions(ProxyPreferenceConstants.PROXY_GLOBAL.equals(proxyType));
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        proxyManager.dispose();
        listeners = null;
        getPreferenceStore().removePropertyChangeListener(this);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static ProxyPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.actf.model.flash.proxy", path); //$NON-NLS-1$
    }

    public void propertyChange(PropertyChangeEvent event) {
        for (int i = 0; i < listeners.size(); i++) {
            ((IPropertyChangeListener) listeners.get(i)).propertyChange(event);
        }
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        if (null == listeners) {
            listeners = new ArrayList();
            getPreferenceStore().addPropertyChangeListener(this);
        }
        listeners.add(listener);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        listeners.remove(listener);
    }
    
    public static boolean cacheChecked = false;

    public void checkCache() {

        String setting = getPreferenceStore().getString(ProxyPreferenceConstants.P_CACHE_CLEAR);

        boolean clearCacheFlag = true && !cacheChecked;

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        Shell shell = window.getShell();
        if (setting.equals(ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP) && !cacheChecked) {

            CacheClearDialog dialog = null;

            dialog = new CacheClearDialog(shell, Platform.getProduct().getName());
            try {
                int result = dialog.open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String select = dialog.getSelection();

            if (select.equals(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP_AND_CACHE_CLEAR)) {
                getPreferenceStore().setValue(ProxyPreferenceConstants.P_CACHE_CLEAR,
                        ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP);
            } else if (select.equals(ProxyPreferenceConstants.CONFIRM_AND_CACHE_CLEAR)) {
                getPreferenceStore().setValue(ProxyPreferenceConstants.P_CACHE_CLEAR,
                        ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP);
            } else if (select.equals(ProxyPreferenceConstants.CONFIRM_AND_NO_OPERATION)) {
                getPreferenceStore().setValue(ProxyPreferenceConstants.P_CACHE_CLEAR,
                        ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP);
                clearCacheFlag = false;
            }
        } else if (setting.equals(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP) && cacheChecked) {
            clearCacheFlag = false;
        } else if (setting.equals(ProxyPreferenceConstants.NO_CACHE_CLEAR)) {
            clearCacheFlag = false;
        }

        if (clearCacheFlag) {
        	clearCache(false,window);
//            DeleteCacheAction dca = new DeleteCacheAction();
//            dca.init(window);
//            dca.run(null);
        }
    }

    public void clearCacheWithCheck() {

        String setting = getPreferenceStore().getString(ProxyPreferenceConstants.P_CACHE_CLEAR);

        if (setting.equals(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP)) {
        	clearCache(false,null);
//            DeleteCacheAction dca = new DeleteCacheAction();
//            dca.run(null);
        }

    }
    
    /**
     * External interface to clear SWF cache
     * @param background
     *     background processing flag
     * @param window 
     *     active WorkbenchWindow for progress dialog. 
     *     use null for silent mode.
     */
    public void clearCache(boolean background, IWorkbenchWindow window) {
        DeleteCacheAction dca = new DeleteCacheAction(background);
        dca.init(window);
        dca.run(null);
    }
}

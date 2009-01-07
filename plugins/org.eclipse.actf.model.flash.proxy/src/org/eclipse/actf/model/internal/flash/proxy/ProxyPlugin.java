/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.internal.flash.proxy.preferences.ProxyPreferenceConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ProxyPlugin extends AbstractUIPlugin implements
		IPropertyChangeListener {

	// The shared instance.
	private static ProxyPlugin plugin;

	// Proxy manager
	private static ProxyManager proxyManager = new ProxyManager();

	// Preferences change listeners
	private List<IPropertyChangeListener> listeners;

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
				if (ProxyPreferenceConstants.P_PROXY_TYPE.equals(event
						.getProperty())
						|| ProxyPreferenceConstants.P_PROXY_SWF_METHOD
								.equals(event.getProperty())
						|| ProxyPreferenceConstants.P_SWF_MINIMUM_VERSION
								.equals(event.getProperty())
						|| ProxyPreferenceConstants.P_TIMEOUT.equals(event
								.getProperty())
						|| ProxyPreferenceConstants.PROXY_PORT.equals(event
								.getProperty())) {
					setProxySettings();
				}
			}
		});
		getDefault().getWorkbench().getDisplay().asyncExec(new Runnable(){
			public void run() {
				setProxySettings();
			}
		});
	}

	private void setProxySettings() {
		proxyManager.stopProxy();
		String proxyType = getPreferenceStore().getString(
				ProxyPreferenceConstants.P_PROXY_TYPE);
		int swfVersion = getPreferenceStore().getInt(
				ProxyPreferenceConstants.P_SWF_MINIMUM_VERSION);
		int timeout = getPreferenceStore().getInt(
				ProxyPreferenceConstants.P_TIMEOUT);
		int port = getPreferenceStore().getInt(
				ProxyPreferenceConstants.PROXY_PORT);
		if (!ProxyPreferenceConstants.PROXY_NONE.equals(proxyType)) {
			String proxySwfMethod = getPreferenceStore().getString(
					ProxyPreferenceConstants.P_PROXY_SWF_METHOD);
			boolean swfBootLoader = false;
			boolean swfTranscoder = false;
			if (ProxyPreferenceConstants.PROXY_SWF_METHOD_BOOTLOADER
					.equals(proxySwfMethod)) {
				swfBootLoader = true;
			} else if (ProxyPreferenceConstants.PROXY_SWF_METHOD_TRANSCODER
					.equals(proxySwfMethod)) {
				swfTranscoder = true;
			}
			proxyManager.startProxy(port, swfVersion, timeout * 1000,
					swfBootLoader, swfTranscoder);

		}
		proxyManager.setInternetOptions(ProxyPreferenceConstants.PROXY_GLOBAL
				.equals(proxyType));
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.eclipse.actf.model.flash.proxy", path); //$NON-NLS-1$
	}

	public void propertyChange(PropertyChangeEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			((IPropertyChangeListener) listeners.get(i)).propertyChange(event);
		}
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if (null == listeners) {
			listeners = new ArrayList<IPropertyChangeListener>();
			getPreferenceStore().addPropertyChangeListener(this);
		}
		listeners.add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
	}

}

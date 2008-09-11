/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.flash;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.bridge.WaXcodingFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class FlashModelPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.actf.model.flash";

	// The shared instance
	private static FlashModelPlugin plugin;

	private static final Path imposedSWFPath = new Path("bridgeSWF/imposed.swf");
	private static final Path bootloaderSWFPath = new Path(
			"bridgeSWF/boot_loader.swf");
	private static final Path bridgeInitSWFPath = new Path(
			"bridgeSWF/boot_bridge.swf");

	/**
	 * The constructor
	 */
	public FlashModelPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		InputStream is;
		is = tryOpenStream(imposedSWFPath);
		IWaXcoding wax = WaXcodingFactory.getWaXcoding();
		if (is != null) {
			wax.setSWFTranscodingImposedFile(is);
		}
		is = tryOpenStream(bootloaderSWFPath);
		if (is != null) {
			wax.setSWFBootloader(is);
		}
		is = tryOpenStream(bridgeInitSWFPath);
		if (is != null) {
			wax.setSWFBridgeInit(is);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static FlashModelPlugin getDefault() {
		return plugin;
	}

	private InputStream tryOpenStream(Path path) {
		try {
			return FileLocator.openStream(getBundle(), path, false);
		} catch (IOException e) {
			return null;
		}
	}

}

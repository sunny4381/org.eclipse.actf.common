package org.eclipse.actf.ui;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ActfUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String ACTF_PLUGIN_ID = "org.eclipse.actf.ui";

	public static final String ACCESSIBILITY_TOOLS_MENU = "org.eclipse.actf.ui.actfContextMenu";
	
	public static final String ROOT_PREFRENCE_PAGE_ID = "org.eclipse.actf.ui.preferences.RootPreferencePage";

	// The shared instance
	private static ActfUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ActfUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static ActfUIPlugin getDefault() {
		return plugin;
	}

}

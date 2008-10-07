/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

/**
 * Interface to provide access to FlashPlayer and it's content.
 * 
 * @see IASBridge
 */
public interface IFlashPlayer extends IASBridge {

	/**
	 * @return native MSAA object of the FlashPlayer as {@link FlashMSAAObject}
	 */
	public abstract FlashMSAAObject getAccessible();

	/**
	 * @return IDispatch object of the FlashPlayer
	 */
	public abstract IDispatch getDispatch();

	/**
	 * @return status message
	 */
	public abstract String getStatus();

	/**
	 * @return version of FlashPlayer
	 */
	public abstract String getPlayerVersion();

	/**
	 * @return URL of Flash content
	 */
	public abstract String getContentURL();

	/**
	 * Set Property to FlashPlayer Node
	 * 
	 * @param propertyName
	 *            target property name
	 * @param value
	 *            property value to set
	 */
	public abstract void setPlayerProperty(String propertyName, String value);

	/**
	 * Get Property from FlashPlayer Node
	 * 
	 * @param propertyName
	 *            target property name
	 * @return property value
	 */
	public abstract String getPlayerProperty(String propertyName);

	/**
	 * @return window handle of FlashPlayer
	 * @see FlashMSAAObject#getWindow()
	 */
	public abstract int getWindow();

	/**
	 * @return WMode of the FlashPlayer
	 */
	public abstract String getWMode();

	/**
	 * @return SWF version of current content
	 */
	public int getSWFVersion();

	/**
	 * @return whether the HTML document that the FlashPlayer belongs to is
	 *         ready or not.
	 */
	public abstract boolean isReady();

	/**
	 * @return whether this Flash object is visible or not. Invisible means that
	 *         the Flash object is in window-less mode.
	 */
	public abstract boolean isVisible();

}
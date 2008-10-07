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
 *
 */
public interface IFlashPlayer extends IASBridge {

	/**
	 * @return
	 */
	public abstract FlashMSAAObject getAccessible();

	/**
	 * @return
	 */
	public abstract IDispatch getDispatch();

	/**
	 * @return
	 */
	public abstract String getStatus();

	/**
	 * @return
	 */
	public abstract String getPlayerVersion();
	
	/**
	 * @return
	 */
	public abstract String getContentURL();

	/**
	 * @param propertyName
	 * @param value
	 */
	public abstract void setPlayerProperty(String propertyName, String value);

	/**
	 * @param propertyName
	 * @return
	 */
	public abstract String getPlayerProperty(String propertyName);

	/**
	 * @return
	 */
	public abstract int getWindow();

	/**
	 * @return
	 */
	public abstract String getWMode();

	/**
	 * @return
	 */
	public int getSWFVersion();

	/**
	 * @return whether the HTML document that the FlashPlayer belongs to is
	 *         ready or not.
	 */
	/**
	 * @return
	 */
	public abstract boolean isReady();

	/**
	 * @return whether this Flash object is visible or not. Invisible means that
	 *         the Flash object is in window-less mode.
	 */
	/**
	 * @return
	 */
	public abstract boolean isVisible();

}
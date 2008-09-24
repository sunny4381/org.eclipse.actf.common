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

public interface IFlashPlayer extends IASBridge {

	public abstract FlashMSAAObject getAccessible();

	public abstract IDispatch getDispatch();

	public abstract String getStatus();

	public abstract String getPlayerVersion();
	
	public abstract String getContentURL();

	public abstract void setPlayerProperty(String propertyName, String value);

	public abstract String getPlayerProperty(String propertyName);

	public abstract int getWindow();

	public abstract String getWMode();

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
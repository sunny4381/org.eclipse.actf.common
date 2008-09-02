/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32.keyhook;

/**
 * IKeyHook interface defines the methods to hook keys of Windows.
 */
public interface IKeyHook {
	/**
	 * Register the vkey on the native key hook utility.
	 * 
	 * @param vkey
	 *            the virtual key code to be hooked.
	 * @param modifier
	 *            the modifier code to be used when the key is hooked.
	 */
	void registerHookedKey(int vkey, int modifier);

	/**
	 * @param flag
	 *            if it is true then all of keys are hooked, and it is false
	 *            then any keys are not hooked.
	 */
	void hookAll(boolean flag);

	/**
	 * You have to call this function when you dispose the instance.
	 */
	void dispose();
}

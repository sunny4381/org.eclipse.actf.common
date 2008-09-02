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
 * The listener interface for receiving hooked key events. The class that is
 * interested in processing hooked key events implements this interface.
 */
public interface IKeyHookListener {
	/**
	 * Invoke when a key is hooked.
	 * 
	 * @param vkey
	 *            the virtual key code.
	 * @param modifier
	 *            the modifier of the key.
	 * @param isUp
	 *            whether the key is up or down.
	 * @return if the key is processed in this listener then return true. The
	 *         key will not be processed another applications.
	 */
	boolean hookedKey(int vkey, int modifier, boolean isUp);
}

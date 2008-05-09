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

package org.eclipse.actf.model.dom.dombycom;

/**
 * INodeExSound interface defines the methods to be implemented by the sound
 * object.
 */
public interface INodeExSound {
	/**
	 * The minimum volume value. Each implementation convert the 0-1090 scale to
	 * the original scale.
	 */
	int VOLUME_MIN = 0;
	/**
	 * The maximum volume value. Each implementation convert the 0-1000 scale to
	 * the original scale.
	 */
	int VOLUME_MAX = 1000;

	/**
	 * @return whether the sound object is muted or not.
	 */
	boolean getMuteState();

	/**
	 * @param flag whether the sound object will be muted or not.
	 * @return whether the operation is succeeded or not.
	 */
	boolean muteMedia(boolean flag);

	// 0 -- 1000
	/**
	 * @return the volume.
	 */
	int getVolume();

	/**
	 * @param val the volume
	 * @return whether the operation is succeeded or not.
	 */
	boolean setVolume(int val);
}

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
 * INodeExVideo interface defines the methods to be implemented by the video
 * object.
 */
public interface INodeExVideo {
	/**
	 * The enumeration of the state of the video.
	 */
	public enum VideoState {
		STATE_UNKNOWN, //
		STATE_PLAY, //
		STATE_STOP, //
		STATE_PAUSE, //
		STATE_FASTFORWARD, //
		STATE_FASTREVERSE, //
		STATE_WAITING
	};

	/**
	 * @return the current state.
	 */
	VideoState getCurrentState();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean previousTrack();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean nextTrack();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean stopMedia();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean playMedia();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean pauseMedia();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean fastReverse();

	/**
	 * @return whether the operation is succeeded or not.
	 */
	boolean fastForward();

	/**
	 * @return the current playing position in seconds.
	 */
	double getCurrentPosition();

	/**
	 * @return the total media length in seconds.
	 */
	double getTotalLength();

	/**
	 * @return the HTML element corresponding to the video object.
	 */
	INodeEx getReferenceNode();
}

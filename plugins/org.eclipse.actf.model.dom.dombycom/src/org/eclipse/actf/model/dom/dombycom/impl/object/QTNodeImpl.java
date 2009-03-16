/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.object;

import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

@SuppressWarnings("nls")
class QTNodeImpl extends MediaObjectImpl {

	boolean pause = false;

	private VideoState currentState = VideoState.STATE_UNKNOWN;

	QTNodeImpl(NodeImpl baseNode, IDispatch inode) {
		super(baseNode, inode);
	}

	private Object exec0(String cmd) {
		try {
			return inode.invoke0(cmd);
		} catch (DispatchException e) {
		}
		return null;
	}

	private Object exec1(String cmd, Object obj) {
		try {
			return inode.invoke1(cmd, obj);
		} catch (DispatchException e) {
		}
		return null;
	}

	public boolean getMuteState() {
		Object bool = exec0("GetMute");
		if (bool != null)
			return ((Integer) bool).intValue() != 0;
		else
			return false;
	}

	public int getVolume() {
		// range is 0-256
		// mapping to 0-1000
		Object volume = exec0("GetVolume");

		if (volume != null)
			return ((Integer) volume).intValue() * 1000 / 256;
		else
			return 0;
	}

	public boolean muteMedia(boolean flag) {
		exec1("setMute", Boolean.valueOf(flag));
		return true;
	}

	public boolean setVolume(int val) {
		int volume = val * 256 / 1000;
		exec1("SetVolume", volume);
		return true;
	}

	public boolean fastForward() {
		// TODO
		return false;
	}

	public boolean fastReverse() {
		exec0("Rewind");
		return true;
	}

	public double getCurrentPosition() {
		Object time = exec0("GetTime");
		Object scale = exec0("GetTimeScale");

		if (time != null)
			return ((Integer) time).doubleValue()
					/ ((Integer) scale).doubleValue();
		else
			return 0;
	}

	public VideoState getCurrentState() {
		return currentState;
	}

	public double getTotalLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean nextTrack() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean pauseMedia() {
		if (currentState == VideoState.STATE_PAUSE)
			return playMedia();
		exec0("Stop");
		currentState = VideoState.STATE_PAUSE;
		return true;
	}

	public boolean playMedia() {
		exec0("Play");
		currentState = VideoState.STATE_PLAY;
		return true;
	}

	public boolean previousTrack() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopMedia() {
		exec0("Stop");
		exec1("SetTime", 0);
		currentState = VideoState.STATE_STOP;
		return true;
	}
}

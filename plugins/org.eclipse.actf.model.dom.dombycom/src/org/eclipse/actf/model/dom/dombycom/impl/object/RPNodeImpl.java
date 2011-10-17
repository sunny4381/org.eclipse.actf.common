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
class RPNodeImpl extends MediaObjectImpl {

	// Holding internal state because Real Player's state
	// is not changed right after executing command

	private boolean pause = false;

	private boolean play = false;

	private boolean stop = false;

	private boolean mute = false;

	RPNodeImpl(NodeImpl baseNode, IDispatch inode) {
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

		if (mute) {
			mute = false;
			return true;
		}

		if (bool != null)
			return ((Boolean) bool).booleanValue();
		else
			return false;
	}

	public int getVolume() {
		// range is 0-100
		// mapping to 0-1000
		Object volume = exec0("GetVolume");

		if (volume != null)
			return ((Short) volume).intValue() * 1000 / 100;
		else
			return 0;
	}

	public boolean muteMedia(boolean flag) {
		// cannot mute media with "true", but can mute media with "1"
		// [ exec1("SetMute", true); ] x
		// [ exec1("SetMute", 1); ] o
		// [ exec1("SetMute", false);] o

		exec1("SetMute", flag ? 1 : 0);
		mute = true;
		return true;
	}

	public boolean setVolume(int val) {
		int volume = val * 100 / 1000;
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
		Object time = exec0("GetPosition");

		if (time != null)
			return ((Integer) time).doubleValue() / 1000;
		else
			return 0;
	}
	
	public boolean setCurrentPosition(double pos) {
		//
		return false;
	}
		
	public VideoState getCurrentState() {
		Object state = exec0("GetPlayState");

		if (pause) {
			pause = play = stop = false;
			return VideoState.STATE_PAUSE;
		}
		if (play) {
			pause = play = stop = false;
			return VideoState.STATE_PLAY;
		}
		if (stop) {
			pause = play = stop = false;
			return VideoState.STATE_STOP;
		}

		if (state != null) {
			switch (((Integer) state).intValue()) {
			case 0:
				return VideoState.STATE_STOP;
			case 1:
			case 2:
				return VideoState.STATE_WAITING;
			case 3:
				return VideoState.STATE_PLAY;
			case 4:
				return VideoState.STATE_PAUSE;
			case 5:
				return VideoState.STATE_WAITING;
			}
		}
		return VideoState.STATE_UNKNOWN;
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
		if (getCurrentState() == VideoState.STATE_PAUSE)
			return playMedia();
		exec0("DoPause");
		pause = true;
		return true;
	}

	public boolean playMedia() {
		exec0("DoPlay");
		play = true;
		return true;
	}

	public boolean previousTrack() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopMedia() {
		exec0("DoStop");
		stop = true;
		return true;
	}
}

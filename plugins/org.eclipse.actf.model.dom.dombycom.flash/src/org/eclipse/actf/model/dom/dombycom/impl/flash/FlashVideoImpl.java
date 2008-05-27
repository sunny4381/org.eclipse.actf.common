/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo;
import org.eclipse.actf.model.flash.ASNode;
import org.eclipse.actf.model.flash.IASBridge;
import org.eclipse.actf.model.flash.IFlashConst;

class FlashVideoImpl implements INodeExVideo, IFlashConst {
	private final FlashTopNodeImpl swf;
	private final String target;
	private final IASBridge player;
	// Some sort of hacking solution!!!
	private VideoState currentState = VideoState.STATE_UNKNOWN;

	public String getTarget() {
		return target;
	}

	FlashVideoImpl(FlashTopNodeImpl swf, ASNode node) {
		this.swf = swf;
		this.target = node.getTarget();
		this.player = node.getPlayer();
	}

	public boolean previousTrack() {
		return false;
	}

	public boolean nextTrack() {
		return false;
	}

	public boolean stopMedia() {
		player.callMethod(getTarget(), M_STOP);
		currentState = VideoState.STATE_STOP;
		return true;
	}

	public boolean playMedia() {
		player.callMethod(getTarget(), M_PLAY);
		currentState = VideoState.STATE_PLAY;
		return true;
	}

	public boolean pauseMedia() {
		player.callMethod(getTarget(), M_PAUSE);
		currentState = VideoState.STATE_PAUSE;
		return true;
	}

	public boolean fastReverse() {
		return false;
	}

	public boolean fastForward() {
		return false;
	}

	public double getCurrentPosition() {
		Object o = player.callMethod(getTarget(), M_GET_CURRENT_POSITION);
		if (o instanceof Double) {
			return ((Double) o).doubleValue();
		} else if (o instanceof Float) {
			return ((Float) o).floatValue();
		} else if (o instanceof Integer) {
			return ((Integer) o).intValue();
		}
		return 0;
	}

	public double getTotalLength() {
		return -1;
	}

	public VideoState getCurrentState() {
		// TODO
		return currentState;
	}

	public INodeEx getReferenceNode() {
		// Returns the top node (maybe a makeshift).
		return swf;
	}
}

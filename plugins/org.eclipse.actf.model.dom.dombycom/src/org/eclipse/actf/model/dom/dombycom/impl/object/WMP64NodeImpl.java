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

package org.eclipse.actf.model.dom.dombycom.impl.object;

import org.eclipse.actf.model.dom.dombycom.INodeExSound;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;




class WMP64NodeImpl extends MediaObjectImpl {
    // This flag is somewhat a workaround.
    // Even if we put "Mute" property to "true",
    // it does not affect the value to be obtained.
    // Therefore, we remember the mute state by ourselves.
    private boolean muteState;

    WMP64NodeImpl(NodeImpl baseNode, IDispatch idisp) {
        super(baseNode, idisp);
    }

    private boolean execControls(String cmd) {
        try {
            inode.invoke0(cmd);
            return true;
        } catch (DispatchException e) {
        }
        return false;
    }

    private boolean putSettings(String prop, Object val) {
        try {
            inode.put(prop, val);
            return true;
        } catch (DispatchException e) {
        }
        return false;
    }

    private Object getSettings(String prop) {
        return Helper.get(inode, prop);
    }

    public boolean fastForward() {
        return execControls("fastForward");
    }

    public boolean fastReverse() {
        return execControls("fastReverse");
    }

    public double getCurrentPosition() {
        Object o = Helper.get(inode, "currentPosition");
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

    public boolean nextTrack() {
        return execControls("next");
    }

    public boolean pauseMedia() {
        return execControls("pause");
    }

    public boolean playMedia() {
        return execControls("play");
    }

    public boolean previousTrack() {
        return execControls("previous");
    }

    public boolean stopMedia() {
        return execControls("stop");
    }

    public int getVolume() {
        Object o = getSettings("Volume");
        if (!(o instanceof Integer))
            return -1;
        int vol = ((Integer) o).intValue();
        // WMP64 volume range is 0 -- -10000. Default is -600.  We rescale it to 0 -- -2000
        if (vol < -2000) {
            return INodeExSound.VOLUME_MIN;
        }
        return (((vol + 2000) * (INodeExSound.VOLUME_MAX - INodeExSound.VOLUME_MIN) / 2000)
                + INodeExSound.VOLUME_MIN);
    }

    public boolean muteMedia(boolean flag) {
        if (putSettings("Mute", flag)) {
            muteState = flag;
            return true;
        } else {
            return false;
        }
    }

    public boolean getMuteState() {
        Object o = getSettings("Mute");
        if (!(o instanceof Boolean))
            return false;
        return ((Boolean) o).booleanValue() || muteState;
    }

    public boolean setVolume(int val) {
        // First, scale our volume range to 2000 -- 0;
        val = (((val - INodeExSound.VOLUME_MIN) * 2000)
               / (INodeExSound.VOLUME_MAX - INodeExSound.VOLUME_MIN));
        if (val == 0) {
            val = - 10000;
        } else {
            val -= 2000;
        }
        return putSettings("Volume", Integer.valueOf(val));
    }

    public VideoState getCurrentState() {
        Object o = Helper.get(inode, "PlayState");
        if (o instanceof Integer) {
            int wmpst = (Integer) o;
            switch (wmpst) {
            case 0:
                return VideoState.STATE_STOP;
            case 1:
                return VideoState.STATE_PAUSE;
            case 2:
                return VideoState.STATE_PLAY;
            case 3:
                return VideoState.STATE_WAITING;
            case 4:
            case 6:
                return VideoState.STATE_FASTFORWARD;
            case 5:
            case 7:
                return VideoState.STATE_FASTREVERSE;
            default:
                return VideoState.STATE_STOP;
            }
        } else {
            return VideoState.STATE_UNKNOWN;
        }
    }
}

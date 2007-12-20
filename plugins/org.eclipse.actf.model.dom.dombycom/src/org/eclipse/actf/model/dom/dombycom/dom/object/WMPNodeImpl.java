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

package org.eclipse.actf.model.dom.dombycom.dom.object;

import org.eclipse.actf.ai.comclutch.win32.DispatchException;
import org.eclipse.actf.ai.comclutch.win32.IDispatch;
import org.eclipse.actf.model.dom.dombycom.INodeExSound;
import org.eclipse.actf.model.dom.dombycom.dom.Helper;
import org.eclipse.actf.model.dom.dombycom.dom.NodeImpl;




class WMPNodeImpl extends MediaObjectImpl {
    private final IDispatch controls;
    private final IDispatch settings;

    WMPNodeImpl(NodeImpl baseNode, IDispatch idisp, IDispatch controls) {
        super(baseNode, idisp);
        this.controls = controls;
        Object o = Helper.get(idisp, "settings");
        if (o instanceof IDispatch) {
            this.settings = (IDispatch) o;
        } else {
            this.settings = null;
        }
    }

    private boolean execControls(String cmd) {
        try {
            controls.invoke0(cmd);
            return true;
        } catch (DispatchException e) {
        }
        return false;
    }

    private boolean putSettings(String prop, Object val) {
        if (settings == null) return false;
        try {
            settings.put(prop, val);
            return true;
        } catch (DispatchException e) {
        }
        return false;
    }

    private Object getSettings(String prop) {
        if (settings == null) return null;
        return Helper.get(settings, prop);
    }

    public boolean fastForward() {
        return execControls("fastForward");
    }

    public boolean fastReverse() {
        return execControls("fastReverse");
    }

    public double getCurrentPosition() {
        Object o = Helper.get(controls, "currentPosition");
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
        Object o = getSettings("volume");
        if (!(o instanceof Integer)) return -1;
        int vol = ((Integer) o).intValue();
        return ((vol * ((INodeExSound.VOLUME_MAX - INodeExSound.VOLUME_MIN) / 100))
                + INodeExSound.VOLUME_MIN);
    }

    public boolean muteMedia(boolean flag) {
        return putSettings("mute", flag);
    }

    public boolean setVolume(int val) {
        val = (((val - INodeExSound.VOLUME_MIN) * 100)
               / (INodeExSound.VOLUME_MAX - INodeExSound.VOLUME_MIN));
        return putSettings("volume", Integer.valueOf(val));
    }

    public VideoState getCurrentState() {
        Object o = Helper.get(inode, "playState");
        if (o instanceof Integer) {
            int wmpst = (Integer) o;
            switch (wmpst) {
            case 0:
                return VideoState.STATE_UNKNOWN;
            case 1:
                return VideoState.STATE_STOP;
            case 2:
                return VideoState.STATE_PAUSE;
            case 3:
                return VideoState.STATE_PLAY;
            case 4:
                return VideoState.STATE_FASTFORWARD;
            case 5:
                return VideoState.STATE_FASTREVERSE;
            case 6:
            case 7:
                return VideoState.STATE_WAITING;
            default:
                return VideoState.STATE_STOP;
            }
        } else {
            return VideoState.STATE_UNKNOWN;
        }
    }

    public boolean getMuteState() {
        Object o = getSettings("mute");
        if (!(o instanceof Boolean)) return false;
        return ((Boolean) o).booleanValue();
    }
}

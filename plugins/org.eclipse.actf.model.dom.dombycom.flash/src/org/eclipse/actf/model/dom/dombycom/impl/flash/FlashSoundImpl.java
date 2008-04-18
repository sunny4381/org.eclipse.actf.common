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

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import org.eclipse.actf.model.dom.dombycom.INodeExSound;
import org.eclipse.actf.model.flash.util.ASObject;


class FlashSoundImpl implements INodeExSound {
    private int volBeforeMuted = -1;
    private final FlashTopNodeImpl swf;
    private final String target;
    
    public String getTarget() {
        return target;
    }

    FlashSoundImpl(FlashTopNodeImpl swf, ASObject nodeASObj) {
        this.swf = swf;
        this.target = (String) nodeASObj.get("target");
    }

    public boolean muteMedia(boolean flag) {
        if (flag) {
            if (volBeforeMuted < 0) {
                volBeforeMuted = getVolume();
                return setVolume(0);
            }
        } else {
            if (volBeforeMuted >= 0) {
                boolean success = setVolume(volBeforeMuted);
                volBeforeMuted = -1;
                return success;
            }
        }
        return true;
    }

    public int getVolume() {
        Object o = swf.callMethod(new Object[] { getTarget(), "getVolume" });
        if (o instanceof Double) {
            return ((Double) o).intValue() * 10;
        } else if (o instanceof Integer) {
            return ((Integer) o).intValue() * 10;
        }
        return -1;
    }

    public boolean setVolume(int val) {
        val = val / 10;
        swf.callMethod(new Object[] { getTarget(), "setVolume", Integer.valueOf(val) });
        return true;
    }

    public boolean getMuteState() {
        if (volBeforeMuted < 0) return false;
        return true;
    }
}

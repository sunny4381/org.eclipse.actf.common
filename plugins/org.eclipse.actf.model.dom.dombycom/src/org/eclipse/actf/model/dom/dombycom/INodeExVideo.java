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



public interface INodeExVideo {
    public enum VideoState {
        STATE_UNKNOWN,
        STATE_PLAY,
        STATE_STOP,
        STATE_PAUSE,
        STATE_FASTFORWARD,
        STATE_FASTREVERSE,
        STATE_WAITING
    };

    VideoState getCurrentState();

    boolean previousTrack();

    boolean nextTrack();

    boolean stopMedia();

    boolean playMedia();

    boolean pauseMedia();

    boolean fastReverse();

    boolean fastForward();

    double getCurrentPosition();

    double getTotalLength();

    INodeEx getReferenceNode();
}

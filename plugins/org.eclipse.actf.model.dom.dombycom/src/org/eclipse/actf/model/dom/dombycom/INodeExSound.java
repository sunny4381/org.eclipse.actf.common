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


public interface INodeExSound {
    int VOLUME_MIN = 0;
    int VOLUME_MAX = 1000;

    boolean getMuteState();
    boolean muteMedia(boolean flag);

    // 0 -- 1000
    int getVolume();

    boolean setVolume(int val);
}

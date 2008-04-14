/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA -initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.bridge;

import java.io.InputStream;


public interface IWaXcoding {
    boolean start(boolean initLogger);
    boolean stop();
    
    String getSecret(String id, boolean remove);

    void setPort(int port);
    int getPort();
    void setExternalProxyFlag(boolean flag);
    void setExternalProxy(String host, int port);
    void setMaxConnection(int connections);
    void setTimeout(int timeout);
    void setSWFBootloaderFlag(boolean flag);
    void setSWFBootloader(InputStream is);
    void setSWFBridgeInit(InputStream is);
    void setSWFTranscodingFlag(boolean flag);
    void setSWFTranscodingMinimumVersion(int version);
    void setSWFTranscodingImposedFile(InputStream is);
    
}

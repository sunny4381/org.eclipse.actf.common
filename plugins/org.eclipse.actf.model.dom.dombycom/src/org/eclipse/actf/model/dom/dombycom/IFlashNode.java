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


public interface IFlashNode extends INodeEx {
    short FLASH_NODE = 120;
    int INVALID_DEPTH = -16384;

    String getTarget();
    IFlashNode getNodeFromPath(String path);
    IFlashNode getNodeAtDepth(int depth);
    IFlashNode[] getInnerNodes();
    int getDepth();
    int getCurrentFrame();

    IFlashNode[] translate();

    IMSAANode getMSAA();
    void repairFlash();

    INodeEx getBaseNode();
}

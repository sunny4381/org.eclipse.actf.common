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

import java.util.ArrayList;



public class AnalyzedResult {
    private static final INodeExVideo[] emptyVideoArray = new INodeExVideo[0]; 
    private static final INodeExSound[] emptySoundArray = new INodeExSound[0]; 
    private ArrayList<INodeExVideo> videoList = new ArrayList<INodeExVideo>();
    private ArrayList<INodeExSound> soundList = new ArrayList<INodeExSound>();
    
    private static final INodeEx[] emptyAccessKeyArray = new INodeEx[0];
    private ArrayList<INodeEx> accessKeyList = new ArrayList<INodeEx>();
    
    private static final IFlashNode[] emptyFlashTopNodeArray = new IFlashNode[0];
    private ArrayList<IFlashNode> flashNodeList = new ArrayList<IFlashNode>();
    
    public void addFlashTopNode(IFlashNode f) {
        flashNodeList.add(f);
    }
    
    public void addVideo(INodeExVideo v) {
        videoList.add(v);
    }

    public void addSound(INodeExSound s) {
        soundList.add(s);
    }
    
    public void addAccessKey(INodeEx a) {
        accessKeyList.add(a);
    }
    
    public INodeExVideo[] getVideoNodes() {
        return videoList.toArray(emptyVideoArray);
    }

    public INodeExSound[] getSoundNodes() {
        return soundList.toArray(emptySoundArray);
    }
    
    public INodeEx[] getAccessKeyNodes() {
        return accessKeyList.toArray(emptyAccessKeyArray);
    }
    
    public IFlashNode[] getFlashTopNodes() {
        return flashNodeList.toArray(emptyFlashTopNodeArray);
    }
}

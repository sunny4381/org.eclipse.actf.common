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

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.INodeExSound;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo;
import org.eclipse.actf.model.dom.dombycom.dom.NodeImpl;
import org.eclipse.actf.model.dom.dombycom.dom.html.ElementImpl;
import org.eclipse.actf.util.comclutch.win32.IDispatch;
import org.eclipse.actf.util.vocab.AbstractTerms;





abstract class MediaObjectImpl extends ElementImpl implements INodeExVideo, INodeExSound {
    @Override
    public AnalyzedResult analyze(AnalyzedResult ar) {
        ar.addVideo(this);
        ar.addSound(this);
        return ar;
    }

    public INodeEx getReferenceNode() {
        return this;
    }

    @Override
    public AbstractTerms getTerms() {
        return MediaObjectTerms.getInstance();
    }

    protected MediaObjectImpl(NodeImpl baseNode, IDispatch idisp) {
        super(baseNode, idisp);
   }
}

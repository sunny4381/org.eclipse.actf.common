/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.object;

import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.vocab.IEvalTarget;




class MediaObjectTerms extends AbstractTerms {
    private static final MediaObjectTerms instance = new MediaObjectTerms();

    @Override
    public boolean hasContent(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isMedia(IEvalTarget node) {
        return true;
    }

    @Override
    public boolean isAlterable(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isValidNode(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isVisibleNode(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isEmbeddedObject(IEvalTarget target) {
        return true;
    }

    static MediaObjectTerms getInstance() {
        return instance;
    }

}

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

package org.eclipse.actf.util.internal.vocab.impl;

import org.eclipse.actf.util.vocab.IProposition;



public class ImplyOperator extends OrOperator {

    public ImplyOperator(IProposition left, IProposition right) {
        super(new NotOperator(left), right);
    }

    @Override
    public String getName() {
        return "implies"; //$NON-NLS-1$
    }
}

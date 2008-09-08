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

import org.eclipse.actf.util.vocab.IOperator1;
import org.eclipse.actf.util.vocab.IProposition;



public abstract class AbstractOperator1 implements IOperator1 {

    private IProposition proposition;
    
    public AbstractOperator1(IProposition proposition){
        this.proposition = proposition;
    }
    
    public IProposition getProposition() {
        return proposition;
    }
    
    public void setProposition(IProposition proposition){
        this.proposition = proposition;
    }
}

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

import org.eclipse.actf.util.vocab.IEvalTarget;
import org.eclipse.actf.util.vocab.IProposition;



public class FalseImpl implements IProposition {
    
    private static FalseImpl instance = new FalseImpl();

    private FalseImpl(){
    }
    
    public boolean eval(IEvalTarget node){
        return false;
    }

    public String getName() {
        return "false"; //$NON-NLS-1$
    }

    public static FalseImpl getInstance() {
        return instance; 
    }

}

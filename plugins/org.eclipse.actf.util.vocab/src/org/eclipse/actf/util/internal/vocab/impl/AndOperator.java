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



public class AndOperator extends AbstractOperator {

    public AndOperator(IProposition... args){
        super(args);
    }

    public boolean eval(IEvalTarget node){
        for(int i=0; i<size(); i++){
            IProposition p = get(i);
            boolean result = p.eval(node);
            if(result == false){
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return "and"; //$NON-NLS-1$
    }

}

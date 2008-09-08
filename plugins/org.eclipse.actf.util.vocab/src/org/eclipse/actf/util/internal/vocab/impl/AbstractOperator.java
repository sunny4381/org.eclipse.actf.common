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

import java.util.ArrayList;

import org.eclipse.actf.util.vocab.IOperator;
import org.eclipse.actf.util.vocab.IProposition;




public abstract class AbstractOperator implements IOperator {

    private ArrayList<IProposition> props = new ArrayList<IProposition>();
    
    public AbstractOperator(IProposition... args){
        for(IProposition p: args){
            props.add(p);
        }
    }
    
    public void add(IProposition prop){
        props.add(prop);
    }
    
    public int size(){
        return props.size();
    }
    
    public IProposition get(int index){
        return props.get(index);
    }
}

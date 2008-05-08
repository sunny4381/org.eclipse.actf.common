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

package org.eclipse.actf.util.vocab;


/**
 * IOperator1 interface defines the methods to be implemented by the operator
 * which has an argument. For example, "not operator".
 */
public interface IOperator1 extends IProposition{
    /**
     * @return the proposition to be used for argument of the operator.
     */
    IProposition getProposition();
    
    
    /**
     * @param prop the proposition to be used for argument of the operator.
     */
    void setProposition(IProposition prop);
}

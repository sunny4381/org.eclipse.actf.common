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
 * IOperator interface defines the methods to be implemented by the operator
 * between propositions. For example, "and operator" and "or operator".
 */
public interface IOperator extends IProposition {
	/**
	 * @return the size of the arguments for the operator.
	 */
	int size();

	/**
	 * @param index
	 *            the index of the proposition.
	 * @return the instance specified by the index.
	 */
	IProposition get(int index);

	/**
	 * Add a proposition to the arguments of this operator.
	 * 
	 * @param prop
	 *            the proposition to be added.
	 */
	void add(IProposition prop);
}

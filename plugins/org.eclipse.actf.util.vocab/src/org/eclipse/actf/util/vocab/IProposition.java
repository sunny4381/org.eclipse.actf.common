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
 * IProposition interface the methods to be implemented by the representation of
 * a proposition. It returns true or false in evaluating.
 */
public interface IProposition {
	/**
	 * @return the name of the proposition.
	 */
	String getName();

	/**
	 * @param target
	 *            the target to be evaluated.
	 * @return whether the target is matched with the proposition.
	 */
	boolean eval(IEvalTarget target);
}

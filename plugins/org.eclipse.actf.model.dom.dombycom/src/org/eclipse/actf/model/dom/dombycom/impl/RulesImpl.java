/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.dombycom.impl;

import org.eclipse.actf.model.dom.dombycom.IRule;
import org.eclipse.actf.model.dom.dombycom.IRules;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class RulesImpl implements IRules {

	private IDispatch inodeCollection;

	public RulesImpl(IDispatch inodeCollection) {
		this.inodeCollection = inodeCollection;
	}

	public int getLength() {
		Integer i = (Integer) Helper.get(inodeCollection, "length"); //$NON-NLS-1$
		if (i == null)
			return 0;
		return i.intValue();
	}

	public IRule item(int index) {
		try {
			IDispatch i = (IDispatch) inodeCollection.invoke1(
					"item", Integer.valueOf(index)); //$NON-NLS-1$
			if (i != null) {
				return new RuleImpl(i);
			}
		} catch (Exception e) {
		}
		return null;
	}

}

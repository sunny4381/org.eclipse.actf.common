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
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class RuleImpl implements IRule {

	private IDispatch idisp;

	public RuleImpl(IDispatch idisp) {
		this.idisp = idisp;
	}

	public String getSelectorText() {
		return (String) Helper.get(idisp, "selectorText");
	}

	public IStyle getStyle() {
		IDispatch i = (IDispatch) Helper.get(idisp, "style");
		if (i != null) {
			return new StyleImpl(i);
		}
		return null;
	}

}

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl;

import org.eclipse.actf.model.dom.dombycom.IRules;
import org.eclipse.actf.model.dom.dombycom.IStyleSheet;
import org.eclipse.actf.model.dom.dombycom.IStyleSheets;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class StyleSheetImpl implements IStyleSheet {
	private IDispatch idisp;

	public StyleSheetImpl(IDispatch idispatch) {
		this.idisp = idispatch;
	}

	public void addRule(String selector, String style) {
		idisp.invoke("addRule", new Object[] { selector, style }); //$NON-NLS-1$
	}

	public String getHref() {
		return (String) Helper.get(idisp, "href");
	}

	public String getTitle() {
		return (String) Helper.get(idisp, "title");
	}

	public IStyleSheet getParentStyleSheet() {
		IDispatch i = (IDispatch) Helper.get(idisp, "parentStyleSheet");
		if (i != null) {
			return new StyleSheetImpl(i);
		}
		return null;
	}

	public IStyleSheets getImports() {
		IDispatch i = (IDispatch) Helper.get(idisp, "imports");
		if (i != null) {
			return new StyleSheetsImpl(i);
		}
		return null;
	}

	public IRules getRules() {
		IDispatch i = (IDispatch) Helper.get(idisp, "rules");
		if (i != null) {
			return new RulesImpl(i);
		}
		return null;
	}

	public String getCssText() {
		return (String) Helper.get(idisp, "cssText");
	}
}

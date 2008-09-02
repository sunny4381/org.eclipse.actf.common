/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.msaa;

import org.eclipse.actf.util.win32.comclutch.IDispatch;

public interface IAccessible extends IDispatch{
	String getAccKeyboardShortcut(int child);

	int getAccState(int child);

	boolean accDoDefaultAction(int child);

	int getAccChildCount();

	int getAccRole(int child);

	String getAccName(int child);

	void accSelect(int selflagTakefocus, int child);

	String getAccDescription(int child);
}

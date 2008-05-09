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
package org.eclipse.actf.util.win32;

public interface IAccessibleObject {
	boolean doDefaultAction();
	String getAccDescription();
	String getAccKeyboardShortcut();
	String getAccName();
	int getAccRole();
	int getAccState();
	int getChildCount();
	IAccessibleObject[] getChildren();
	String getClassName();
	int getWindow();
	boolean select(int selflagTakefocus);
	int getPtr();
}

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

import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.msaa.IAccessible;
import org.eclipse.actf.util.win32.msaa.MSAA;


public class AccessibleObject implements IAccessibleObject{

	IAccessible parent, iacc;
	int childId = MSAA.CHILDID_SELF;
	
	public AccessibleObject(IAccessible iacc) {
		this.iacc = iacc;
	}
	
	public AccessibleObject(IAccessible parent, int childId) {
		this.parent = parent;
		this.childId = childId;
	}
	
	private int getInt() {
		return childId;
	}
	
	public IAccessible getIAccessible() {
		if (parent != null)
			return parent;
		return iacc;
	}

	public String getAccKeyboardShortcut() {
		return getIAccessible().getAccKeyboardShortcut(getInt());
	}

	public int getAccRole() {
		return getIAccessible().getAccRole(getInt());
	}

	public String getAccDescription() {
		return getIAccessible().getAccDescription(getInt());
	}

	public int getAccState() {
		return getIAccessible().getAccState(getInt());
	}

	public boolean doDefaultAction() {
		try {
			return getIAccessible().accDoDefaultAction(getInt());
		} catch (Exception e) {
		}
		return false;
	}

	public int getChildCount() {
		if (iacc != null) {
			return iacc.getAccChildCount();
		}
		return 0;
	}

	public String getAccName() {
		return getIAccessible().getAccName(getInt());
	}

	public boolean select(int selflagTakefocus) {
		getIAccessible().accSelect(selflagTakefocus, getInt());
		return true;
	}


	private AccessibleObject[] cachedChildren = new AccessibleObject[0];

	public IAccessibleObject[] getChildren() {
		int childCount = Math.max(0, getChildCount());
		if (childCount == cachedChildren.length) {
			return cachedChildren;
		}
		if (childCount > 32 * 1024) {
			System.err
					.println("Too many children(" + childCount + "), we don't fectch."); //$NON-NLS-1$ //$NON-NLS-2$

			return new AccessibleObject[0];
		}
		cachedChildren = new AccessibleObject[childCount];
		if (childCount > 0) {
			Object[] children = MSAA.getAccessibleChildren(getIAccessible(), 0, childCount);
			for (int i = 0; i < childCount; i++) {
				if (children[i] != null) {
					if (children[i] instanceof Integer) {
						cachedChildren[i] = new AccessibleObject(this.getIAccessible(), (Integer) children[i]);
					} else if (children[i] instanceof IDispatch) {
						IAccessible iacc = ComService.newIAccessible((IDispatch) children[i]);
						//System.out.println(((IDispatch) children[i]).getPtr()+", "+iacc.getPtr());
						cachedChildren[i] = new AccessibleObject(iacc);
					}
				}
			}
		}
		return cachedChildren;
	}

	private String strClassName;

	public String getClassName() {
		if (null == strClassName) {
			long hwnd = this.getWindow();
			strClassName = null;
			if (0 != hwnd) {
				strClassName = WindowUtil.GetWindowClassName((int) hwnd);
			}
		}
		return strClassName;
	}

	private long accWindow = -1;

	public int getWindow() {
		if (-1 == accWindow) {
			try {
				accWindow = MSAA.WindowFromAccessibleObject(getIAccessible().getPtr());
			} catch (Exception e) {
				accWindow = 0;
			}
		}
		return (int) accWindow;
	}

	public int getPtr() {
		return (int) getIAccessible().getPtr();
	}

}

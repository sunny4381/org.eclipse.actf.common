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

/**
 * FlashMSAAObject is a wrapper class of native MSAA object for Flash content
 */
public class FlashMSAAObject {

	FlashMSAAObject parent;
	IAccessible iacc;
	int childId = MSAA.CHILDID_SELF;

	/**
	 * @param iacc
	 *            native wrapped IAccessible object
	 */
	public FlashMSAAObject(IAccessible iacc) {
		this.iacc = iacc;
	}

	/**
	 * @param parent
	 * @param childId
	 *            the child ID to be used to obtain the actual child object from
	 *            the parent.
	 */
	public FlashMSAAObject(FlashMSAAObject parent, int childId) {
		this.parent = parent;
		this.childId = childId;
	}

	private int getChildId() {
		return childId;
	}

	/**
	 * @return the native wrapped IAccessible object.
	 */
	public IAccessible getIAccessible() {
		if (iacc != null)
			return iacc;
		if (parent != null)
			return parent.getIAccessible();
		return null;
	}

	/**
	 * @see IAccessible#getAccKeyboardShortcut(int)
	 */
	public String getAccKeyboardShortcut() {
		return getIAccessible().getAccKeyboardShortcut(getChildId());
	}

	/**
	 * @see IAccessible#getAccRole(int)
	 */
	public int getAccRole() {
		return getIAccessible().getAccRole(getChildId());
	}

	/**
	 * @see IAccessible#getAccDescription(int)
	 */
	public String getAccDescription() {
		return getIAccessible().getAccDescription(getChildId());
	}

	/**
	 * @see IAccessible#getAccState(int)
	 */
	public int getAccState() {
		return getIAccessible().getAccState(getChildId());
	}

	/**
	 * @see IAccessible#accDoDefaultAction(int)
	 */
	public boolean doDefaultAction() {
		try {
			return getIAccessible().accDoDefaultAction(getChildId());
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * @see IAccessible#getAccChildCount()
	 */
	public int getChildCount() {
		if (iacc != null) {
			return iacc.getAccChildCount();
		}
		return 0;
	}

	/**
	 * @see IAccessible#getAccName(int)
	 */
	public String getAccName() {
		return getIAccessible().getAccName(getChildId());
	}

	/**
	 * @see IAccessible#accSelect(int, int)
	 */
	public boolean select(int selflagTakefocus) {
		getIAccessible().accSelect(selflagTakefocus, getChildId());
		return true;
	}

	private FlashMSAAObject[] cachedChildren = new FlashMSAAObject[0];

	/**
	 * @return an array of the children
	 */
	public FlashMSAAObject[] getChildren() {
		int childCount = Math.max(0, getChildCount());
		if (childCount == cachedChildren.length) {
			return cachedChildren;
		}
		if (childCount > 32 * 1024) {
			System.err
					.println("Too many children(" + childCount + "), we don't fectch."); //$NON-NLS-1$ //$NON-NLS-2$

			return new FlashMSAAObject[0];
		}
		cachedChildren = new FlashMSAAObject[childCount];
		if (childCount > 0) {
			Object[] children = MSAA.getAccessibleChildren(getIAccessible(), 0,
					childCount);
			for (int i = 0; i < childCount; i++) {
				if (children[i] != null) {
					if (children[i] instanceof Integer) {
						cachedChildren[i] = new FlashMSAAObject(this,
								(Integer) children[i]);
					} else if (children[i] instanceof IDispatch) {
						// IAccessible iacc = (IAccessible) ((IDispatch)
						// children[i]).queryInterface(IUnknown.IID_IAccessible);

						IAccessible iacc = ComService
								.newIAccessible((IDispatch) children[i]);

						cachedChildren[i] = new FlashMSAAObject(iacc);
					}
				}
			}
		}
		return cachedChildren;
	}

	private String strClassName;

	/**
	 * @return the window class name of the wrapped object (Windows native). If
	 *         class name can't be obtained from the wrapped object then return
	 *         null.
	 * @see WindowUtil#GetWindowClassName(int)
	 */
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

	/**
	 * @return the address of the window handle of the wrapped object. If window
	 *         handle can't be obtained from the wrapped object then return 0.
	 * @see MSAA#WindowFromAccessibleObject
	 */
	public int getWindow() {
		if (-1 == accWindow) {
			try {
				accWindow = MSAA.WindowFromAccessibleObject(getIAccessible()
						.getPtr());
			} catch (Exception e) {
				accWindow = 0;
			}
		}
		return (int) accWindow;
	}

	/**
	 * @return the native address of the wrapped object
	 */
	public int getPtr() {
		return (int) getIAccessible().getPtr();
	}

}

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
package org.eclipse.actf.util.win32.msaa.impl;

import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.RefInt;
import org.eclipse.actf.util.win32.comclutch.RefString;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;
import org.eclipse.actf.util.win32.comclutch.impl.IDispatchImpl;
import org.eclipse.actf.util.win32.comclutch.impl.IUnknownImpl;
import org.eclipse.actf.util.win32.msaa.IAccessible;

/**
 * 
 */
public class IAccessibleImpl extends IDispatchImpl implements IAccessible {

	private static final int ACC_PARENT = 0xffffec78;
	private static final int ACC_CHILDCOUNT = 0xffffec77;
	private static final int ACC_CHILD = 0xffffec76;
	private static final int ACC_NAME = 0xffffec75;
	private static final int ACC_VALUE = 0xffffec74;
	private static final int ACC_DESCRIPTION = 0xffffec73;
	private static final int ACC_ROLE = 0xffffec72;
	private static final int ACC_STATE = 0xffffec71;
	private static final int ACC_HELP = 0xffffec70;
	private static final int ACC_HELPTOPIC = 0xffffec6f;
	private static final int ACC_KEYBOARDSHORTCUT = 0xffffec6e;
	private static final int ACC_FOCUS = 0xffffec6d;
	private static final int ACC_SELECTION = 0xffffec6c;
	private static final int ACC_DEFAULTACTION = 0xffffec6b;
	private static final int ACC_SELECT = 0xffffec6a;
	private static final int ACC_LOCATION = 0xffffec69;
	private static final int ACC_NAVIGATE = 0xffffec68;
	private static final int ACC_HITTEST = 0xffffec67;
	private static final int ACC_DODEFAULTACTION = 0xffffec66;

	public IAccessibleImpl(IUnknownImpl iunkImpl) {
		super(iunkImpl);
	}

	public IAccessibleImpl(ResourceManager rm, long ptr, boolean permanent) {
		super(rm, ptr, permanent);
	}

	public synchronized String getAccDescription(int childId) {
		try {
			return (String) get(ACC_DESCRIPTION, new Object[] { childId });
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (acc desc): " + e);
		}
		return null;
	}

	public synchronized String getAccKeyboardShortcut(int childId) {
		try {
			return (String) get(ACC_KEYBOARDSHORTCUT, new Object[] { childId });
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (acc key): " + e);
		}
		return null;
	}

	public synchronized String getAccName(int childId) {
		try {
			return (String) get(ACC_NAME, new Object[] { childId });
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (acc name): " + e);
		}
		return null;
	}

	public synchronized int getAccRole(int childId) {
		try {
			return (Integer) get(ACC_ROLE, new Object[] { childId });
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (acc role): " + e);
		}
		return 0;
	}

	public synchronized int getAccState(int childId) {
		try {
			return (Integer) get(ACC_STATE, new Object[] { childId });
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (acc state): " + e);
		}
		return 0;
	}

	public synchronized int getAccChildCount() {
		try {
			return (Integer) get(ACC_CHILDCOUNT);
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (child count): " + e);
		}
		return 0;
	}

	public synchronized IAccessible getAccChild(int id) {
		try {
			IDispatch idisp = (IDispatch) get(ACC_CHILD, new Object[] { id });
			if (idisp == null) {
				return null;
			}
			return ComService.newIAccessible(idisp);
		} catch (DispatchException e) {
		}
		return null;
	}

	public synchronized boolean accDoDefaultAction(int childId) {
		try {
			return (Boolean) invoke1(ACC_DODEFAULTACTION, childId);
		} catch (DispatchException e) {
		}
		return false;
	}

	public synchronized void accSelect(int selflagTakefocus, int childId) {
		try {
			invoke(ACC_SELECT, new Object[] { selflagTakefocus, childId });
		} catch (DispatchException e) {
			// System.out.println(getPtr() + " (select: " + e);
		}
	}

	public synchronized IAccessible getAccParent() {
		try {
			IDispatch idisp = (IDispatch) get(ACC_PARENT);
			if (idisp == null) {
				return null;
			}
			return ComService.newIAccessible(idisp);
		} catch (DispatchException e) {
		}
		return null;
	}

	public synchronized String getAccValue(int childId) {
		try {
			return (String) get(ACC_VALUE, new Object[] { childId });
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized String getAccDefaultAction(int childId) {
		try {
			return (String) get(ACC_DEFAULTACTION, new Object[] { childId });
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized Object getAccFocus() {
		try {
			return get(ACC_FOCUS);
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized String getAccHelp(int childId) {
		try {
			return (String) get(ACC_HELP, new Object[] { childId });
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized int getAccHelpTopic(RefString helpFile, int childId) {
		try {
			return (Integer) get(ACC_HELPTOPIC, new Object[] { helpFile,
					childId });
		} catch (Exception e) {
		}
		return 0;
	}

	public synchronized Object accHitTest(int xLeft, int yTop) {
		try {
			return invoke(ACC_HITTEST, new Object[] { xLeft, yTop });
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized void accLocation(RefInt pxLeft, RefInt pyTop, RefInt pcxWidth,
			RefInt pcyHeight, int childId) {
		try {
			invoke(ACC_LOCATION, new Object[] { pxLeft, pyTop, pcxWidth,
					pcyHeight, childId });
		} catch (Exception e) {
		}
	}

	public synchronized Object accNavigate(int navDir, int start) {
		try {
			invoke(ACC_NAVIGATE, new Object[] { navDir, start });
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized Object getAccSelection() {
		try {
			return get(ACC_SELECTION);
		} catch (Exception e) {
		}
		return null;
	}

	public static IAccessible newIAccessible(ResourceManager rm,
			long ptr, boolean permanent) {
		IAccessible iacc = new IAccessibleImpl(rm, ptr, permanent);
		rm.addResource(iacc);
		return iacc;
	}

}

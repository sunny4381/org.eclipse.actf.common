/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *    Daisuke SATO
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.internal.ASBridge;
import org.eclipse.actf.model.flash.internal.Messages;
import org.eclipse.actf.model.flash.util.ASObject;
import org.eclipse.actf.model.flash.util.FlashMSAAUtil;
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;

public class FlashPlayer {

	private IDispatch idispFlash;
	private Object objMarker;

	public boolean isVisible = true;
	private static final String sidGetRootNode = "getRootNode", //$NON-NLS-1$
			sidGetNumDebugChildren = "getNumSuccessorNodes", //$NON-NLS-1$
			sidGetDebugChildren = "getSuccessorNodes", //$NON-NLS-1$
			sidGetNumChildren = "getNumChildNodes", //$NON-NLS-1$
			sidGetChildren = "getChildNodes", //$NON-NLS-1$
			sidGetInnerNodes = "getInnerNodes", //$NON-NLS-1$
			sidNewMarker = "newMarker", //$NON-NLS-1$
			sidSetMarker = "setMarker", //$NON-NLS-1$
			sidGetNodeFromPath = "getNodeFromPath",//$NON-NLS-1$
			sidCallMethod = "callMethodA"; //$NON-NLS-1$

	private ASBridge bridge;
	private FlashMSAAObject accessible;

	public FlashPlayer(IDispatch idisp) {
		idispFlash = idisp;
		accessible = FlashMSAAObjectFactory.getFlashMSAAObjectFromElement(idispFlash);
		bridge = ASBridge.getInstance(this);
	}
	
	public FlashMSAAObject getAccessible() {
		return accessible;
	}

	public static FlashPlayer getPlayerFromPtr(int ptr) {
		IUnknown accObject = ComService.newIUnknown(ResourceManager.newResourceManager(null), ptr, true);
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}
	
	public static FlashPlayer getPlayerFromWindow(int hwnd) {
		FlashMSAAObject accObject = FlashMSAAObjectFactory.getFlashMSAAObjectFromWindow(hwnd);
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}
	
	public static FlashPlayer getPlayerFromObject(FlashMSAAObject accObject) {
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

	public FlashNode getRootNode() {
		if (null != bridge) {
			Object result = invoke(sidGetRootNode);
			if (result instanceof ASObject) {
				return new FlashNode(null, this, (ASObject) result);
			}
		}
		return null;
	}

	public FlashNode getNodeFromPath(String path) {
		if (null != bridge) {
			Object result = invoke(sidGetNodeFromPath, path);
			if (result instanceof ASObject) {
				return new FlashNode(null, this, (ASObject) result);
			}
		}
		return null;
	}

	public boolean hasChild(FlashNode parentNode, boolean visual) {
		return hasChild(parentNode, visual, false);
	}

	public boolean hasChild(FlashNode parentNode, boolean visual,
			boolean debugMode) {
		if (visual) {
			return true;
		}
		if (null != bridge) {
			String sidMethod;
			if (visual) {
				sidMethod = sidGetNumDebugChildren;
			} else {
				sidMethod = debugMode ? sidGetNumDebugChildren
						: sidGetNumChildren;
			}
			Object result = invoke(sidMethod, parentNode.getTarget());
			if (result instanceof Integer) {
				return ((Integer) result).intValue() > 0;
			}
		}
		return false;
	}

	public FlashNode[] getChildren(FlashNode parentNode, boolean visual) {
		return getChildren(parentNode, visual, false);
	}

	public FlashNode[] getChildren(FlashNode parentNode, boolean visual,
			boolean debugMode) {
		List<FlashNode> children = new ArrayList<FlashNode>();
		if (null != bridge) {
			String sidMethod;
			if (visual) {
				sidMethod = sidGetInnerNodes;
			} else {
				sidMethod = debugMode ? sidGetDebugChildren : sidGetChildren;
			}
			Object result = invoke(sidMethod, parentNode.getTarget());
			if (result instanceof Object[]) {
				Object[] objChildren = (Object[]) result;
				for (int i = 0; i < objChildren.length; i++) {
					if (objChildren[i] instanceof ASObject) {
						children.add(new FlashNode(parentNode, this,
								(ASObject) objChildren[i]));
					}
				}
			}
		}
		return children.toArray(new FlashNode[children.size()]);
	}

	public void setMarker(Object objX, Object objY, Object objW, Object objH) {
		if (null != objX && null != objY && null != objW && null != objH) {
			if (null == objMarker) {

				Object objMarker = idispFlash.invoke1("GetAttribute", "marker");
				if (null == objMarker) {
					objMarker = invoke(sidNewMarker);
					if (!(objMarker instanceof Integer)) {
						return;
					}
					idispFlash.invoke("SetAttribute", new Object[] { "marker",
							objMarker });
				}
			}
			if (null != bridge && null != objMarker) {
				bridge.invoke(new Object[] { sidSetMarker, objMarker, objX,
						objY, objW, objH });
			}
		}
	}

	public Object callMethod(String target, String method, Object arg1) {
		return bridge
				.invoke(new Object[] { sidCallMethod, target, method, arg1 });
	}

	private Object invoke(String method) {
		return bridge.invoke(new Object[] { method });
	}

	private Object invoke(String method, Object arg1) {
		return bridge.invoke(new Object[] { method, arg1 });
	}

	public String getErrorText() {

		Object objError = null;
		try {
			objError = idispFlash.invoke1("GetAttribute", "aDesignerError");
		} catch (Exception e) {
		}
		if (objError != null) {
			String strError = (String) objError;
			if (strError.startsWith(FlashAdjust.ERROR_WAIT)) {
				return Messages.getString("flash.player_loading"); //$NON-NLS-1$
			}
			if (strError.startsWith(FlashAdjust.ERROR_NG)) {
				return Messages.getString("flash.player_no_dom"); //$NON-NLS-1$
			}
			if (strError.startsWith(FlashAdjust.ERROR_NA)) {
				return Messages.getString("flash.player_no_xcode"); //$NON-NLS-1$
			}
		}
		// return Messages.getString("flash.player_unknown"); //$NON-NLS-1$
		return Messages.getString("flash.player_no_xcode"); //$NON-NLS-1$
	}

	public void dispose() {
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	public String getWMode() {
		try {
			Object objWMode = idispFlash.get("WMode");
			return (String) objWMode;
		} catch (Exception e) {
			return null;
		}
	}

	public void setVariable(String name, String value) {
		try {
			idispFlash.invoke("SetVariable", new Object[] { name, value });
		} catch (Exception e) {
		}
	}

	public String getVariable(String name) {
		try {
			Object obj = idispFlash.invoke1("GetVariable", name);
			return (String) obj;
		} catch (Exception e) {
			return null;
		}
	}

	public String getProperty(String propertyName) {
		try {
			Object obj = idispFlash.get(propertyName);
			return (String) obj;
		} catch (Exception e) {
			return null;
		}
	}

	public int getWindow() {
		FlashMSAAObject fob = FlashMSAAObjectFactory.getFlashMSAAObjectFromElement(idispFlash);
		return fob.getWindow();
	}

	public IDispatch getDispatch() {
		return idispFlash;
	}

}

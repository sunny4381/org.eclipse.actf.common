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
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.internal.ASBridge;
import org.eclipse.actf.model.flash.internal.Messages;
import org.eclipse.actf.model.flash.util.ASObject;
import org.eclipse.actf.util.win32.HTMLElementUtil;
import org.eclipse.actf.util.win32.IAccessibleObject;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class FlashPlayer {

	private IDispatch idispFlash;
	private IDispatch idispMarker;

	private final String WMODE = "wmode";
	private final String GET_VARIABLE = "GetVariable";
	private final String SET_VARIABLE = "SetVariable";
	private final String GET_ATTRIBUTE = "getAttribute";
	private final String SET_ATTRIBUTE = "setAttribute";

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

	public FlashPlayer(IDispatch idispFlash) {
		this.idispFlash = idispFlash;
		idispFlash.cacheDispIDs(new String[] { WMODE, GET_VARIABLE,
				SET_VARIABLE, GET_ATTRIBUTE, SET_ATTRIBUTE });
		bridge = ASBridge.getInstance(this);
	}

	public static FlashPlayer getPlayerFromObject(IAccessibleObject accObject) {
		IDispatch idispFlash = HTMLElementUtil
				.getHtmlElementFromObject(accObject);
		if (idispFlash != null) {
			return new FlashPlayer(idispFlash);
		}
		return null;
	}
	
	public IDispatch getIDispatch() {
		return idispFlash;
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
			if (idispMarker == null) {
				idispMarker = (IDispatch) idispFlash.invoke1(GET_ATTRIBUTE,
						"marker");
				if (idispMarker != null) {
					Object objMarker = invoke(sidNewMarker);
					if (!(objMarker instanceof Integer)) {
						return;
					}
					int idispMarker = (Integer) objMarker;
					idispFlash.invoke(SET_ATTRIBUTE, new Object[] { "marker",
							idispMarker });
				}
			}

			if (null != bridge && null != idispMarker) {
				bridge.invoke(new Object[] { sidSetMarker, idispMarker, objX,
						objY, objW, objH });
			}
		}
	}

	public Object callMethod(String target, String method, Object arg) {
		Object ret = bridge.invoke(new Object[]{sidCallMethod, target, method, arg});
		if (ret == null)
			return null;
		return ret;
	}

	private Object invoke(String method) {
		return bridge.invoke(new Object[] { method });
	}

	private Object invoke(String method, Object arg1) {
		return bridge.invoke(new Object[] { method, arg1 });
	}

	public String getErrorText() {
		Object objError = idispFlash.invoke1(GET_ATTRIBUTE, "aDesignerError");
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
		return Messages.getString("flash.player_no_xcode"); //$NON-NLS-1$
	}

	public void dispose() {
		if (idispFlash != null) {
			idispFlash.release();
			idispFlash = null;
		}
		if (null != idispMarker) {
			idispMarker.release();
			idispMarker = null;
		}
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	public String getWMode() {
		if (idispFlash != null) {
			Object ret = idispFlash.get(WMODE);
			if (ret != null) {
				return (String) ret;
			}
		}
		return null;
	}
	
	public void setVariable(String name, String value) {
		if (idispFlash != null) {
			idispFlash.invoke(SET_VARIABLE, new Object[] { name, value });
		}
	}

	public String getVariable(String name) {
		if (idispFlash != null) {
			Object ret = idispFlash.invoke1(GET_VARIABLE, name);
			if (ret != null) {
				return (String) ret;
			}
		}
		return null;
	}
}

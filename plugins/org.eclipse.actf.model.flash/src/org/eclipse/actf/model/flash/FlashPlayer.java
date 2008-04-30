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

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.util.HTMLElementUtil;
import org.eclipse.actf.model.flash.internal.ASBridge;
import org.eclipse.actf.model.flash.internal.Messages;
import org.eclipse.actf.model.flash.util.ASObject;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

public class FlashPlayer {

	private OleAutomation automation;
	private Variant varMarker;
	private int idGetAttribute;
	private int idSetAttribute;
	private int idWMode;
	private int idGetVariable;
	private int idSetVariable;
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

	public FlashPlayer(Variant flash) {
		automation = flash.getAutomation();
		idWMode = getIDsOfNames("wmode"); //$NON-NLS-1$
		idGetVariable = getIDsOfNames("GetVariable"); //$NON-NLS-1$
		idSetVariable = getIDsOfNames("SetVariable"); //$NON-NLS-1$
		idGetAttribute = getIDsOfNames("getAttribute"); //$NON-NLS-1$
		idSetAttribute = getIDsOfNames("setAttribute"); //$NON-NLS-1$
		bridge = ASBridge.getInstance(this);
	}

	public static FlashPlayer getPlayerFromObject(AccessibleObject accObject) {
		Variant varFlash = HTMLElementUtil.getHtmlElementFromObject(accObject);
		if (null != varFlash) {
			return new FlashPlayer(varFlash);
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
			if (null == varMarker) {
				varMarker = automation.invoke(idGetAttribute,
						new Variant[] { new Variant("marker") }); //$NON-NLS-1$
				if (null == varMarker || OLE.VT_I4 != varMarker.getType()) {
					Object objMarker = invoke(sidNewMarker);
					if (!(objMarker instanceof Integer)) {
						return;
					}
					varMarker = new Variant(((Integer) objMarker).intValue());
					automation.invoke(idSetAttribute, new Variant[] {
							new Variant("marker"), varMarker }); //$NON-NLS-1$
				}
			}
			if (null != bridge && null != varMarker) {
				bridge
						.invoke(new Object[] { sidSetMarker,
								new Integer(varMarker.getInt()), objX, objY,
								objW, objH });
			}
		}
	}

	public Variant callMethod(String target, String method, Variant arg1) {
		if (null != bridge) {
			Object result = null;
			switch (arg1.getType()) {
			case OLE.VT_I4:
				result = callMethod(target, method, new Integer(arg1.getInt()));
				break;
			case OLE.VT_BSTR:
				result = callMethod(target, method, arg1.getString());
				break;
			}
			if (result instanceof Integer) {
				return new Variant(((Integer) result).intValue());
			} else if (result instanceof Double) {
				return new Variant(((Double) result).doubleValue());
			} else if (result instanceof String) {
				return new Variant(result.toString());
			}
		}
		return null;
	}

	private Object callMethod(String target, String method, Object arg1) {
		return bridge
				.invoke(new Object[] { sidCallMethod, target, method, arg1 });
	}

	private Object invoke(String method) {
		return bridge.invoke(new Object[] { method });
	}

	private Object invoke(String method, Object arg1) {
		return bridge.invoke(new Object[] { method, arg1 });
	}

	private int getIDsOfNames(String name) {
		try {
			int dispid[] = automation.getIDsOfNames(new String[] { name });
			if (null != dispid) {
				return dispid[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getErrorText() {
		Variant varError = automation.invoke(idGetAttribute,
				new Variant[] { new Variant("aDesignerError") }); //$NON-NLS-1$
		if (null != varError && OLE.VT_BSTR == varError.getType()) {
			String strError = varError.getString();
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

	public OleAutomation getAutomation() {
		return automation;
	}

	public void dispose() {
		if (null != varMarker) {
			varMarker.dispose();
			varMarker = null;
		}
		if (null != automation) {
			automation.dispose();
			automation = null;
		}
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	public String getWMode() {
		if (0 != idWMode) {
			Variant varWMode = automation.getProperty(idWMode);
			if (null != varWMode && OLE.VT_BSTR == varWMode.getType()) {
				return varWMode.getString();
			}
		}
		return null;
	}

	public void setVariable(String name, String value) {
		if (0 != idSetVariable) {
			automation.invoke(idSetVariable, new Variant[] { new Variant(name),
					new Variant(value) });
		}
	}

	public String getVariable(String name) {
		if (0 != idGetVariable) {
			Variant varValue = automation.invoke(idGetVariable,
					new Variant[] { new Variant(name) });
			if (null != varValue) {
				return varValue.getString();
			}
		}
		return null;
	}
}

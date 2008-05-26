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
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.as.ASDeserializer;
import org.eclipse.actf.model.flash.as.ASObject;
import org.eclipse.actf.model.flash.as.ASSerializer;
import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.internal.Messages;
import org.eclipse.actf.model.flash.util.FlashMSAAUtil;
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;
import org.eclipse.swt.widgets.Display;

public class FlashPlayer implements IFlashConst {

	private IDispatch idispFlash;
	private Object objMarker;

	public boolean isVisible = true;
	private FlashMSAAObject accessible;

	private String requestArgsPath;
	private String responseValuePath;
	private String contentIdPath;
	private String secret;
	private int swfVersion = -1;

	private boolean _isReady = false;
	private boolean _isRepaired = false;

	private FlashPlayer(IDispatch idisp) {
		idispFlash = idisp;
		accessible = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromElement(idispFlash);

		String rootPath = "";
		if ("true".equals(getVariable(PATH_ROOTLEVEL + PATH_IS_AVAILABLE))) { //$NON-NLS-1$ //$NON-NLS-2$
			rootPath = PATH_ROOTLEVEL;
		} else if ("true".equals(getVariable(PATH_BRIDGELEVEL + PATH_IS_AVAILABLE))) { //$NON-NLS-1$ //$NON-NLS-2$
			rootPath = PATH_BRIDGELEVEL;
		}

		this.requestArgsPath = rootPath + PROP_REQUEST_ARGS;
		this.responseValuePath = rootPath + PROP_RESPONSE_VALUE;
		this.contentIdPath = rootPath + PATH_CONTENT_ID;
	}

	public static FlashPlayer getPlayerFromPtr(int ptr) {
		IUnknown accObject = ComService.newIUnknown(ResourceManager
				.newResourceManager(null), ptr, true);
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

	public static FlashPlayer getPlayerFromWindow(int hwnd) {
		FlashMSAAObject accObject = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromWindow(hwnd);
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

	public static FlashPlayer getPlayerFromIDsipatch(IDispatch idisp) {
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

	public FlashMSAAObject getAccessible() {
		return accessible;
	}

	private boolean isReady() {
		if (_isReady)
			return true;
		try {
			Object r = idispFlash.get(READY_STATE);
			if (COMPLETED_READY_STATE.equals(r)) {
				_isReady = true;
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public FlashNode getRootNode() {
		Object result = invoke(M_GET_ROOT_NODE);
		if (result instanceof ASObject) {
			return new FlashNode(null, this, (ASObject) result);
		}
		return null;
	}

	public FlashNode getNodeFromPath(String path) {
		Object result = invoke(M_GET_NODE_FROM_PATH, path);
		if (result instanceof ASObject) {
			return new FlashNode(null, this, (ASObject) result);
		}
		return null;
	}

	public FlashNode getNodeAtDepthWithPath(String path, int depth) {
		Object result = invoke(new Object[] { M_GET_NODE_AT_DEPTH, path,
				Integer.valueOf(depth) });
		if (result instanceof ASObject) {
			return new FlashNode(null, this, (ASObject) result);
		}
		return null;
	}

	private FlashNode[] createFlashNodeArray(Object object, FlashNode parentNode) {
		List<FlashNode> children = new ArrayList<FlashNode>();
		if (object instanceof Object[]) {
			Object[] objChildren = (Object[]) object;
			for (int i = 0; i < objChildren.length; i++) {
				if (objChildren[i] instanceof ASObject) {
					children.add(new FlashNode(parentNode, this,
							(ASObject) objChildren[i]));
				}
			}
		}
		return children.toArray(new FlashNode[children.size()]);

	}

	public FlashNode[] translateWithPath(String path) {
		Object result = null;
		try {
			result = invoke(M_TRANSLATE, path);
		} catch (DispatchException e) {
		}
		return createFlashNodeArray(result, null);
	}

	public boolean hasChild(FlashNode parentNode, boolean visual) {
		return hasChild(parentNode, visual, false);
	}

	public boolean hasChild(FlashNode parentNode, boolean visual,
			boolean debugMode) {
		if (visual) {
			return true;
		}
		String sidMethod;
		if (visual) {
			sidMethod = M_GET_NUM_SUCCESSOR_NODES;
		} else {
			sidMethod = debugMode ? M_GET_NUM_SUCCESSOR_NODES
					: M_GET_NUM_CHILD_NODES;
		}
		Object result = invoke(sidMethod, parentNode.getTarget());
		if (result instanceof Integer) {
			return ((Integer) result).intValue() > 0;
		}
		return false;
	}

	public FlashNode[] getChildren(FlashNode parentNode, boolean visual) {
		return getChildren(parentNode, visual, false);
	}

	public FlashNode[] getChildren(FlashNode parentNode, boolean visual,
			boolean debugMode) {
		String sidMethod;
		if (visual) {
			sidMethod = M_GET_INNER_NODES;
		} else {
			sidMethod = debugMode ? M_GET_SUCCESSOR_NODES : M_GET_CHILD_NODES;
		}
		return createFlashNodeArray(invoke(sidMethod, parentNode.getTarget()),
				parentNode);
	}

	public FlashNode[] searchVideo() {
		return createFlashNodeArray(invoke(new Object[] { M_SEARCH_VIDEO,
				PATH_ROOTLEVEL, PATH_GLOBAL }), null);
	}

	public FlashNode[] searchSound() {
		return createFlashNodeArray(invoke(new Object[] { M_SEARCH_SOUND,
				PATH_ROOTLEVEL, PATH_GLOBAL }), null);
	}

	private void initMarker() {
		if (objMarker != null)
			return;
		Object result = invoke(M_NEW_MARKER);
		if (result instanceof Integer) {
			objMarker = (Integer) result;
			return;
		}
		objMarker = null;
	}

	public boolean setMarker(Object objX, Object objY, Object objW, Object objH) {
		if (null != objX && null != objY && null != objW && null != objH) {
			initMarker();
			if (null != objMarker) {
				invoke(new Object[] { M_SET_MARKER, objMarker, objX, objY,
						objW, objH });
				return true;
			}
		}
		return false;
	}

	public boolean unsetMarker() {
		if (objMarker == null)
			return false;
		invoke(M_UNSET_MARKER, objMarker);
		return true;
	}

	public boolean setFocus(String target) {
		Object result = invoke(M_SET_FOCUS, target);
		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		}
		return false;
	}

	public Object getProperty(String path, String prop) {
		return invoke(new Object[] { M_GET_PROPERTY, path, prop });
	}

	public void setProperty(String path, String prop, Object value) {
		invoke(new Object[] { M_SET_PROPERTY, path, prop, value });
	}

	public boolean updateTarget() {
		Object result = invoke(new Object[] { M_UPDATE_TARGET, PATH_ROOTLEVEL,
				10 });
		return ((result instanceof Boolean) && (((Boolean) result)
				.booleanValue()));
	}

	public void repairFlash() {
		if (!_isRepaired) {
			_isRepaired = true;
			invoke(M_REPAIR_FLASH, PATH_ROOTLEVEL);
		}
	}

	public Object callMethod(String target, String method) {
		return invoke(new Object[] { M_CALL_METHOD, target, method });
	}

	public Object callMethod(String target, String method, Object[] args) {
		if (null == args) {
			args = new Object[0];
		}
		Object[] a = new Object[args.length + 3];
		a[0] = M_CALL_METHOD;
		a[1] = target;
		a[2] = method;
		System.arraycopy(args, 0, a, 3, args.length);
		return invoke(a);
	}

	private Object invoke(String method) {
		return invoke(new Object[] { method });
	}

	private Object invoke(String method, Object arg1) {
		return invoke(new Object[] { method, arg1 });
	}

	private Object invoke(Object[] args) {
		int counter = 0;
		try {
			if (secret == null) {
				this.secret = getSecret(contentIdPath);
				if (secret == null) {
					return null;
				}
			}
			setVariable(responseValuePath, ""); //$NON-NLS-1$
			String argsStr = ASSerializer.serialize(secret, args);
			setVariable(requestArgsPath, argsStr);
			long endTime = System.currentTimeMillis() + 100;
			while (endTime > System.currentTimeMillis()) {
				counter++;
				String value = getVariable(responseValuePath);
				if (null == value)
					return null;
				if (value.length() > 0) {
					ASDeserializer asd = new ASDeserializer(value);
					return asd.deserialize();
				}
				Display.getCurrent().readAndDispatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (counter != 1) {
			// System.out.println("FlashPlayer: " + counter);
			// for (int i = 0; i < args.length; i++) {
			// System.out.println(" args[" + i + "]=" + args[i]); //$NON-NLS-1$
			// //$NON-NLS-2$
			// }
			// }
		}
		return null;
	}

	public String getErrorText() {

		Object objError = null;
		try {
			objError = idispFlash.invoke1(PLAYER_GET_ATTRIBUTE, ATTR_ERROR);
		} catch (Exception e) {
		}
		if (objError != null) {
			String strError = (String) objError;
			if (strError.startsWith(ERROR_WAIT)) {
				return Messages.getString("flash.player_loading"); //$NON-NLS-1$
			}
			if (strError.startsWith(ERROR_NG)) {
				return Messages.getString("flash.player_no_dom"); //$NON-NLS-1$
			}
			if (strError.startsWith(ERROR_NA)) {
				return Messages.getString("flash.player_no_xcode"); //$NON-NLS-1$
			}
		}
		// return Messages.getString("flash.player_unknown"); //$NON-NLS-1$
		return Messages.getString("flash.player_no_xcode"); //$NON-NLS-1$
	}

	public String getWMode() {
		try {
			Object objWMode = idispFlash.get(WMODE);
			return (String) objWMode;
		} catch (Exception e) {
			return null;
		}
	}

	public void setVariable(String name, String value) {
		if (!isReady()) {
			return;
		}
		try {
			idispFlash
					.invoke(PLAYER_SET_VARIABLE, new Object[] { name, value });
		} catch (Exception e) {
		}
	}

	public String getVariable(String name) {
		if (!isReady()) {
			return "";
		}
		try {
			Object obj = idispFlash.invoke1(PLAYER_GET_VARIABLE, name);
			return (String) obj;
		} catch (Exception e) {
			return null;
		}
	}

	public String getPlayerProperty(String propertyName) {
		try {
			Object obj = idispFlash.get(propertyName);
			return (String) obj;
		} catch (Exception e) {
			return null;
		}
	}

	public int getWindow() {
		FlashMSAAObject fob = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromElement(idispFlash);
		return fob.getWindow();
	}

	public IDispatch getDispatch() {
		return idispFlash;
	}

	private String getSecret(String contentId) {
		try {
			String id = getVariable(contentId);
			if (null == id || id.length() == 0)
				return null;
			IWaXcoding waxcoding = FlashModelPlugin.getDefault()
					.getIWaXcoding();
			return waxcoding.getSecret(id, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

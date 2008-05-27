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
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.swt.widgets.Display;

public class FlashPlayer implements IFlashPlayer {

	private IDispatch idispFlash;
	private Object objMarker;

	private FlashMSAAObject accessible;

	private String requestArgsPath;
	private String responseValuePath;
	private String contentIdPath;
	private String secret;
	private int swfVersion = -1;

	private final boolean _isVisible;
	private boolean _isReady = false;
	private boolean _isRepaired = false;

	FlashPlayer(IDispatch idisp) {
		idispFlash = idisp;
		try {
			accessible = FlashMSAAObjectFactory
					.getFlashMSAAObjectFromElement(idispFlash);
		} catch (Exception e) {
			// without UI (FlashDetect)
			_isReady = true;
		}

		String wmode = getWMode();
		if (null != wmode
				&& (V_OPAQUE.equalsIgnoreCase(wmode) || V_TRANSPARENT
						.equalsIgnoreCase(wmode))) {
			_isVisible = false;
		} else {
			_isVisible = true;
		}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getAccessible()
	 */
	public FlashMSAAObject getAccessible() {
		return accessible;
	}

	public boolean isReady() {
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

	public boolean isVisible() {
		return _isVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getRootNode()
	 */
	public ASNode getRootNode() {
		Object result = invoke(M_GET_ROOT_NODE);
		if (result instanceof ASObject) {
			return new ASNode(null, this, (ASObject) result);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getNodeFromPath(java.lang.String)
	 */
	public ASNode getNodeFromPath(String path) {
		Object result = invoke(M_GET_NODE_FROM_PATH, path);
		if (result instanceof ASObject) {
			return new ASNode(null, this, (ASObject) result);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getNodeAtDepthWithPath(java.lang.String,
	 *      int)
	 */
	public ASNode getNodeAtDepthWithPath(String path, int depth) {
		Object result = invoke(new Object[] { M_GET_NODE_AT_DEPTH, path,
				Integer.valueOf(depth) });
		if (result instanceof ASObject) {
			return new ASNode(null, this, (ASObject) result);
		}
		return null;
	}

	private ASNode[] createFlashNodeArray(Object object, ASNode parentNode) {
		List<ASNode> children = new ArrayList<ASNode>();
		if (object instanceof Object[]) {
			Object[] objChildren = (Object[]) object;
			for (int i = 0; i < objChildren.length; i++) {
				if (objChildren[i] instanceof ASObject) {
					children.add(new ASNode(parentNode, this,
							(ASObject) objChildren[i]));
				}
			}
		}
		return children.toArray(new ASNode[children.size()]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#translateWithPath(java.lang.String)
	 */
	public ASNode[] translateWithPath(String path) {
		Object result = null;
		try {
			result = invoke(M_TRANSLATE, path);
		} catch (DispatchException e) {
		}
		return createFlashNodeArray(result, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#hasChild(org.eclipse.actf.model.flash.ASNode,
	 *      boolean)
	 */
	public boolean hasChild(ASNode parentNode, boolean visual) {
		return hasChild(parentNode, visual, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#hasChild(org.eclipse.actf.model.flash.ASNode,
	 *      boolean, boolean)
	 */
	public boolean hasChild(ASNode parentNode, boolean visual, boolean debugMode) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getChildren(org.eclipse.actf.model.flash.ASNode,
	 *      boolean)
	 */
	public ASNode[] getChildren(ASNode parentNode, boolean visual) {
		return getChildren(parentNode, visual, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getChildren(org.eclipse.actf.model.flash.ASNode,
	 *      boolean, boolean)
	 */
	public ASNode[] getChildren(ASNode parentNode, boolean visual,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#searchVideo()
	 */
	public ASNode[] searchVideo() {
		return createFlashNodeArray(invoke(new Object[] { M_SEARCH_VIDEO,
				PATH_ROOTLEVEL, PATH_GLOBAL }), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#searchSound()
	 */
	public ASNode[] searchSound() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#setMarker(java.lang.Number,
	 *      java.lang.Number, java.lang.Number, java.lang.Number)
	 */
	public boolean setMarker(Number x, Number y, Number width, Number height) {
		unsetMarker();
		if (null != x && null != y && null != width && null != height) {
			initMarker();
			if (null != objMarker) {
				invoke(new Object[] { M_SET_MARKER, objMarker, x, y, width,
						height });
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#unsetMarker()
	 */
	public boolean unsetMarker() {
		if (objMarker == null)
			return false;
		invoke(M_UNSET_MARKER, objMarker);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#setFocus(java.lang.String)
	 */
	public boolean setFocus(String target) {
		Object result = invoke(M_SET_FOCUS, target);
		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public Object getProperty(String path, String prop) {
		return invoke(new Object[] { M_GET_PROPERTY, path, prop });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#setProperty(java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setProperty(String path, String prop, Object value) {
		invoke(new Object[] { M_SET_PROPERTY, path, prop, value });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#updateTarget()
	 */
	public boolean updateTarget() {
		Object result = invoke(new Object[] { M_UPDATE_TARGET, PATH_ROOTLEVEL,
				10 });
		return ((result instanceof Boolean) && (((Boolean) result)
				.booleanValue()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#repairFlash()
	 */
	public void repairFlash() {
		if (!_isRepaired) {
			_isRepaired = true;
			invoke(M_REPAIR_FLASH, PATH_ROOTLEVEL);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#callMethod(java.lang.String,
	 *      java.lang.String)
	 */
	public Object callMethod(String target, String method) {
		return invoke(new Object[] { M_CALL_METHOD, target, method });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#callMethod(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getErrorText()
	 */
	public String getErrorText() {

		String strError = getPlayerProperty(ATTR_ERROR);
		if (strError != null) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getWMode()
	 */
	public String getWMode() {
		try {
			Object objWMode = idispFlash.get(ATTR_WMODE);
			return (String) objWMode;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#setVariable(java.lang.String,
	 *      java.lang.String)
	 */
	void setVariable(String name, String value) {
		if (!isReady()) {
			return;
		}
		try {
			idispFlash
					.invoke(PLAYER_SET_VARIABLE, new Object[] { name, value });
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getVariable(java.lang.String)
	 */
	String getVariable(String name) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getPlayerProperty(java.lang.String)
	 */
	public void setPlayerProperty(String propertyName, String value) {
		try {
			idispFlash.invoke(PLAYER_SET_ATTRIBUTE, new Object[] {
					propertyName, value });
		} catch (Exception e) {
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getPlayerProperty(java.lang.String)
	 */
	public String getPlayerProperty(String propertyName) {
		try {
			Object obj = idispFlash.get(propertyName);
			return (String) obj;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getWindow()
	 */
	public int getWindow() {
		FlashMSAAObject fob = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromElement(idispFlash);
		return fob.getWindow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getDispatch()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getPlayerVersion()
	 */
	public String getPlayerVersion() {
		String version = getVariable(PLAYER_VERSION);
		if (null == version) {
			version = getNodeFromPath(PLAYER_VERSION).getValue();
		}
		return version;
	}

}

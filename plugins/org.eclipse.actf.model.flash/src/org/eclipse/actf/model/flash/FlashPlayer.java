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

import org.eclipse.actf.model.flash.internal.ASBridgeImplV8;
import org.eclipse.actf.model.flash.internal.FlashStatusUtil;
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class FlashPlayer implements IFlashPlayer {

	private IDispatch idispFlash;

	private FlashMSAAObject accessible;

	private IASBridge asBrigde = null;

	private final boolean _isVisible;
	private boolean _isReady = false;

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

	}

	private boolean initAsBridge() {
		if (null != asBrigde) {
			return true;
		}
		asBrigde = new ASBridgeImplV8(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getAccessible()
	 */
	public FlashMSAAObject getAccessible() {
		return accessible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#isReady()
	 */
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
	 * @see org.eclipse.actf.model.flash.IASBridge#getRootNode()
	 */
	public IASNode getRootNode() {
		if (initAsBridge()) {
			return asBrigde.getRootNode();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getNodeFromPath(java.lang.String)
	 */
	public IASNode getNodeFromPath(String path) {
		if (initAsBridge()) {
			return asBrigde.getNodeFromPath(path);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getNodeAtDepthWithPath(java.lang.String,
	 *      int)
	 */
	public IASNode getNodeAtDepthWithPath(String path, int depth) {
		if (initAsBridge()) {
			return asBrigde.getNodeAtDepthWithPath(path, depth);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#translateWithPath(java.lang.String)
	 */
	public IASNode[] translateWithPath(String path) {
		if (initAsBridge()) {
			return asBrigde.translateWithPath(path);
		}
		return new IASNode[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#hasChild(org.eclipse.actf.model.flash.ASNode,
	 *      boolean)
	 */
	public boolean hasChild(IASNode parentNode, boolean visual) {
		return hasChild(parentNode, visual, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#hasChild(org.eclipse.actf.model.flash.ASNode,
	 *      boolean, boolean)
	 */
	public boolean hasChild(IASNode parentNode, boolean visual,
			boolean debugMode) {
		if (initAsBridge()) {
			return asBrigde.hasChild(parentNode, visual, debugMode);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getChildren(org.eclipse.actf.model.flash.ASNode,
	 *      boolean)
	 */
	public IASNode[] getChildren(IASNode parentNode, boolean visual) {
		return getChildren(parentNode, visual, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getChildren(org.eclipse.actf.model.flash.ASNode,
	 *      boolean, boolean)
	 */
	public IASNode[] getChildren(IASNode parentNode, boolean visual,
			boolean debugMode) {
		if (initAsBridge()) {
			return asBrigde.getChildren(parentNode, visual, debugMode);
		}
		return new IASNode[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#searchVideo()
	 */
	public IASNode[] searchVideo() {
		if (initAsBridge()) {
			return asBrigde.searchVideo();
		}
		return new IASNode[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#searchSound()
	 */
	public IASNode[] searchSound() {
		if (initAsBridge()) {
			return asBrigde.searchSound();
		}
		return new IASNode[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#setMarker(java.lang.Number,
	 *      java.lang.Number, java.lang.Number, java.lang.Number)
	 */
	public boolean setMarker(Number x, Number y, Number width, Number height) {
		if (initAsBridge()) {
			return asBrigde.setMarker(x, y, width, height);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#unsetMarker()
	 */
	public boolean unsetMarker() {
		if (initAsBridge()) {
			return asBrigde.unsetMarker();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#setFocus(java.lang.String)
	 */
	public boolean setFocus(String target) {
		if (initAsBridge()) {
			return asBrigde.setFocus(target);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public Object getProperty(String path, String prop) {
		if (initAsBridge()) {
			return asBrigde.getProperty(path, prop);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#setProperty(java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setProperty(String path, String prop, Object value) {
		if (initAsBridge()) {
			asBrigde.setProperty(path, prop, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#updateTarget()
	 */
	public boolean updateTarget() {
		if (initAsBridge()) {
			return asBrigde.updateTarget();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#repairFlash()
	 */
	public void repairFlash() {
		if (initAsBridge()) {
			asBrigde.repairFlash();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#callMethod(org.eclipse.actf.model.flash.IASNode,
	 *      java.lang.String)
	 */
	public Object callMethod(IASNode targetNode, String method) {
		if (initAsBridge()) {
			return asBrigde.callMethod(targetNode, method);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#callMethod(org.eclipse.actf.model.flash.IASNode,
	 *      java.lang.String, java.lang.Object[])
	 */
	public Object callMethod(IASNode targetNode, String method, Object[] args) {
		if (initAsBridge()) {
			return asBrigde.callMethod(targetNode, method, args);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getStatus()
	 */
	public String getStatus() {
		return FlashStatusUtil.getStatus(this);
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

	String getVariable(String name) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IFlashPlayer#getPlayerVersion()
	 */
	public String getPlayerVersion() {
		String version = getVariable(PLAYER_VERSION);
		if (null == version) {
			IASNode versionNode = getNodeFromPath(PLAYER_VERSION);
			if (null != versionNode) {
				version = versionNode.getValue();
			}
		}
		return version;
	}

}

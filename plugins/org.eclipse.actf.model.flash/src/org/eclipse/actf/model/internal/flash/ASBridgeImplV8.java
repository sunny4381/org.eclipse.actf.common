/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.IASBridge;
import org.eclipse.actf.model.flash.IASNode;
import org.eclipse.actf.model.flash.IFlashPlayer;
import org.eclipse.actf.model.flash.as.ASDeserializer;
import org.eclipse.actf.model.flash.as.ASObject;
import org.eclipse.actf.model.flash.as.ASSerializer;
import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.bridge.WaXcodingFactory;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.swt.widgets.Display;

public class ASBridgeImplV8 implements IASBridge {

	private IFlashPlayer flashPlayer;
	private IDispatch idispFlash;

	private Object objMarker;

	private String requestArgsPath;
	private String responseValuePath;
	private String contentIdPath;
	private String secret = null;

	private boolean _isRepaired = false;

	public ASBridgeImplV8(IFlashPlayer flashPlayer) {
		this.flashPlayer = flashPlayer;
		idispFlash = flashPlayer.getDispatch();

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

	private String getVariable(String name) {
		try {
			Object obj = idispFlash.invoke1(PLAYER_GET_VARIABLE, name);
			return (String) obj;
		} catch (Exception e) {
			return null;
		}
	}

	private void setVariable(String name, String value) {
		try {
			idispFlash
					.invoke(PLAYER_SET_VARIABLE, new Object[] { name, value });
		} catch (Exception e) {
		}
	}

	private boolean initSecret() {
		if (null != secret) {
			return true;
		}
		try {
			String id = getVariable(contentIdPath);
			if (null == id || id.length() == 0)
				return false;
			IWaXcoding waxcoding = WaXcodingFactory.getWaXcoding();
			secret = waxcoding.getSecret(id, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (null != secret);
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
			if (!initSecret()) {
				return null;
			}
			setVariable(responseValuePath, ""); //$NON-NLS-1$
			String argsStr = ASSerializer.serialize(secret, args);
			setVariable(requestArgsPath, argsStr);
			long endTime = System.currentTimeMillis() + 100;
			while (endTime > System.currentTimeMillis()) {
				counter++;
				String value = getVariable(responseValuePath);
				if (null != value && value.length() > 0) {
					ASDeserializer asd = new ASDeserializer(value);
					return asd.deserialize();
				}
				Display.getCurrent().readAndDispatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (counter != 1) {
				System.out.println("FlashPlayer: " + counter);
				for (int i = 0; i < args.length; i++) {
					System.out.println(" args[" + i + "]=" + args[i]); //$NON-NLS-1$
					//$NON-NLS-2$
				}
			}
		}
		return null;
	}

	private ASNodeImplV8[] createFlashNodeArray(Object object,
			IASNode parentNode) {
		List<ASNodeImplV8> children = new ArrayList<ASNodeImplV8>();
		if (object instanceof Object[]) {
			Object[] objChildren = (Object[]) object;
			for (int i = 0; i < objChildren.length; i++) {
				if (objChildren[i] instanceof ASObject) {
					children.add(new ASNodeImplV8(parentNode, flashPlayer,
							(ASObject) objChildren[i]));
				}
			}
		}
		return children.toArray(new ASNodeImplV8[children.size()]);

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
	 * @see org.eclipse.actf.model.flash.IASBridge#callMethod(org.eclipse.actf.model.flash.IASNode,
	 *      java.lang.String)
	 */
	public Object callMethod(IASNode targetNode, String method) {
		return invoke(new Object[] { M_CALL_METHOD, targetNode.getTarget(),
				method });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#callMethod(org.eclipse.actf.model.flash.IASNode,
	 *      java.lang.String, java.lang.Object[])
	 */
	public Object callMethod(IASNode targetNode, String method, Object[] args) {
		if (null == args) {
			args = new Object[0];
		}
		Object[] a = new Object[args.length + 3];
		a[0] = M_CALL_METHOD;
		a[1] = targetNode.getTarget();
		a[2] = method;
		System.arraycopy(args, 0, a, 3, args.length);
		return invoke(a);
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
	 * @see org.eclipse.actf.model.flash.IASBridge#getNodeAtDepthWithPath(java.lang.String,
	 *      int)
	 */
	public IASNode getNodeAtDepthWithPath(String path, int depth) {
		Object result = invoke(new Object[] { M_GET_NODE_AT_DEPTH, path,
				Integer.valueOf(depth) });
		if (result instanceof ASObject) {
			return new ASNodeImplV8(null, flashPlayer, (ASObject) result);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getNodeFromPath(java.lang.String)
	 */
	public IASNode getNodeFromPath(String path) {
		Object result = invoke(M_GET_NODE_FROM_PATH, path);
		if (result instanceof ASObject) {
			return new ASNodeImplV8(null, flashPlayer, (ASObject) result);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public Object getProperty(String path, String prop) {
		return invoke(new Object[] { M_GET_PROPERTY, path, prop });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#getRootNode()
	 */
	public IASNode getRootNode() {
		Object result = invoke(M_GET_ROOT_NODE);
		if (result instanceof ASObject) {
			return new ASNodeImplV8(null, flashPlayer, (ASObject) result);
		}
		return null;
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
	 * @see org.eclipse.actf.model.flash.IASBridge#repairFlash()
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
	 * @see org.eclipse.actf.model.flash.IASBridge#searchSound()
	 */
	public IASNode[] searchSound() {
		return createFlashNodeArray(invoke(new Object[] { M_SEARCH_SOUND,
				PATH_ROOTLEVEL, PATH_GLOBAL }), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#searchVideo()
	 */
	public IASNode[] searchVideo() {
		return createFlashNodeArray(invoke(new Object[] { M_SEARCH_VIDEO,
				PATH_ROOTLEVEL, PATH_GLOBAL }), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#setFocus(java.lang.String)
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
	 * @see org.eclipse.actf.model.flash.IASBridge#setMarker(java.lang.Number,
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
	 * @see org.eclipse.actf.model.flash.IASBridge#setMarker(org.eclipse.actf.model.flash.IASNode)
	 */
	public boolean setMarker(IASNode node) {
		return setMarker(node.getX(), node.getY(), node.getWidth(), node
				.getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#setProperty(java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setProperty(String path, String prop, Object value) {
		invoke(new Object[] { M_SET_PROPERTY, path, prop, value });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#translateWithPath(java.lang.String)
	 */
	public IASNode[] translateWithPath(String path) {
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
	 * @see org.eclipse.actf.model.flash.IASBridge#unsetMarker()
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
	 * @see org.eclipse.actf.model.flash.IASBridge#updateTarget()
	 */
	public boolean updateTarget() {
		Object result = invoke(new Object[] { M_UPDATE_TARGET, PATH_ROOTLEVEL,
				10 });
		return ((result instanceof Boolean) && (((Boolean) result)
				.booleanValue()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.flash.IASBridge#clearAllMarkers()
	 */
	public boolean clearAllMarkers() {
		invoke(M_CLEAR_ALL_MARKERS);
		return true;
	}

}

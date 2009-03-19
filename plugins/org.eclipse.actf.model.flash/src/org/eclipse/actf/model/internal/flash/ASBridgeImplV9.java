/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.IASBridge;
import org.eclipse.actf.model.flash.IASNode;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.flash.IFlashPlayer;
import org.eclipse.actf.model.flash.as.ASDeserializer;
import org.eclipse.actf.model.flash.as.ASObject;
import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.bridge.WaXcodingFactory;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class ASBridgeImplV9 implements IASBridge {

	private IFlashPlayer flashPlayer;
	private IDispatch idispFlash;

	private Object objMarker;

	private String secret = null;

	private boolean _isRepaired = false;

	public ASBridgeImplV9(IFlashPlayer flashPlayer) {
		this.flashPlayer = flashPlayer;
		idispFlash = this.flashPlayer.getDispatch();
	}

	public boolean initSecret() {
		if (null != secret) {
			return true;
		}
		try {
			// obtains content ID
			String id = (String) idispFlash.invoke(DISPATCH_METHOD,
					new String[] { "", M_GET_CONTENT_ID }); //$NON-NLS-1$
			if (id.length() == 0) {
				// TODO
			}

			// obtains secret from the proxy
			IWaXcoding waxcoding = WaXcodingFactory.getWaXcoding();
			secret = waxcoding.getSecret(id, false);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return (null != secret);
	}

	public Object dispatchMethod(String method) {
		return dispatchMethod(method, null);
	}

	// TODO private, in the future
	public Object dispatchMethod(String method, Object[] args) {
		if (initSecret()) {
			try {
				// invokes method call
				Object[] invokeArgs = new Object[((args == null) ? 0
						: args.length) + 2];
				invokeArgs[0] = secret;
				invokeArgs[1] = method;
				if (args != null)
					for (int i = 0; i < args.length; i++) {
						invokeArgs[i + 2] = args[i];
					}
				String ret = (String) (idispFlash.invoke(DISPATCH_METHOD,
						invokeArgs));
				// System.out.println("[d] ret = " + ret);
				Object obj = new ASDeserializer(ret).deserialize();
				// System.out.println(obj.toString());
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object callMethod(IASNode targetNode, String method) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object callMethod(IASNode targetNode, String method, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	public IASNode[] getChildren(IASNode parentNode, boolean visual) {
		return getChildren(parentNode, visual, true);
	}

	public IASNode[] getChildren(IASNode parentNode, boolean visual,
			boolean debugMode) {
		// System.out.println("[bridge] AS3: calling Flash's getChildNodes...");
		Object children = dispatchMethod(IFlashConst.M_GET_CHILD_NODES,
				new Object[] { parentNode.getId() });
		// System.out.println("[bridge] done.");

		List<IASNode> ret = new ArrayList<IASNode>();
		if (children instanceof Object[]) {
			Object[] objChildren = (Object[]) children;
			for (int i = 0; i < objChildren.length; i++) {
				if (objChildren[i] instanceof ASObject) {
					ret.add(new ASNodeImplV9(parentNode, flashPlayer,
							(ASObject) objChildren[i]));
				}
			}
			return ret.toArray(new IASNode[ret.size()]);
		} else
			return new IASNode[0];

	}

	public IASNode getNodeAtDepthWithPath(String path, int depth) {
		// TODO Auto-generated method stub
		return null;
	}

	public IASNode getNodeFromPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getProperty(String path, String prop) {
		// TODO Auto-generated method stub
		return null;
	}

	public IASNode getRootNode() {
		// System.out.println("[bridge] getting AS3 root...");
		// System.out.println("[bridge] calling Flash's getRootNode...");
		Object flashNode = dispatchMethod(IFlashConst.M_GET_ROOT_NODE,
				new Object[] {});
		// System.out.println("[bridge] done.");
		if (flashNode == null)
			return null;
		if (flashNode instanceof ASObject) {
			IASNode asNode = new ASNodeImplV9(null, flashPlayer,
					(ASObject) flashNode);
			return asNode;
		} else
			return null;
	}

	public boolean hasChild(IASNode parentNode, boolean visual) {
		return hasChild(parentNode, visual, true);
	}

	public boolean hasChild(IASNode parentNode, boolean visual,
			boolean debugMode) {
		// System.out.println("[bridge] AS3: calling Flash's getNumChildNodes...");
		Object numChildren = dispatchMethod(IFlashConst.M_GET_NUM_CHILD_NODES,
				new Object[] { parentNode.getId() });
		// System.out.println("[bridge] done.");
		if (numChildren instanceof Integer) {
			return ((Integer) numChildren).intValue() > 0;
		} else
			return false;
	}

	public void repairFlash() {
		if (!_isRepaired) {
			_isRepaired = true;
			// TODO impl repair
		}
	}

	public IASNode[] searchSound() {
		// TODO Auto-generated method stub
		return null;
	}

	public IASNode[] searchVideo() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setFocus(String target) {
		// TODO Auto-generated method stub
		return false;
	}

	private void initMarker() {
		if (objMarker != null)
			return;
		Object result = dispatchMethod(M_NEW_MARKER);
		if (result instanceof Integer) {
			objMarker = result;
			return;
		}
		objMarker = null;
	}

	public boolean setMarker(Number x, Number y, Number width, Number height) {
		unsetMarker();
		if (null != x && null != y && null != width && null != height) {
			initMarker();
			if (null != objMarker) {
				dispatchMethod(M_SET_MARKER, new Object[] { objMarker, x, y,
						width, height });
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.actf.model.flash.IASBridge#setMarker(org.eclipse.actf.model
	 * .flash.IASNode)
	 */
	public boolean setMarker(IASNode node) {
		return setMarker(node.getX(), node.getY(), node.getWidth(), node
				.getHeight());
	}

	public void setProperty(String path, String prop, Object value) {
		// TODO Auto-generated method stub

	}

	public IASNode[] translateWithPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean unsetMarker() {
		if (objMarker == null)
			return false;
		dispatchMethod(M_UNSET_MARKER, new Object[] { objMarker });
		return true;
	}

	public boolean updateTarget() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASBridge#clearAllMarkers()
	 */
	public boolean clearAllMarkers() {
		dispatchMethod(M_CLEAR_ALL_MARKERS);
		return true;
	}

}

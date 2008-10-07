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
package org.eclipse.actf.model.flash;


/**
 *
 */
public interface IASBridge extends IFlashConst {

	/**
	 * @param targetNode
	 * @param method
	 * @return
	 */
	public abstract Object callMethod(IASNode targetNode, String method);

	/**
	 * @param targetNode
	 * @param method
	 * @param args
	 * @return
	 */
	public abstract Object callMethod(IASNode targetNode, String method,
			Object[] args);

	/**
	 * @param parentNode
	 * @param visual
	 * @return
	 */
	public abstract IASNode[] getChildren(IASNode parentNode, boolean visual);

	/**
	 * @param parentNode
	 * @param visual
	 * @param debugMode
	 * @return
	 */
	public abstract IASNode[] getChildren(IASNode parentNode, boolean visual,
			boolean debugMode);

	/**
	 * @param path
	 * @param depth
	 * @return
	 */
	public abstract IASNode getNodeAtDepthWithPath(String path, int depth);

	/**
	 * @param path
	 * @return
	 */
	public abstract IASNode getNodeFromPath(String path);

	/**
	 * @param path
	 * @param prop
	 * @return
	 */
	public abstract Object getProperty(String path, String prop);

	/**
	 * @return
	 */
	public abstract IASNode getRootNode();

	/**
	 * @param parentNode
	 * @param visual
	 * @return
	 */
	public abstract boolean hasChild(IASNode parentNode, boolean visual);

	/**
	 * @param parentNode
	 * @param visual
	 * @param debugMode
	 * @return
	 */
	public abstract boolean hasChild(IASNode parentNode, boolean visual,
			boolean debugMode);

	/**
	 * 
	 */
	public abstract void repairFlash();

	/**
	 * @return
	 */
	public abstract IASNode[] searchSound();

	/**
	 * @return
	 */
	public abstract IASNode[] searchVideo();

	/**
	 * @param target
	 * @return
	 */
	public abstract boolean setFocus(String target);

	/**
	 * @param node
	 * @return
	 */
	public abstract boolean setMarker(IASNode node);

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public abstract boolean setMarker(Number x, Number y, Number width,
			Number height);

	/**
	 * @return
	 */
	public abstract boolean clearAllMarkers();

	/**
	 * @param path
	 * @param prop
	 * @param value
	 */
	public abstract void setProperty(String path, String prop, Object value);

	/**
	 * @param path
	 * @return
	 */
	public abstract IASNode[] translateWithPath(String path);

	/**
	 * @return
	 */
	public abstract boolean unsetMarker();

	/**
	 * @return
	 */
	public abstract boolean updateTarget();

}
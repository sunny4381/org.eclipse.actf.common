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
 * Interface to implement bridge that enables access to ActionScript from Java
 */
public interface IASBridge extends IFlashConst {

	/**
	 * Call ActionScript method of target {@link IASNode}
	 * 
	 * @param targetNode
	 *            target node
	 * @param method
	 *            target method
	 * @return resulting Object
	 */
	public abstract Object callMethod(IASNode targetNode, String method);

	/**
	 * Call ActionScript method of target {@link IASNode}
	 * 
	 * @param targetNode
	 *            target node
	 * @param method
	 *            target method
	 * @param args
	 *            arguments
	 * @return resulting Object
	 */
	public abstract Object callMethod(IASNode targetNode, String method,
			Object[] args);

	/**
	 * Get children of specified parent {@link IASNode}
	 * 
	 * @param parentNode
	 *            target parent node
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 * @return children of the node as {@link IASNode} array
	 */
	public abstract IASNode[] getChildren(IASNode parentNode, boolean visual);

	/**
	 * Get children of specified parent {@link IASNode}
	 * 
	 * @param parentNode
	 *            target parent node
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 * @param debugMode
	 *            <ul>
	 *            <li>true: list all entities including the internal variables.
	 *            Apparent parent-child relationships are used as specified,
	 *            even if there are circular references.</li>
	 *            <li>false: list children without the internal variables.
	 *            Circular references are removed.</li>
	 *            </ul>
	 * @return children of the node as {@link IASNode} array
	 */
	public abstract IASNode[] getChildren(IASNode parentNode, boolean visual,
			boolean debugMode);

	/**
	 * Get {@link IASNode} at specified depth and path
	 * 
	 * @param path
	 *            target path
	 * @param depth
	 *            target depth
	 * @return {@link IASNode} or null if not available
	 */
	public abstract IASNode getNodeAtDepthWithPath(String path, int depth);

	/**
	 * Get {@link IASNode} at specified path
	 * 
	 * @param path
	 *            target path
	 * @return {@link IASNode} or null if not available
	 */
	public abstract IASNode getNodeFromPath(String path);

	/**
	 * Get Property value at specified path
	 * 
	 * @param path
	 *            target path
	 * @param prop
	 *            target property name
	 * @return resulting property value
	 */
	public abstract Object getProperty(String path, String prop);

	/**
	 * @return root {@link IASNode}
	 */
	public abstract IASNode getRootNode();

	/**
	 * @param parentNode
	 *            target parent {@link IASNode}
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 * 
	 * @return true if the parent node has child
	 */
	public abstract boolean hasChild(IASNode parentNode, boolean visual);

	/**
	 * @param parentNode
	 *            target parent {@link IASNode}
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 * @param debugMode
	 *            <ul>
	 *            <li>true: check all entities including the internal
	 *            variables. Apparent parent-child relationships are used as
	 *            specified, even if there are circular references.</li>
	 *            <li>false: check without the internal variables. Circular
	 *            references are removed.</li>
	 *            </ul>
	 * 
	 * @return true if the parent node has child
	 */
	public abstract boolean hasChild(IASNode parentNode, boolean visual,
			boolean debugMode);

	/**
	 * Repair flash content
	 */
	public abstract void repairFlash();

	/**
	 * @return sound objects in the content as {@link IASNode} array
	 */
	public abstract IASNode[] searchSound();

	/**
	 * @return video objects in the content as {@link IASNode} array
	 */
	public abstract IASNode[] searchVideo();

	/**
	 * Focus specified target
	 * 
	 * @param target
	 *            full instance name of the node
	 * @return true if succeeded
	 * 
	 * @see IASNode#getTarget()
	 * @see IFlashConst#M_SET_FOCUS
	 */
	public abstract boolean setFocus(String target);

	/**
	 * Set marker on the specified {@link IASNode} position
	 * 
	 * @param node
	 *            target {@link IASNode}
	 * @return true if succeeded
	 */
	public abstract boolean setMarker(IASNode node);

	/**
	 * Set marker at specified position and size
	 * 
	 * @param x
	 *            x coordinates
	 * @param y
	 *            y coordinates
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @return true if succeeded
	 */
	public abstract boolean setMarker(Number x, Number y, Number width,
			Number height);

	/**
	 * Clear all markers
	 * 
	 * @return true if succeeded
	 */
	public abstract boolean clearAllMarkers();

	/**
	 * Set Property value at specified path
	 * 
	 * @param path
	 *            target path
	 * @param prop
	 *            target property name
	 * @param value
	 *            target property value
	 */
	public abstract void setProperty(String path, String prop, Object value);

	/**
	 * @param path
	 *            target path
	 * @return true if succeeded
	 */
	public abstract IASNode[] translateWithPath(String path);

	/**
	 * Unset current marker
	 * 
	 * @return true if succeeded
	 */
	public abstract boolean unsetMarker();

	/**
	 * @return true if succeeded
	 */
	public abstract boolean updateTarget();

}
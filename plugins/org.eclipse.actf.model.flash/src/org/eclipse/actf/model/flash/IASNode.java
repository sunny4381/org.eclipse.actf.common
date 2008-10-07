/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import java.util.Set;

import org.eclipse.actf.model.flash.as.ASObject;

/**
 * Interface to implement wrapper class for ActionScript Node class. This Node
 * class wraps all type of ActionScript entities (primitive values, special
 * visual objects, and other object instances) and its derived information.
 */
public interface IASNode {

	/**
	 * @return icon type of the node
	 */
	public abstract String getIconType();

	/**
	 * @return type of the node
	 * @see IFlashConst#ASNODE_TYPE
	 */
	public abstract String getType();

	/**
	 * @return class name of the node
	 * @see IFlashConst#ASNODE_CLASS_NAME
	 */
	public abstract String getClassName();

	/**
	 * @return short instance name of the node
	 * @see IFlashConst#ASNODE_OBJECT_NAME
	 */
	public abstract String getObjectName();

	/**
	 * @return full instance name of the node
	 * @see IFlashConst#ASNODE_TARGET
	 */
	public abstract String getTarget();

	/**
	 * @return whether the node is UI Component
	 * @see IFlashConst#ASNODE_IS_UI_COMPONENT
	 */
	public abstract boolean isUIComponent();

	/**
	 * @return string representation of the node's value
	 * @see IFlashConst#ASNODE_VALUE
	 */
	public abstract String getValue();

	/**
	 * @return text content of the node. This method is equals to
	 *         <code>getText(true);</code>
	 * @see IFlashConst#ASNODE_TEXT
	 */
	public abstract String getText();

	/**
	 * @param useAccName
	 * @return text content of the node. If <code>useAccName</code> is true,
	 *         try to get text content information from {@link ASAccInfo}
	 * @see ASAccInfo
	 */
	public abstract String getText(boolean useAccName);

	/**
	 * @return title of the node
	 */
	public abstract String getTitle();

	/**
	 * Gets the value for a given key from corresponding {@link ASObject}
	 * 
	 * @param name
	 *            name of a key
	 * @return value for given key
	 * @see ASObject#get(String)
	 */
	public abstract Object getObject(String name);

	/**
	 * @return parent {@link IASNode} of the node
	 */
	public abstract IASNode getParent();

	/**
	 * @return level (depth) of the node from root {@link IASNode} (root=0)
	 */
	public abstract int getLevel();

	/**
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 *            
	 * @return true if the node has child
	 */
	public abstract boolean hasChild(boolean visual);

	/**
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
	 * @return true if the node has child
	 */
	public abstract boolean hasChild(boolean visual, boolean debugMode);

	/**
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 * @param informative
	 *            <ul>
	 *            <li>true: filter out invisible (i.e. the type of number,
	 *            string, function or boolean) properties and methods.</li>
	 *            <li>false: all result (without filter)</li>
	 *            </ul>
	 * @return children of the node as {@link IASNode} array
	 */
	public abstract IASNode[] getChildren(boolean visual, boolean informative);

	/**
	 * @param visual
	 *            <ul>
	 *            <li>true: use visual structure (use AS
	 *            <code>getInnerNodes</code> method)</li>
	 *            <li>false: use object structure</li>
	 *            </ul>
	 * @param informative
	 *            <ul>
	 *            <li>true: filter out invisible (i.e. the type of number,
	 *            string, function or boolean) properties and methods.</li>
	 *            <li>false: all result (without filter)</li>
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
	public abstract IASNode[] getChildren(boolean visual, boolean informative,
			boolean debugMode);

	/**
	 * @return highlight the node
	 */
	public abstract boolean setMarker();

	/**
	 * @return {@link IFlashPlayer} that contains the node
	 */
	public abstract IFlashPlayer getPlayer();

	/**
	 * @return {@link ASAccInfo} of the node
	 */
	public abstract ASAccInfo getAccInfo();

	/**
	 * Gets the {@link Set} of keys of corresponding {@link ASObject}
	 * 
	 * @return Set of keys
	 * @see ASObject#getKeys()
	 */
	public abstract Set<String> getKeys();

	/**
	 * @return whether the node is Button component and its onRelease handler is
	 *         defined.
	 */
	public abstract boolean hasOnRelease();

	/**
	 * @return x coordinate of the node
	 * @see IFlashConst#ASNODE_X
	 */
	public abstract double getX();

	/**
	 * @return y coordinate of the node
	 * @see IFlashConst#ASNODE_Y
	 */
	public abstract double getY();

	/**
	 * @return width of the node
	 * @see IFlashConst#ASNODE_WIDTH
	 */
	public abstract double getWidth();

	/**
	 * @return height of the node
	 * @see IFlashConst#ASNODE_HEIGHT
	 */
	public abstract double getHeight();

	/**
	 * @return ID of the node
	 * @see IFlashConst#ASNODE_ID
	 */
	public abstract int getId();

	/**
	 * @return depth (aka z-oder) of the node
	 * @see IFlashConst#ASNODE_DEPTH
	 */
	public abstract int getDepth();

	/**
	 * @return current frame of the node
	 * @see IFlashConst#ASNODE_CURRENT_FRAME
	 */
	public abstract int getCurrentFrame();

	/**
	 * @return tab index of the node
	 * @see IFlashConst#ASNODE_TAB_INDEX
	 */
	public abstract int getTabIndex();

	/**
	 * @return whether the node is input-able by users.
	 * @see IFlashConst#ASNODE_IS_INPUTABLE
	 */
	public abstract boolean isInputable();

	/**
	 * @return whether the system should traverse into its children.
	 * @see IFlashConst#ASNODE_IS_OPAQUE_OBJECT
	 */
	public abstract boolean isOpaqueObject();

}
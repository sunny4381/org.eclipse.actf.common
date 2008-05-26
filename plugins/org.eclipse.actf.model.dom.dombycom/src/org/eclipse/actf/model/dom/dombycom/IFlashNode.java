/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.dombycom;

/**
 * IFlashNode interface defines the methods to be implemented by the Flash node
 * objects. It extends the {@link INodeEx} interface for Flash object.
 */
public interface IFlashNode extends INodeEx {
	short FLASH_NODE = 120;

	/**
	 * @return the target path of the Flash object from the root. It might be
	 *         like a "_level0.hoge.hoge".
	 */
	String getTarget();

	/**
	 * @param path the target path to be obtained.
	 * @return the Flash object specified by the <i>path</i>
	 */
	IFlashNode getNodeFromPath(String path);

	/**
	 * @param depth the target depth to be obtained.
	 * @return the Flash object specified by the <i>depth</i>
	 */
	IFlashNode getNodeAtDepth(int depth);

	/**
	 * @return the original child nodes of the Flash node.
	 */
	IFlashNode[] getInnerNodes();

	/**
	 * @return the depth of the node.
	 */
	int getDepth();

	/**
	 * @return the frame number of the current state.
	 */
	int getCurrentFrame();

	/**
	 * This method is used by the auto translate function.
	 * @return the translated nodes.
	 */
	IFlashNode[] translate();

	/**
	 * @return the MSAA representation of the Flash element.
	 */
	IFlashMSAANode getMSAA();

	/**
	 * do the Flash repairing function. (experimental)
	 */
	void repairFlash();

	/**
	 * @return the root Flash object node.
	 */
	INodeEx getBaseNode();
}
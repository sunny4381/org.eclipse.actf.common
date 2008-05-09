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
 * The IFlashMSAANode interface defines the methods to be implemented by the MSAA
 * object.
 */
public interface IFlashMSAANode extends INodeEx {
	/**
	 * @return the unique ID of the node. A node should return an id.
	 */
	String getID();

	/**
	 * @param id the id to be used for the search
	 * @return the MSAA node specified by the <i>id<i>.
	 */
	IFlashMSAANode searchByID(String id);

	/**
	 * @return the base node. It might be an object element of the Flash object.
	 */
	INodeEx getBaseNode();
}

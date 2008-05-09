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
package org.eclipse.actf.model.dom.dombycom;

import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

/**
 * IObjectElementFactory interface defines the methods to be implemented by the
 * extension of the DOM object.
 */
public interface IObjectElementFactory {

	/**
	 * @param base an instance of INodeEx which becomes the base of the new Node.
	 * @param inode the instance of the IDispatch which is the target to create. 
	 * @return the new node of wrapper of the <i>inode</i>. 
	 */
	public NodeImpl createTopNode(NodeImpl base, IDispatch inode);

}
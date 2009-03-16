/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.object;

import org.eclipse.actf.model.dom.dombycom.IObjectElementFactory;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

@SuppressWarnings("nls")
public class ObjectElementFactory {

	private static IObjectElementFactory[] factories = ObjectElementFactoryExtension
			.getObjectElementFactories();

	public static NodeImpl create(NodeImpl base, IDispatch inode) {
		for (IObjectElementFactory factory : factories) {
			NodeImpl newNode = factory.createTopNode(base, inode);
			if (newNode != null)
				return newNode;
		}

		Object controls = Helper.get(inode, "controls");
		if (controls instanceof IDispatch) {
			return new WMPNodeImpl(base, inode, (IDispatch) controls);
		}

		Object clientID = Helper.get(inode, "ClientID");
		if (clientID != null) {
			return new WMP64NodeImpl(base, inode);
		}

		try {
			Object qtVersion = inode.invoke0("GetQuickTimeVersion");
			if (qtVersion != null) {
				return new QTNodeImpl(base, inode);
			}
		} catch (DispatchException e) {
		}

		try {
			Object rpVersion = inode.invoke0("GetVersionInfo");
			if (rpVersion != null) {
				return new RPNodeImpl(base, inode);
			}
		} catch (DispatchException e) {
		}

		return null;
	}

}

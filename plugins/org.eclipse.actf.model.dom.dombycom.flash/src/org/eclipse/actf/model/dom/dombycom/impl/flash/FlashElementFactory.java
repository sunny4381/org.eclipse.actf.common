/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import org.eclipse.actf.model.dom.dombycom.IObjectElementFactory;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.comclutch.win32.IDispatch;

public class FlashElementFactory implements IObjectElementFactory {

	public FlashElementFactory() {
	}
	
	public NodeImpl createTopNode(NodeImpl base, IDispatch inode) {
		return FlashTopNodeImpl.newFlashNode(base, inode);
	}
}

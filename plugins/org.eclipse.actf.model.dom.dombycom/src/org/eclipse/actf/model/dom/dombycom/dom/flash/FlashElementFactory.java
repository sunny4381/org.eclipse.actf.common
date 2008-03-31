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

package org.eclipse.actf.model.dom.dombycom.dom.flash;

import org.eclipse.actf.model.dom.dombycom.dom.NodeImpl;
import org.eclipse.actf.util.comclutch.win32.IDispatch;




public class FlashElementFactory {
    public static NodeImpl createTopNode(NodeImpl base, IDispatch inode) {
        return FlashTopNodeImpl.newFlashNode(base, inode);
    }
}

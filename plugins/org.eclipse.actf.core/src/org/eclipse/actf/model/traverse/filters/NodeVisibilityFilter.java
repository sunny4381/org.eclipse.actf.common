/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Ann Ford - initial API and implementation
*******************************************************************************/ 
package org.eclipse.actf.model.traverse.filters;

import org.eclipse.actf.model.IGuiModel;
import org.eclipse.actf.model.IModel;

/**
 * This filter will exclude invisible items.  
 * If you wish to show all items, visible or invisible,
 * remove it, or do not set it in the first place.
 * @author annford
 *
 */
public class NodeVisibilityFilter implements INodeFilter
{
	private IModel model = null;
	
	public NodeVisibilityFilter (IModel model) {
		this.model = model;
	}

	public boolean pass (Object node) {
		boolean visible = false;
		if (model != null && model instanceof IGuiModel) {
			visible = ((IGuiModel)model).isVisible(node);
		}
		return visible;
	}
}

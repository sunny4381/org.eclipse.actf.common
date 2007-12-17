/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  <a href="mailto:fordann@us.ibm.com">Ann Ford</a> - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.traverse.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.actf.model.IModel;


public class NodeNameFilter implements INodeFilter {

	protected IModel model = null;
	protected List ignoreNames = new ArrayList();

	public NodeNameFilter(IModel model, String names) {
		this.model = model;
		if (names != null) {
			this.ignoreNames = Arrays.asList(names.split(",\\s*"));
		}
	}

	public boolean pass(Object node) {
		boolean pass = true;
		if (node != null && model != null) {
			String name = model.getNodeName(node);
			pass = !ignoreNames.contains(name);
		}
		return pass;
	}
	
}

/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  <a href="mailto:masquill@us.ibm.com">Mike Squillace</a> - initial API and implementation
*******************************************************************************/ 
package com.ibm.haac.raven.core.traverse;

import java.util.LinkedList;

import com.ibm.haac.raven.core.traverse.filters.INodeFilter;

/**
 * base implementation for any node walker. Clients should subclass this class rather than 
 * attempting to implement all of <code>INodeWalker</code>.
 * 
 * @author <a href="mailto:masquill@us.ibm.com">Mike Squillace</a>
 *
 */
public abstract class AbstractNodeWalker implements INodeWalker
{

	protected LinkedList nodeFilters = new LinkedList();
	
	/** {@inheritDoc} */
	public void addNodeFilter(INodeFilter filter) {
		if (!nodeFilters.contains(filter)) {
			nodeFilters.add(filter);
		}
	}

	/** {@inheritDoc} */
	public INodeFilter[] removeAllFilters() {
		INodeFilter[] filters = (INodeFilter[]) nodeFilters.toArray(new INodeFilter[nodeFilters.size()]);
		nodeFilters.clear();
		return filters;
	}

	public void removeNodeFilter(INodeFilter filter) {
		nodeFilters.remove(filter);
	}

}

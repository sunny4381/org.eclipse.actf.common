/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package com.ibm.haac.raven.core.traverse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.model.InvalidComponentException;
import com.ibm.haac.raven.core.resources.ClassLoaderCache;
import com.ibm.haac.raven.core.runtime.IRuntimeContext;
import com.ibm.haac.raven.core.runtime.RuntimeContextFactory;
import com.ibm.haac.raven.core.traverse.filters.INodeFilter;

/**
 * base implementation for any tree walker
 * 
 * @author Mike Squillace
 */
public abstract class AbstractTreeNodeWalker extends AbstractNodeWalker
	implements ITreeNodeWalker
{

	protected ClassLoaderCache clCache = ClassLoaderCache.getDefault();
	protected IConfiguration configuration;
	protected Map componentBridgeMap;

	public AbstractTreeNodeWalker () {
		try {
			IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
			configuration = context.getConfiguration();
		}catch (ConfigurationException e) {
		}
	}

	/** {@inheritDoc} */
	public void setComponentBridgeMap (Map bridgeMap) {
		componentBridgeMap = bridgeMap;
	}

	protected Object getBridgedChild (Object parent) {
		return componentBridgeMap != null ? componentBridgeMap.get(parent) : null;
	}

	protected Object getBridgedParent (Object child) {
		Object parent = null;
		if (componentBridgeMap != null) {
			for (Iterator iter = componentBridgeMap.keySet().iterator(); iter.hasNext() & parent == null;) {
				Object p = iter.next();
				if (componentBridgeMap.get(p).equals(child)) {
					parent = p;
				}
			}
		}
		return parent;
	}

	/** {@inheritDoc} */
	public boolean hasChildren (Object element)
		throws InvalidComponentException {
		return getChildren(element).length > 0
				|| getBridgedChild(element) != null;
	}

	/** {@inheritDoc} */
	public Object[] getSuccessorNodes (Object node)
		throws InvalidComponentException {
		return getChildren(node);
	}
	
	/** {@inheritDoc} */
	public Object[] getFilteredSuccessorNodes (Object node)
		throws InvalidComponentException {
		return getFilteredChildren(node);
	}
	
	/** {@inheritDoc} */
	public Object[] getFilteredChildren (Object parent) throws InvalidComponentException {
		Object[] children = getChildren(parent);
		LinkedList filtered = new LinkedList();

		if(!nodeFilters.isEmpty()){
			if (children != null && children.length > 0) {
				// admit each child only if it passes all filters
				for (int c = 0; c < children.length; ++c) {
					Object child = children[c];
					boolean pass = true;
					for (Iterator iter = nodeFilters.iterator(); pass && iter.hasNext(); ) {
						pass = ((INodeFilter) iter.next()).pass(child);
					}
					if (pass) {
						filtered.add(child);
					}
				}
			}
		}
		
		return !nodeFilters.isEmpty() ? filtered.toArray() : children;
	}
	
	/** {@inheritDoc} */
	public Object[] getPredecessorNodes (Object node)
		throws InvalidComponentException {
		Object parent = getParent(node);
		return parent != null ? new Object[] {parent} : new Object[0];
	}
	
} // AbstractTreeWalker

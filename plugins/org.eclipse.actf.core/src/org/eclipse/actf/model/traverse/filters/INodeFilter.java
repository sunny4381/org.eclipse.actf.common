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
package org.eclipse.actf.model.traverse.filters;

import org.eclipse.actf.model.traverse.INodeWalker;

/**
 * used as a general-purpose filter for nodes during retreaval of successor nodes in a graph.
 *
 * @see INodeWalker#addNodeFilter(INodeFilter)
 * @see INodeWalker#getFilteredSuccessorNodes(Object)
 * @author <a href="mailto:masquill@us.ibm.com">Mike Squillace</a>
 *
 */
public interface INodeFilter
{
	/**
	 * whether or not to admit this object through the filter
	 * 
	 * @param node
	 * @return <code>true</code> if this object should be included in successor node collections, 
	 * <code>false</code> if it should be filtered
	 */
	public boolean pass (Object node);
	
}

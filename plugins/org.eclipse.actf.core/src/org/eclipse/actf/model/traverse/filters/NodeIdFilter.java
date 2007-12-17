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


public class NodeIdFilter implements INodeFilter {

	/*
	 * public boolean isIgnorable(Object comp) { if
	 * (ignorableClassNamePatterns.isEmpty()) { String ids =
	 * getModelProperty(Configuration.MODEL_IGNOREIDS); String types =
	 * getModelProperty(Configuration.MODEL_IGNORENODENAMES); StringTokenizer
	 * regExpList = types == null ? null : new StringTokenizer(types); while
	 * (regExpList != null && regExpList.hasMoreTokens()) {
	 * ignorableClassNamePatterns.add(Pattern.compile(regExpList.nextToken())); }
	 * regExpList = ids == null ? null : new StringTokenizer(ids); while
	 * (regExpList != null && regExpList.hasMoreTokens()) {
	 * ignorableIDPatterns.add(Pattern.compile(regExpList.nextToken())); } }
	 * boolean matched = false; if (comp != null) { Iterator iter =
	 * ignorableClassNamePatterns.iterator(); String name =
	 * comp.getClass().getName(); while (!matched & iter.hasNext()) { matched =
	 * ((Pattern) iter.next()).matcher(name).matches(); } iter =
	 * ignorableIDPatterns.iterator(); name = getNodeID(comp); while (!matched &
	 * iter.hasNext()) { matched = ((Pattern)
	 * iter.next()).matcher(name).matches(); } } return matched; }
	 */

	protected IModel model = null;
	protected List ignoreIds = new ArrayList();

	public NodeIdFilter(IModel model, String ids) {
		this.model = model;
		if (ids != null) {
			this.ignoreIds = Arrays.asList(ids.split(",\\s*"));
		}
	}

	public boolean pass(Object node) {
		boolean pass = true;
		if (node != null && model != null) {
			String id = model.getNodeId(node);
			pass = !ignoreIds.contains(id);
		}
		return pass;
	}
}

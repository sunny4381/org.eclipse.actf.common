/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.as;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ASObject {
	
	public static final String ASNODE_TYPE = "type"; //$NON-NLS-1$
	public static final String ASNODE_CLASS_NAME = "className"; //$NON-NLS-1$
	public static final String ASNODE_OBJECT_NAME = "objectName"; //$NON-NLS-1$
	public static final String ASNODE_TARGET = "target"; //$NON-NLS-1$
	public static final String ASNODE_VALUE = "value"; //$NON-NLS-1$
	public static final String ASNODE_TEXT = "text"; //$NON-NLS-1$
	public static final String ASNODE_TITLE = "title"; //$NON-NLS-1$
	public static final String ASNODE_IS_UI_COMPONENT = "isUIComponent"; //$NON-NLS-1$
	public static final String ASNODE_ACCINFO = "accInfo"; //$NON-NLS-1$

	public static final String ACCINFO_NAME = "name"; //$NON-NLS-1$
	public static final String ACCINFO_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ACCINFO_SILENT = "silent"; //$NON-NLS-1$
	public static final String ACCINFO_ROLE = "role"; //$NON-NLS-1$

	private final Map<String, Object> map;

	
	ASObject() {
		this.map = new HashMap<String, Object>();
	}

	Object put(String prop, Object obj) {
		return map.put(prop, obj);
	}

	public Object get(String prop) {
		return map.get(prop);
	}

	public Set<String> getKeys() {
		return map.keySet();
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String prop = it.next();
			ret.append(prop);
			ret.append(":");
			ret.append("" + map.get(prop));
			ret.append(",");
		}
		return ret.toString();
	}
}

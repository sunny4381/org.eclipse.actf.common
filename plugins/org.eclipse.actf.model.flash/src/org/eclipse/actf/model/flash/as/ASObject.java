/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
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

/**
 * A wrapper class for ActionScript <code>Object</code> class. ActionScript
 * <code>Object</code> instance is a map from <code>String</code> (called
 * key) to <code>Object</code> (called value), which is written as a literal
 * like <code>{id:123, mes:'Hello'}</code>. The value for a given key is
 * obtained by {@link #get} method. For the example above,
 * <code>get("id")==123</code> and <code>get("mes")=="Hello"</code> holds.
 * ASObject instance cannot be created by its constructor. The instance is
 * internally created by {@link ASDeserializer#deserialize} method.
 * 
 * @see ASDeserializer
 */
public class ASObject {

	/**
	 * A map from {@link String} to {@link Object} which stores ActionScript
	 * <code>Object</code> instance content.
	 */
	private final Map<String, Object> map;

	/**
	 * Constructor without any arguments. It's not public so developers cannot
	 * directly call it.
	 */
	ASObject() {
		this.map = new HashMap<String, Object>();
	}

	/**
	 * Sets the value for a given key. If the instance already has the value for
	 * the key, it is overwritten.
	 * 
	 * @param prop
	 *            Name of a key to put value
	 * @param obj
	 *            A value for the given key to set
	 * @return The value of <code>prop</code>.
	 */
	Object put(String prop, Object obj) {
		return map.put(prop, obj);
	}

	/**
	 * Gets the value for a given key.
	 * 
	 * @param prop
	 *            Name of a key to get value.
	 * @return The value for the given key.
	 */
	public Object get(String prop) {
		return map.get(prop);
	}

	/**
	 * Gets the set of keys of the instance.
	 * 
	 * @return The {@link Set} of the keys.
	 */
	public Set<String> getKeys() {
		return map.keySet();
	}

	/**
	 * Gets the ActionScript style {@link String} representation of the
	 * instance.
	 * 
	 * @return The string for the instance in ActionScript literal style such as
	 *         <code>{id:123,mes:'Hello',}</code>. Note that a comma appears
	 *         after the last key-value pair.
	 */
	@SuppressWarnings("nls")
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

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.ui.internal;

import java.util.EventObject;
import java.util.Map;



public class FavoritesChangeEvent extends EventObject {

	private static final long serialVersionUID = 7558042163036173736L;

	private Map _favoritesMap;
	
	public FavoritesChangeEvent(Object obj, Map favoritesMap) {
		super(obj);
		setFavoritesMap(favoritesMap);
	}

	public void setFavoritesMap(Map favoritesMap) {
		this._favoritesMap = favoritesMap;
	}

	public Map getFavoritesMap() {
		return this._favoritesMap;
	}
	
}

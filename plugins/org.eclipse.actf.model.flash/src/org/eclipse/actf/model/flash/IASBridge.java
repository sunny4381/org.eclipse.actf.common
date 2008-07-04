/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;


public interface IASBridge extends IFlashConst {

	public abstract Object callMethod(IASNode targetNode, String method);

	public abstract Object callMethod(IASNode targetNode, String method,
			Object[] args);

	public abstract IASNode[] getChildren(IASNode parentNode, boolean visual);

	public abstract IASNode[] getChildren(IASNode parentNode, boolean visual,
			boolean debugMode);

	public abstract IASNode getNodeAtDepthWithPath(String path, int depth);

	public abstract IASNode getNodeFromPath(String path);

	public abstract Object getProperty(String path, String prop);

	public abstract IASNode getRootNode();

	public abstract boolean hasChild(IASNode parentNode, boolean visual);

	public abstract boolean hasChild(IASNode parentNode, boolean visual,
			boolean debugMode);

	public abstract void repairFlash();

	public abstract IASNode[] searchSound();

	public abstract IASNode[] searchVideo();

	public abstract boolean setFocus(String target);

	public abstract boolean setMarker(IASNode node);

	public abstract boolean setMarker(Number x, Number y, Number width,
			Number height);

	public abstract boolean clearAllMarkers();

	public abstract void setProperty(String path, String prop, Object value);

	public abstract IASNode[] translateWithPath(String path);

	public abstract boolean unsetMarker();

	public abstract boolean updateTarget();

}
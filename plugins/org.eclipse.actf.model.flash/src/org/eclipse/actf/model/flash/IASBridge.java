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

public interface IASBridge {

	public abstract Object callMethod(String target, String method);

	public abstract Object callMethod(String target, String method,
			Object[] args);

	public abstract ASNode[] getChildren(ASNode parentNode, boolean visual);

	public abstract ASNode[] getChildren(ASNode parentNode, boolean visual,
			boolean debugMode);

	public abstract ASNode getNodeAtDepthWithPath(String path, int depth);

	public abstract ASNode getNodeFromPath(String path);

	public abstract Object getProperty(String path, String prop);

	public abstract ASNode getRootNode();

	public abstract boolean hasChild(ASNode parentNode, boolean visual);

	public abstract boolean hasChild(ASNode parentNode, boolean visual,
			boolean debugMode);

	public abstract void repairFlash();

	public abstract ASNode[] searchSound();

	public abstract ASNode[] searchVideo();

	public abstract boolean setFocus(String target);

	public abstract boolean setMarker(Number x, Number y, Number width,
			Number height);

	public abstract void setProperty(String path, String prop, Object value);

	public abstract ASNode[] translateWithPath(String path);

	public abstract boolean unsetMarker();

	public abstract boolean updateTarget();

}
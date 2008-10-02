/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch;

/**
 * IResource interface defines the methods to be implemented by classes
 * treats native resources
 */
public interface IResource {
	/**
	 * @return the resource manager of the resource
	 */
	ResourceManager getResourceManager();

	/**
	 * call {@link ResourceManager#addResource(IResource)} with the target
	 */
	void addResource(IResource target);

	/**
	 * call {@link ResourceManager#findInResource(long)} with the ptr
	 */
	IResource findInResource(long ptr);

	/**
	 * @return the resource is permanent object or not
	 */
	boolean isPermanent();

	/**
	 * @return the native reference pointer of the resource
	 */
	long getPtr();

	/**
	 * call {@link ResourceManager#removeResource(IResource)} with this
	 */
	void release();
}

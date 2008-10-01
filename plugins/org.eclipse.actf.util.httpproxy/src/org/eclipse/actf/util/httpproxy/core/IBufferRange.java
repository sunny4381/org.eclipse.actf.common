/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

/**
 * Interface for keeping start position and length of target content
 */
public interface IBufferRange {

	/**
	 * Reset start position and length to zero.
	 */
	public abstract void reset();

	/**
	 * Set start position of the target content in the buffer.
	 * 
	 * @param start
	 *            start position
	 */
	public abstract void setStart(int start);

	/**
	 * Set length of the target content.
	 * 
	 * @param length
	 *            length
	 */
	public abstract void setLength(int length);

	/**
	 * Get start position of the target content in buffer.
	 * 
	 * @return start position
	 */
	public abstract int getStart();

	/**
	 * Get length of the target content.
	 * 
	 * @return length
	 */
	public abstract int getLength();

}
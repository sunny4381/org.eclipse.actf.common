/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash.util;

/**
 * A class representing SWF file information.
 * 
 */
public class SwfInfo {
	private int swfVersion;
	private int asVersion;
	private int frameSizeX;
	private int frameSizeY;

	public SwfInfo() {
		asVersion = -1;
	}

	public int getSwfVersion() {
		return swfVersion;
	}

	public int getAsVersion() {
		return asVersion;
	}

	public void setAsVersion(int asVersion) {
		this.asVersion = asVersion;
	}

	/**
	 * Returns frame width in twips.
	 * 
	 * @return frame width in twips
	 */
	public int getFrameSizeX() {
		return frameSizeX;
	}

	public void setFrameSizeX(int frameSizeX) {
		this.frameSizeX = frameSizeX;
	}

	/**
	 * Returns frame height in twips.
	 * 
	 * @return frame height in twips
	 */
	public int getFrameSizeY() {
		return frameSizeY;
	}

	public void setFrameSizeY(int frameSizeY) {
		this.frameSizeY = frameSizeY;
	}
}

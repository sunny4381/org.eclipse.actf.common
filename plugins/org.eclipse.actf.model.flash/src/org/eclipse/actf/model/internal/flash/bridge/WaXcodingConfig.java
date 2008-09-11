/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash.bridge;

import java.io.InputStream;

import org.eclipse.actf.model.internal.flash.proxy.HTTPLocalServerSWFFactory;
import org.eclipse.actf.model.internal.flash.proxy.ProxyTranscoderSWF;
import org.eclipse.actf.model.internal.flash.proxy.SWFBootloader;

public class WaXcodingConfig {

	private boolean swfTranscodingFlag = false;
	private int swfTranscodingMinimumVersion = 0;
	private boolean swfBootloaderFlag = true;

	public boolean getSWFTranscodingFlag() {
		return swfTranscodingFlag;
	}

	public void setSWFTranscodingFlag(boolean flag) {
		this.swfTranscodingFlag = flag;
	}

	public void setSWFBootloaderFlag(boolean swfBootloaderFlag) {
		this.swfBootloaderFlag = swfBootloaderFlag;
	}

	public boolean getSWFBootloaderFlag() {
		return swfBootloaderFlag;
	}

	public int getSWFTranscodingMinimumVersion() {
		return swfTranscodingMinimumVersion;
	}

	public void setSWFTranscodingMinimumVersion(int version) {
		this.swfTranscodingMinimumVersion = version;
	}

	public void setSWFTranscodingImposedFile(InputStream is) {
		ProxyTranscoderSWF.setSWFTranscodingImposedFile(is);
	}

	public void setSWFBootLoader(InputStream is) {
		SWFBootloader.setSWFBootLoaderFile(is);
	}

	public void setSWFBridgeInit(InputStream is) {
		HTTPLocalServerSWFFactory.setBridgeInitSwf(is);
	}

	
	private static WaXcodingConfig instance = new WaXcodingConfig();

	public static WaXcodingConfig getInstance() {
		return instance;
	}
}

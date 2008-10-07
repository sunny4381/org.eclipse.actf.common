/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.transcoder;

/**
 * Interface to implement Factory class for {@link ISwfTranscoder}
 */
public interface ISwfTranscoderFactory {
	/**
	 * Create new instance of {@link ISwfTranscoder}
	 * 
	 * @param id
	 *            instance ID
	 * @return new instance of {@link ISwfTranscoder}
	 */
	ISwfTranscoder newInstance(int id);
}

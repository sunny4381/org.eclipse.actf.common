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

import java.io.InputStream;

/**
 * Interface to provide SWF transcoding function
 */
public interface ISwfTranscoder {
	/**
	 * Parse target input stream
	 * 
	 * @param is
	 *            target input stream
	 * @return parsed Object
	 * @throws Exception
	 */
	Object parse(InputStream is) throws Exception;

	/**
	 * Impose target to source
	 * 
	 * @param impose
	 *            target Object to impose
	 * @param src
	 *            source Object
	 * @return imposed Object
	 */
	Object impose(Object impose, Object src);

	/**
	 * Generate content from Object
	 * 
	 * @param swf
	 *            target Object
	 * @param compress
	 *            if true, compress the content
	 * @param version
	 *            target version
	 * @return generated content
	 * @throws Exception
	 */
	byte[] generate(Object swf, boolean compress, int version) throws Exception;
}

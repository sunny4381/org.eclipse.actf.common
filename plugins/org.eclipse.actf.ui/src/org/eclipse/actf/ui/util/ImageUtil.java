/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ui.util;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class ImageUtil {

	public static synchronized boolean saveImageToFile(Image image,
			String savePath, int format) {
		ImageLoader loader = new ImageLoader();
		if (image != null && image.getImageData() != null) {
			loader.data = new ImageData[] { image.getImageData() };
			// TODO how to handle SWT.error?
			loader.save(savePath, format);
		}
		return true;
	}
}

/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tatsuya ISHIHARA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.odf.base;

import org.w3c.dom.Document;

public interface ODFDocument extends Document {
	public String getURL();

	public void setURL(String sUrl);

	public void setODFVersion(double version);

	public double getODFVersion();

	public void setStyleDocument(ODFDocument styleDoc);

	public ODFDocument getStyleDocument();
}
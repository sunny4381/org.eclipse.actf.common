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
package org.eclipse.actf.model.dom.odf.draw;

import org.eclipse.actf.model.dom.odf.base.ODFElement;

/**
 * Interface for &lt;draw:stroke-dash&gt; element.
 */
public interface StrokeDashElement extends ODFElement {
	public String getAttrDrawName();

	public String getAttrDrawDisplayName();

	public String getAttrDrawStyle();

	public int getAttrDrawDots1();

	public String getAttrDrawDots1Length();

	public int getAttrDrawDots2();

	public String getAttrDrawDots2Length();

	public String getAttrDrawDistance();
}
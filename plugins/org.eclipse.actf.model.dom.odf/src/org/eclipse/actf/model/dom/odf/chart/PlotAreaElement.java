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
package org.eclipse.actf.model.dom.odf.chart;

import org.eclipse.actf.model.dom.odf.base.ODFElement;
import org.eclipse.actf.model.dom.odf.content.IStylable;

/**
 * Interface for &lt;chart:plot-area&gt; element.
 */
public interface PlotAreaElement extends ODFElement, IStylable {
	public String getAttrTableCellRangeAddress();

	public String getAttrChartDataSourceHasLabels();

	public String getAttrChartTableNumberList();

	public String getAttrSvgX();

	public String getAttrSvgY();

	public String getAttrSvgWidth();

	public String getAttrSvgHeight();

	public String getAttrDr3dVrp();

	public String getAttrDr3dVpn();

	public String getAttrDr3dVup();

	public String getAttrDr3dProjection();

	public String getAttrDr3dDistance();

	public String getAttrDr3dFocalLength();

	public int getAttrDr3dShadowSlant();

	public String getAttrDr3dShadeMode();

	public String getAttrDr3dAmbientColor();

	public boolean getAttrDr3dLightingMode();
}
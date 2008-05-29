/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.ui.preferences;

import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

public class ScaleFieldEditorWithValue extends ScaleFieldEditor {

	private Label valueLabel;

	private Label minLabel;

	private Label maxLabel;

	private IScaleValueLabelProvider scaleValueLabelProvider;

	private class DefaultScaleValueLabelProvider implements
			IScaleValueLabelProvider {
		public String getScaleValueText(Scale scale) {
			return Integer.toString(scale.getSelection());
		}
	}

	public ScaleFieldEditorWithValue(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent);
		scaleValueLabelProvider = new DefaultScaleValueLabelProvider();
	}

	public ScaleFieldEditorWithValue(String name, String labelText,
			Composite parent, int min, int max, int increment, int pageIncrement) {
		super(name, labelText, parent, min, max, increment, pageIncrement);
		minLabel.setText(Integer.toString(min));
		maxLabel.setText(Integer.toString(max));
		scaleValueLabelProvider = new DefaultScaleValueLabelProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.ScaleFieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite,
	 *      int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);
		valueLabel = new Label(parent, SWT.RIGHT);
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		valueLabel.setLayoutData(gridData);

		scale.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				valueLabel.setText(scaleValueLabelProvider
						.getScaleValueText(scale));
			}
		});

		(new Label(parent, SWT.NONE)).setLayoutData(new GridData());

		minLabel = new Label(parent, SWT.LEFT);
		gridData = new GridData();
		gridData.widthHint = 50;
		minLabel.setLayoutData(gridData);

		maxLabel = new Label(parent, SWT.RIGHT);
		gridData = new GridData();
		gridData.widthHint = 50;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.RIGHT;
		maxLabel.setLayoutData(gridData);

		(new Label(parent, SWT.NONE)).setLayoutData(new GridData());

	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		super.adjustForNumColumns(numColumns - 1);
		((GridData) maxLabel.getLayoutData()).horizontalSpan = numColumns - 3;
	}

	@Override
	public int getNumberOfControls() {
		return super.getNumberOfControls() + 2;
	}

	@Override
	protected void doLoad() {
		super.doLoad();
		valueLabel.setText(scaleValueLabelProvider.getScaleValueText(scale));
	}

	@Override
	protected void doLoadDefault() {
		super.doLoadDefault();
		valueLabel.setText(scaleValueLabelProvider.getScaleValueText(scale));
	}

	public Label getMaxLabel() {
		return maxLabel;
	}

	public Label getMinLabel() {
		return minLabel;
	}

	public Label getValueLabel() {
		return valueLabel;
	}

	public void setScaleValueLabelProvider(
			IScaleValueLabelProvider scaleValueLabelProvider) {
		if (scaleValueLabelProvider != null) {
			this.scaleValueLabelProvider = scaleValueLabelProvider;
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom;

/**
 * ISelectElement interface defines the methods to be implemented by the form
 * select element such as combo box and list box.
 */
public interface ISelectElement extends INodeEx {
	/**
	 * @param indices
	 *            the indices to be set.
	 */
	public void setSelectedIndices(int[] indices);

	/**
	 * @return the selected indices.
	 */
	public int[] getSelectedIndices();

	/**
	 * @return the total number of the item in the select.
	 */
	public int getOptionsCount();

	/**
	 * @param index the index of the option to be obtained.
	 * @return the text of the item specified by the <i>index</i>.
	 */
	public String getOptionTextAt(int index);
}

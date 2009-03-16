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

package org.eclipse.actf.model.dom.dombycom.impl.html;

import java.util.ArrayList;

import org.eclipse.actf.model.dom.dombycom.ISelectElement;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

@SuppressWarnings("nls")
public class SelectElementImpl extends HTMLElementImpl implements
		ISelectElement {

	public SelectElementImpl(NodeImpl baseNode, IDispatch inode) {
		super(baseNode, inode);
	}

	public void setSelectedIndices(int[] indices) {
		int[] prev = getSelectedIndices();

		int length = (Integer) Helper.get(inode, "length");
		for (int i = 0; i < length; i++) {
			IDispatch option = (IDispatch) inode.invoke1("item", i);
			Helper.put(option, "selected", false);
		}

		for (int i = 0; i < indices.length; i++) {
			IDispatch option = (IDispatch) inode.invoke1("item", indices[i]);
			Helper.put(option, "selected", true);
		}

		if (Helper.hasProperty(inode, "onchange")) {
			if (prev.length != indices.length) {
				inode.invoke0("onchange");
			} else {
				for (int i = 0; i < prev.length; i++) {
					if (prev[i] != indices[i]) {
						inode.invoke0("onchange");
						break;
					}
				}
			}
		}

	}

	public int[] getSelectedIndices() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int length = (Integer) Helper.get(inode, "length");
		for (int i = 0; i < length; i++) {
			IDispatch option = (IDispatch) inode.invoke1("item", i);
			boolean selected = (Boolean) Helper.get(option, "selected");
			if (selected)
				list.add(i);
		}

		int[] indices = new int[list.size()];
		for (int i = 0; i < indices.length; i++)
			indices[i] = list.get(i);
		return indices;
	}

	public int getOptionsCount() {
		int length = (Integer) Helper.get(inode, "length");
		return length;
	}

	public String getOptionTextAt(int index) {
		IDispatch option = (IDispatch) inode.invoke1("item", index);
		return (String) Helper.get(option, "text");
	}
}

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom;

import java.util.ArrayList;

/**
 * AnalyzedResult is used as the object storage for DOM tree traversal.
 * Currently it will have video objects, sound objects, access key list, and
 * Flash nodes. You can collect those objects with
 * {@link INodeEx#analyze(AnalyzedResult)} method.
 */
public class AnalyzedResult {
	private static final INodeExVideo[] emptyVideoArray = new INodeExVideo[0];
	private static final INodeExSound[] emptySoundArray = new INodeExSound[0];
	private ArrayList<INodeExVideo> videoList = new ArrayList<INodeExVideo>();
	private ArrayList<INodeExSound> soundList = new ArrayList<INodeExSound>();

	private static final INodeEx[] emptyAccessKeyArray = new INodeEx[0];
	private ArrayList<INodeEx> accessKeyList = new ArrayList<INodeEx>();

	private static final IFlashNode[] emptyFlashTopNodeArray = new IFlashNode[0];
	private ArrayList<IFlashNode> flashNodeList = new ArrayList<IFlashNode>();

	/**
	 * This method is used by items of tree.
	 * @param f
	 */
	public void addFlashTopNode(IFlashNode f) {
		flashNodeList.add(f);
	}

	/**
	 * This method is used by items of tree.
	 * @param v
	 */
	public void addVideo(INodeExVideo v) {
		videoList.add(v);
	}

	/**
	 * This method is used by items of tree.
	 * @param s
	 */
	public void addSound(INodeExSound s) {
		soundList.add(s);
	}

	/**
	 * This method is used by items of tree.
	 * @param a
	 */
	public void addAccessKey(INodeEx a) {
		accessKeyList.add(a);
	}

	/**
	 * @return video objects in the tree.
	 */
	public INodeExVideo[] getVideoNodes() {
		return videoList.toArray(emptyVideoArray);
	}

	/**
	 * @return sound objects in the tree.
	 */
	public INodeExSound[] getSoundNodes() {
		return soundList.toArray(emptySoundArray);
	}

	/**
	 * @return node objects that has one or more access keys in the tree. 
	 */
	public INodeEx[] getAccessKeyNodes() {
		return accessKeyList.toArray(emptyAccessKeyArray);
	}

	/**
	 * @return Flash objects. Each object is corresponding to a Flash object.
	 */
	public IFlashNode[] getFlashTopNodes() {
		return flashNodeList.toArray(emptyFlashTopNodeArray);
	}
}

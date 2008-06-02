/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.IFlashMSAANode;
import org.eclipse.actf.model.dom.dombycom.IFlashNode;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.INodeExSound;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo;
import org.eclipse.actf.model.dom.dombycom.impl.Helper;
import org.eclipse.actf.model.dom.dombycom.impl.ListNodeListImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.model.dom.dombycom.impl.html.ElementImpl;
import org.eclipse.actf.model.flash.ASNode;
import org.eclipse.actf.model.flash.FlashPlayerFactory;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.flash.IFlashPlayer;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class FlashTopNodeImpl extends ElementImpl implements IFlashNode, IFlashConst {

	private static final String CLSID = "CLSID:D27CDB6E-AE6D-11CF-96B8-444553540000";
	private static final String APP_TYPE = "application/x-shockwave-flash";

	private static final IFlashNode[] emptyResult = new IFlashNode[0];

	private FlashMSAANodeImpl cachedMSAA;

	private IFlashPlayer flashPlayer;

	static FlashTopNodeImpl newFlashNode(NodeImpl baseNode, IDispatch inode) {
		String clsid = (String) Helper.get(inode, "classid");
		if (CLSID.equalsIgnoreCase(clsid)) {
			return new FlashTopNodeImpl(baseNode, inode);
		}
		String type = (String) Helper.get((IDispatch) inode.invoke1(
				"getAttributeNode", "type"), "value");
		if (APP_TYPE.equalsIgnoreCase(type)) {
			return new FlashTopNodeImpl(baseNode, inode);
		}
		return null;
	}

	private FlashTopNodeImpl(NodeImpl baseNode, IDispatch idisp) {
		super(baseNode, idisp);

		flashPlayer = FlashPlayerFactory.getPlayerFromIDsipatch(idisp);

	}

	private INodeExVideo[] searchVideo() {
		ASNode[] videos = flashPlayer.searchVideo();
		int len = videos.length;
		INodeExVideo[] result = new INodeExVideo[len];
		for (int i = 0; i < len; i++) {
			// System.err.println("SVO:" + videos[i]);
			result[i] = new FlashVideoImpl(this, videos[i]);
		}
		return result;
	}

	private INodeExSound[] searchSound() {
		ASNode[] sounds = flashPlayer.searchSound();
		int len = sounds.length;
		INodeExSound[] result = new INodeExSound[len];
		for (int i = 0; i < len; i++) {
			result[i] = new FlashSoundImpl(sounds[i]);
		}
		return result;
	}

	public String getTarget() {
		return "";
	}

	public IFlashNode getNodeFromPath(String path) {
		ASNode node = flashPlayer.getNodeFromPath(path);
		if (node == null)
			return null;
		return new FlashNodeImpl(this, node);
	}

	public IFlashNode getNodeAtDepth(int depth) {
		return null;
	}

	public IFlashNode[] getInnerNodes() {
		return emptyResult;
	}

	private IFlashNode[] createIFlashNodeArray(ASNode[] nodes) {
		IFlashNode[] results = new IFlashNode[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			results[i] = new FlashNodeImpl(this, nodes[i]);
		}
		return results;
	}

	public IFlashNode[] translate() {
		return createIFlashNodeArray(flashPlayer
				.translateWithPath(PATH_ROOTLEVEL));
	}

	public int getDepth() {
		return INVALID_DEPTH;
	}

	public int getCurrentFrame() {
		return -1;
	}

	public INodeEx getBaseNode() {
		return this;
	}

	// --------------------------------------------------------------------------------
	// Node Overriding Impl.
	// --------------------------------------------------------------------------------

	@Override
	public Node getFirstChild() {
		//System.err.println("invalid(getFirstChild).");
		return null;
	}

	@Override
	public Node getLastChild() {
		//System.err.println("invalid(getLastChild).");
		return null;
	}

	@Override
	public NodeList getChildNodes() {
		List<Node> l = new ArrayList<Node>(3);
		for (int i = 0; i < 3; i++) {
			String levelxName = "_level" + i;
			IFlashNode levelx = getNodeFromPath(levelxName);
			if (levelx != null) {
				// System.err.println(levelxName + " is found.");
				l.add(levelx);
			}
		}

		return new ListNodeListImpl(l);
	}

	private boolean hasMedia = false;

	boolean hasMedia() {
		return hasMedia;
	}

	@Override
	public AnalyzedResult analyze(AnalyzedResult ar) {
		INodeExVideo[] videos = searchVideo();
		if (videos.length > 0) {
			hasMedia = true;
			for (int i = 0; i < videos.length; i++) {
				ar.addVideo(videos[i]);
			}
		}
		INodeExSound[] sounds = searchSound();
		for (int i = 0; i < sounds.length; i++) {
			ar.addSound(sounds[i]);
		}
		ar.addFlashTopNode(this);
		return ar;
	}

	public void repairFlash() {
		flashPlayer.repairFlash();
	}

	public IFlashMSAANode getMSAA() {
		if (cachedMSAA == null)
			cachedMSAA = FlashMSAANodeImpl.newMSAANode(this, inode);
		if ((cachedMSAA == null) || (cachedMSAA.getWindow() == 0))
			return null;
		// repairFlash();
		updateTarget();
		return cachedMSAA;
	}

	public long getHWND() {
		return FlashMSAANodeImpl.getHWNDFromObject(super.getINode());
	}

	@Override
	public AbstractTerms getTerms() {
		return FlashTerms.getInstance();
	}

	// TODO...
	private boolean updatedTarget = false;

	public void updateTarget() {
		if (updatedTarget)
			return;
		updatedTarget = flashPlayer.updateTarget();
	}
}

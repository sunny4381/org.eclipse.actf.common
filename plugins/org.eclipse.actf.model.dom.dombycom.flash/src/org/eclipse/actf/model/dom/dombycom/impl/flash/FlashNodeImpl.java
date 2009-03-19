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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.IFlashMSAANode;
import org.eclipse.actf.model.dom.dombycom.IFlashNode;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.flash.ASAccInfo;
import org.eclipse.actf.model.flash.IASBridge;
import org.eclipse.actf.model.flash.IASNode;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.util.dom.EmptyNodeListImpl;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.win32.comclutch.ComPlugin;
import org.eclipse.actf.util.win32.keyhook.ISendEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

@SuppressWarnings("nls")
class FlashNodeImpl implements IFlashNode, IFlashConst {
	// final ASObject nodeASObj;
	private final ASAccInfo accInfo;
	private final String target;
	private final IFlashNode parent;
	private final boolean hasChildren;
	private FlashTopNodeImpl swf;
	private DocumentImpl doc;

	private final IASNode flashNode;
	private final IASBridge flashPlayer;

	@Override
	public int hashCode() {
		return this.target.intern().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FlashNodeImpl) {
			FlashNodeImpl f = (FlashNodeImpl) o;
			return swf.equals(f.swf) && this.target.equals(f.target);
		}
		return false;
	}

	private FlashNodeImpl(IASNode node, IFlashNode parent) {
		this.flashNode = node;
		this.flashPlayer = node.getPlayer();
		this.target = node.getTarget();
		this.accInfo = node.getAccInfo();
		if (node.isOpaqueObject()) {
			this.hasChildren = false;
		} else {
			this.hasChildren = true;
		}
		this.parent = parent;
	}

	FlashNodeImpl(FlashNodeImpl baseNode, IASNode node) {
		this(node, baseNode);
		this.swf = baseNode.swf;
		this.doc = baseNode.doc;
	}

	FlashNodeImpl(FlashTopNodeImpl baseNode, IASNode node) {
		this(node, baseNode);
		this.swf = baseNode;
		this.doc = (DocumentImpl) baseNode.getOwnerDocument();
	}

	private Object getProperty(String prop) {
		return flashPlayer.getProperty(getTarget(), prop);
	}

	private void setProperty(String prop, Object value) {
		flashPlayer.setProperty(getTarget(), prop, value);
	}

	private static ISendEvent sendEvent;

	private ISendEvent getSendEvent() {
		if (sendEvent == null) {
			sendEvent = ComPlugin.getDefault().newSendEvent();
		}
		return sendEvent;
	}

	public String getNodeName() {
		return "#flashNode:" + getTarget();
	}

	public String getNodeValue() throws DOMException {
		return null;
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public short getNodeType() {
		// return FLASH_NODE;
		return Node.ELEMENT_NODE;
	}

	public Node getParentNode() {
		return parent;
	}

	private static class NodeListImpl implements NodeList {
		private final IFlashNode[] nodes;

		public Node item(int index) {
			if ((index < 0) || (index >= nodes.length))
				return null;
			return nodes[index];
		}

		public int getLength() {
			return nodes.length;
		}

		NodeListImpl(IFlashNode[] nodes) {
			this.nodes = nodes;
		}
	}

	public NodeList getChildNodes() {
		if (!hasChildren)
			return EmptyNodeListImpl.getInstance();
		IFlashNode[] r = getSWFChildNodes();
		return new NodeListImpl(r);
	}

	public Node getFirstChild() {
		if (!hasChildren)
			return null;
		IFlashNode[] r = getSWFChildNodes();
		if (r == null)
			return null;
		if (r.length == 0)
			return null;
		return r[0];
	}

	public Node getLastChild() {
		if (!hasChildren)
			return null;
		IFlashNode[] r = getSWFChildNodes();
		if (r == null)
			return null;
		if (r.length == 0)
			return null;
		return r[r.length - 1];
	}

	public Node getPreviousSibling() {
		throw new UnsupportedOperationException();
	}

	public Node getNextSibling() {
		throw new UnsupportedOperationException();
	}

	public NamedNodeMap getAttributes() {
		return null;
	}

	public Document getOwnerDocument() {
		return doc;
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public Node removeChild(Node oldChild) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public Node appendChild(Node newChild) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public boolean hasChildNodes() {
		// IFlashNode[] r = getInnerNodes();
		if (!hasChildren)
			return false;
		IFlashNode[] r = getSWFChildNodes();
		if (r == null)
			return false;
		if (r.length == 0)
			return false;
		return true;
	}

	public Node cloneNode(boolean deep) {
		throw new UnsupportedOperationException();
	}

	public void normalize() {
	}

	public boolean isSupported(String feature, String version) {
		return false;
	}

	public String getNamespaceURI() {
		return null;
	}

	public String getPrefix() {
		return null;
	}

	public void setPrefix(String prefix) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public String getLocalName() {
		String target = getTarget();
		int idx = target.lastIndexOf('.');
		if (idx < 0)
			return target;
		return target.substring(idx + 1);
	}

	public boolean hasAttributes() {
		return false;
	}

	public String getBaseURI() {
		return null;
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return 0;
	}

	public String getTextContent() throws DOMException {
		return extractString();
	}

	public void setTextContent(String textContent) throws DOMException {
		throw new UnsupportedOperationException();
	}

	public boolean isSameNode(Node other) {
		return false;
	}

	public String lookupPrefix(String namespaceURI) {
		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return false;
	}

	public String lookupNamespaceURI(String prefix) {
		return null;
	}

	public boolean isEqualNode(Node arg) {
		return false;
	}

	public Object getFeature(String feature, String version) {
		return null;
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new UnsupportedOperationException();
	}

	public Object getUserData(String key) {
		throw new UnsupportedOperationException();
	}

	// --------------------------------------------------------------------------------
	// INodeEx interface
	// --------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private String decodeString(String input) {
		if (input == null)
			return "";
		try {
			return URLDecoder.decode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return input;
	}

	public String extractString() {
		String r = null;
		if (null != accInfo) {
			r = accInfo.getName();
		}
		if (r == null) {
			r = flashNode.getText(false);
		}
		if (r == null)
			return "";
		// return decodeString(r);
		return r;
	}

	public short getHeadingLevel() {
		return 0;
	}

	public String getLinkURI() {
		// TODO
		return null;
	}

	public void setText(String text) {
		setProperty(ASNODE_TEXT, text);
	}

	public String getText() {
		Object o = getProperty(ASNODE_TEXT);
		if (o instanceof String)
			return (String) o;
		return "";
	}

	private boolean doHardwareClick() {
		long hwnd = swf.getHWND();
		int ix = (int) flashNode.getX();
		int iy = (int) flashNode.getY();
		int iw = (int) flashNode.getWidth();
		int ih = (int) flashNode.getHeight();
		int x = ix + iw / 4;
		int y = iy + ih / 4;

		// System.err.println("Click to:" + x + "," + y);

		// getSendEvent().postMouse(x, y, false);
		getSendEvent().focusWindow(hwnd);
		setFocus();
		getSendEvent().postMouse(x, y, false);
		boolean result = getSendEvent().postMouse(x, y, true);
		return result;
	}

	String getClickableTarget(String current) {
		while (current.length() > 0) {
			String tryOnRelease = current + PATH_ON_RELEASE;
			if (swf.getNodeFromPath(tryOnRelease) != null) {
				return current;
			}
			String tryOnPress = current + PATH_ON_PRESS;
			if (swf.getNodeFromPath(tryOnPress) != null) {
				return current;
			}
			int idx = current.lastIndexOf('.');
			if (idx <= 0)
				break;
			current = current.substring(0, idx);
		}
		return null;
	}

	public boolean doClick() {
		String current = getTarget();
		while (current.length() > 0) {
			String tryOnRelease = current + PATH_ON_RELEASE;
			if (swf.getNodeFromPath(tryOnRelease) != null) {
				flashPlayer.callMethod(flashNode, M_ON_RELEASE);
				return true;
			}
			String tryOnPress = current + PATH_ON_PRESS;
			if (swf.getNodeFromPath(tryOnPress) != null) {
				flashPlayer.callMethod(flashNode, M_ON_PRESS);
				return true;
			}
			if (true)
				break;
			int idx = current.lastIndexOf('.');
			if (idx <= 0)
				break;
			current = current.substring(0, idx);
		}
		if (doHardwareClick())
			return true;
		return false;
	}

	public boolean highlight() {
		flashPlayer.callMethod(flashNode, M_ON_ROLL_OVER);
		boolean ret1 = swf.highlight();
		boolean ret2 = flashNode.setMarker();
		return ret1 && ret2;
	}

	public boolean unhighlight() {
		flashPlayer.callMethod(flashNode, M_ON_ROLL_OUT);
		boolean ret1 = swf.unhighlight();
		boolean ret2 = flashPlayer.unsetMarker();
		return ret1 && ret2;
	}

	public boolean setFocus() {
		if (!swf.setFocus())
			return false;
		return flashPlayer.setFocus(getTarget());
	}

	public int getNth() {
		// TODO!
		return 0;
	}

	// --------------------------------------------------------------------------------
	// IFlashNode interface
	// --------------------------------------------------------------------------------

	public String getTarget() {
		return target;
	}

	public IFlashNode getNodeFromPath(String path) {
		return swf.getNodeFromPath(path);
	}

	public IFlashNode getNodeAtDepth(int depth) {
		IASNode result = flashPlayer.getNodeAtDepthWithPath(getTarget(), depth);
		if (result == null)
			return null;
		return new FlashNodeImpl(swf, result);
	}

	private IFlashNode[] createIFlashNodeArray(IASNode[] nodes) {
		IFlashNode[] results = new IFlashNode[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			results[i] = new FlashNodeImpl(this, nodes[i]);
		}
		return results;
	}

	public IFlashNode[] getInnerNodes() {
		return createIFlashNodeArray(flashNode.getChildren(true));
	}

	public IFlashNode[] getSWFChildNodes() {
		return createIFlashNodeArray(flashNode.getChildren(false));
	}

	public int getDepth() {
		return flashNode.getDepth();
	}

	public int getCurrentFrame() {
		return flashNode.getCurrentFrame();
	}

	public IFlashNode[] translate() {
		return createIFlashNodeArray(flashPlayer.translateWithPath(getTarget()));
	}

	public INodeEx getBaseNode() {
		return swf;
	}

	// --------------------------------------------------------------------------------
	// extensions.
	// --------------------------------------------------------------------------------

	boolean isInputable() {
		return flashNode.isInputable();
	}

	boolean isSilent() {
		return accInfo.isSilent();
	}

	public AnalyzedResult analyze(AnalyzedResult ar) {
		return ar;
	}

	public AbstractTerms getTerms() {
		return FlashTerms.getInstance();
	}

	public Rectangle getLocation() {
		// TODO
		return swf.getLocation();
	}

	// --------------------------------------------------------------------------------
	// Dummy
	// --------------------------------------------------------------------------------
	public IFlashMSAANode getMSAA() {
		return null;
	}

	public void repairFlash() {
	}

	// !FN!
	public String[] getStillPictureData() {
		return new String[3];
	}

	public char getAccessKey() {
		return 0;
	}

}

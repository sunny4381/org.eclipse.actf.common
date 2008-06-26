/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.actf.model.flash.as.ASObject;
import org.eclipse.actf.model.flash.internal.Messages;

public class ASNode {

	private IFlashPlayer player;
	private ASObject asObject;
	private ASNode parent;
	private int level;
	private ASAccInfo accInfo;
	private boolean isReference = false;
	private boolean skipChildren = false;
	private boolean isAccProperties = false;

	private Boolean hasOnRelease;

	private String strType;
	private String strClassName;
	private String strObjectName;
	private String strTarget;
	private boolean isUIComponent;

	public ASNode(ASNode parent, IFlashPlayer player, ASObject node) {
		this.parent = parent;
		this.level = null != parent ? parent.getLevel() + 1 : 0;
		this.player = player;

		asObject = node;
		strType = getString(IFlashConst.ASNODE_TYPE);
		strClassName = getString(IFlashConst.ASNODE_CLASS_NAME);
		strObjectName = getString(IFlashConst.ASNODE_OBJECT_NAME);
		strTarget = getString(IFlashConst.ASNODE_TARGET);
		isUIComponent = "true".equals(getString(IFlashConst.ASNODE_IS_UI_COMPONENT)); //$NON-NLS-1$

		if (null != parent) {
			String targetParent = parent.getTarget();
			if (null != targetParent) {
				if (!strTarget.equals(targetParent + "." + strObjectName)) { //$NON-NLS-1$
					isReference = true;
				}
			}
			String parentObjectName = parent.getObjectName();
			if ("_accProps".equals(parentObjectName) || //$NON-NLS-1$
					"_accImpl".equals(parentObjectName)) { //$NON-NLS-1$
				isAccProperties = true;
			}
		}
		if ("number".equals(strType) || //$NON-NLS-1$
				"null".equals(strType) || //$NON-NLS-1$
				"boolean".equals(strType) || //$NON-NLS-1$
				"string".equals(strType) || //$NON-NLS-1$
				"undefined".equals(strType)) { //$NON-NLS-1$
			skipChildren = true;
		} else if ("object".equals(strType)) { //$NON-NLS-1$
			if (null == strClassName || "Array".equals(strClassName)) { //$NON-NLS-1$
				skipChildren = true;
			}
		} else if ("movieclip".equals(strType)) { //$NON-NLS-1$
			if ("_level0.reserved".equals(strTarget) || //$NON-NLS-1$
					"_level0.focusManager".equals(strTarget)) { //$NON-NLS-1$
				skipChildren = true;
			}
		}
		this.accInfo = ASAccInfo.create(asObject);
	}

	public String getType() {
		return strType;
	}

	public String getClassName() {
		return strClassName;
	}

	public String getObjectName() {
		return strObjectName;
	}

	public String getTarget() {
		return strTarget;
	}

	public boolean isUIComponent() {
		return isUIComponent;
	}

	public String getValue() {
		if (null != asObject) {
			return decodeString(getString(IFlashConst.ASNODE_VALUE));
		}
		return null;
	}

	public String getText() {
		return getText(true);
	}

	public String getText(boolean useAccName) {
		String text = null;
		if (useAccName && null != accInfo) {
			text = accInfo.getName();
		}
		if (null == text) {
			if (null != asObject) {
				text = getString(IFlashConst.ASNODE_TEXT);
			}
		} else {
			text = "[" + text + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return decodeString(text);
	}

	public String getTitle() {
		if (null != asObject) {
			return decodeString(getString(IFlashConst.ASNODE_TITLE));
		}
		return null;
	}

	private String getString(String name) {
		if (null != asObject) {
			Object result = asObject.get(name);
			return null == result ? null : result.toString();
		}
		return null;
	}

	public Object getObject(String name) {
		if (null != asObject) {
			return asObject.get(name);
		}
		return null;
	}

	private String decodeString(String input) {
		if (null != input) {
			try {
				return URLDecoder.decode(input, "UTF-8"); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return input;
	}

	private double getDoubleValue(Object o) {
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		return Double.NaN;
	}

	public ASNode getParent() {
		return parent;
	}

	public int getLevel() {
		return level;
	}

	private boolean shouldSkip() {
		return (skipChildren && !isAccProperties) || isReference;
	}

	private boolean isAccProperties() {
		return isAccProperties;
	}

	public boolean hasChild(boolean visual) {
		return hasChild(visual, false);
	}

	public boolean hasChild(boolean visual, boolean debugMode) {
		if (level >= 50) {
			throw new Error(
					MessageFormat
							.format(
									Messages
											.getString("flash.error_target_length"), new Object[] { new Integer(level) }) + "\n" + strTarget); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return player.hasChild(this, visual, debugMode);
	}

	public Object[] getChildren(boolean visual, boolean informative) {
		return getChildren(visual, informative, false);
	}

	public Object[] getChildren(boolean visual, boolean informative,
			boolean debugMode) {
		ASNode[] children = player.getChildren(this, visual, debugMode);
		List<ASNode> childList = new ArrayList<ASNode>();
		for (int i = 0; i < children.length; i++) {
			ASNode node = children[i];
			if (!debugMode) {
				if (!visual && node.shouldSkip()) {
					continue;
				}
				if (informative && !node.isAccProperties()) {
					if (null == node.getText()
							&& !"movieclip".equals(node.getType()) && //$NON-NLS-1$
							!"Button".equals(node.getClassName()) && //$NON-NLS-1$
							!"_accProps".equals(node.getObjectName()) && //$NON-NLS-1$
							!"_accImpl".equals(node.getObjectName()) && //$NON-NLS-1$
							!"onRelease".equals(node.getObjectName())) //$NON-NLS-1$
					{
						continue;
					}
				}
			}
			childList.add(node);
		}
		return childList.toArray();
	}

	public boolean setMarker() {
		if (null != asObject) {
			try {
				return player.setMarker((Number) asObject
						.get(IFlashConst.ASNODE_X), (Number) asObject
						.get(IFlashConst.ASNODE_Y), (Number) asObject
						.get(IFlashConst.ASNODE_WIDTH), (Number) asObject
						.get(IFlashConst.ASNODE_HEIGHT));
			} catch (Exception e) {
			}
		}
		return false;
	}

	public IFlashPlayer getPlayer() {
		return player;
	}

	public ASAccInfo getAccInfo() {
		return accInfo;
	}

	public Set<String> getKeys() {
		if (null != asObject) {
			return asObject.getKeys();
		}
		return null;
	}

	public boolean hasOnRelease() {
		if (null == hasOnRelease) {
			ASNode onReleaseNode = player.getNodeFromPath(strTarget
					+ IFlashConst.PATH_ON_RELEASE); //$NON-NLS-1$
			if (null != onReleaseNode) {
				hasOnRelease = Boolean.TRUE;
			} else {
				hasOnRelease = Boolean.FALSE;
			}
		}
		return hasOnRelease.booleanValue();
	}

	public double getX() {
		return getDoubleValue(asObject.get(IFlashConst.ASNODE_X));
	}

	public double getY() {
		return getDoubleValue(asObject.get(IFlashConst.ASNODE_Y));
	}

	public double getWidth() {
		return getDoubleValue(asObject.get(IFlashConst.ASNODE_WIDTH));
	}

	public double getHeight() {
		return getDoubleValue(asObject.get(IFlashConst.ASNODE_HEIGHT));
	}

	public int getDepth() {
		Integer target = (Integer) asObject.get(IFlashConst.ASNODE_DEPTH);
		if (target != null)
			return target.intValue();
		return IFlashConst.INVALID_DEPTH;
	}

	public int getCurrentFrame() {
		Integer target = (Integer) asObject
				.get(IFlashConst.ASNODE_CURRENT_FRAME);
		if (target != null)
			return target.intValue();
		return -1;
	}

	public boolean isInputable() {
		Boolean b = (Boolean) asObject.get(IFlashConst.ASNODE_IS_INPUTABLE);
		if (b == null)
			return false;
		return b.booleanValue();
	}

	public boolean isOpaqueObject() {
		Boolean b = (Boolean) asObject.get(IFlashConst.ASNODE_IS_OPAQUE_OBJECT);
		if (b == null)
			return false;
		return b.booleanValue();
	}

}

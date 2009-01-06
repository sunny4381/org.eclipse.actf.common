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
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.actf.model.flash.ASAccInfo;
import org.eclipse.actf.model.flash.IASNode;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.flash.IFlashPlayer;
import org.eclipse.actf.model.flash.as.ASObject;

import com.ibm.icu.text.MessageFormat;

public class ASNodeImplV8 implements IFlashConst, IASNode {

	private static final String LEVEL0_FOCUS_MANAGER = "_level0.focusManager";
	private static final String LEVEL0_RESERVED = "_level0.reserved";

	private IFlashPlayer player;
	private ASObject asObject;
	private IASNode parent;
	private int level;
	private int id;
	private ASAccInfo accInfo;
	private boolean isReference = false;
	private boolean skipChildren = false;
	private boolean isAccProperties = false;

	private Boolean hasOnRelease;

	private String strIconType = ASNODE_ICON_OTHERS;
	private String strType;
	private String strClassName;
	private String strObjectName;
	private String strTarget;
	private boolean isUIComponent;

	public ASNodeImplV8(IASNode parent, IFlashPlayer player, ASObject node) {
		this.parent = parent;
		this.level = null != parent ? parent.getLevel() + 1 : 0;
		this.player = player;

		asObject = node;
		strType = getString(ASNODE_TYPE);
		strClassName = getString(ASNODE_CLASS_NAME);
		strObjectName = getString(ASNODE_OBJECT_NAME);
		strTarget = getString(ASNODE_TARGET);
		Object tmpId = asObject.get(ASNODE_ID);
		if (tmpId instanceof Integer) {
			id = ((Integer) tmpId).intValue();
		} else {
			id = -1;
		}

		isUIComponent = "true".equals(getString(ASNODE_IS_UI_COMPONENT)); //$NON-NLS-1$

		if (null != parent) {
			String targetParent = parent.getTarget();
			if (null != targetParent) {
				if (!strTarget.equals(targetParent + "." + strObjectName)) { //$NON-NLS-1$
					isReference = true;
				}
			}
			String parentObjectName = parent.getObjectName();
			if (ACC_PROPS.equals(parentObjectName) || //$NON-NLS-1$
					ACC_IMPL.equals(parentObjectName)) { //$NON-NLS-1$
				isAccProperties = true;
			}
		}
		if (ASNODE_TYPE_NUMBER.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_NULL.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_BOOLEAN.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_STRING.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_UNDEFINED.equals(strType)) { //$NON-NLS-1$
			skipChildren = true;
		} else if (ASNODE_TYPE_OBJECT.equals(strType)) { //$NON-NLS-1$
			if (null == strClassName || ASNODE_CLASS_ARRAY.equals(strClassName)) { //$NON-NLS-1$
				skipChildren = true;
			}
		} else if (ASNODE_TYPE_MOVIECLIP.equals(strType)) { //$NON-NLS-1$
			if (LEVEL0_RESERVED.equals(strTarget) || //$NON-NLS-1$
					LEVEL0_FOCUS_MANAGER.equals(strTarget)) { //$NON-NLS-1$
				skipChildren = true;
			}
		}
		this.accInfo = ASAccInfo.create(asObject);

		initIconType();
	}

	private void initIconType() {
		if (ASNODE_TYPE_MOVIECLIP.equals(strType)) { //$NON-NLS-1$
			strIconType = strType;
			if (accInfo != null) {
				int accRole = accInfo.getRole();
				if (-1 != accRole) {
					strIconType = ASNODE_ICON_ACCROLE + accRole;
					return;
				}
			}
			if (hasOnRelease()) {
				strIconType = ASNODE_CLASS_BUTTON;
			}
		} else if (ASNODE_TYPE_OBJECT.equals(strType)) { //$NON-NLS-1$
			strIconType = strType;
			if (ASNODE_CLASS_BUTTON.equals(strClassName)) { //$NON-NLS-1$
				strIconType = ASNODE_CLASS_BUTTON;
			} else if (strClassName.startsWith(ASNODE_CLASS_TEXTFIELD)) { //$NON-NLS-1$
				strIconType = ASNODE_ICON_TEXT;
			} else {
				if (ACC_IMPL.equals(strObjectName) || //$NON-NLS-1$
						ACC_PROPS.equals(strObjectName)) { //$NON-NLS-1$
					strIconType = ASNODE_ICON_ACCPROPS;
				}
			}
		} else if (ASNODE_TYPE_FUNCTION.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_STRING.equals(strType)) { //$NON-NLS-1$
			strIconType = strType;
		} else if (ASNODE_TYPE_NUMBER.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_NULL.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_BOOLEAN.equals(strType) || //$NON-NLS-1$
				ASNODE_TYPE_UNDEFINED.equals(strType)) { //$NON-NLS-1$
			strIconType = ASNODE_ICON_VARIABLE;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getType()
	 */
	public String getType() {
		return strType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getClassName()
	 */
	public String getClassName() {
		return strClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getObjectName()
	 */
	public String getObjectName() {
		return strObjectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getTarget()
	 */
	public String getTarget() {
		return strTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#isUIComponent()
	 */
	public boolean isUIComponent() {
		return isUIComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getValue()
	 */
	public String getValue() {
		if (null != asObject) {
			return decodeString(getString(ASNODE_VALUE));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getText()
	 */
	public String getText() {
		return getText(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getText(boolean)
	 */
	public String getText(boolean useAccName) {
		String text = null;
		if (useAccName && null != accInfo) {
			text = accInfo.getName();
		}
		if (null == text) {
			if (null != asObject) {
				text = getString(ASNODE_TEXT);
			}
		} else {
			text = "[" + text + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return decodeString(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getTitle()
	 */
	public String getTitle() {
		if (null != asObject) {
			return decodeString(getString(ASNODE_TITLE));
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

	private Number getNumber(String name) {
		if (null != asObject) {
			Object result = asObject.get(name);
			if (result == null)
				return null;
			else if (result instanceof Number)
				return (Number) result;
			else
				return null;
		} else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getObject(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getParent()
	 */
	public IASNode getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getLevel()
	 */
	public int getLevel() {
		return level;
	}

	private boolean shouldSkip() {
		return (skipChildren && !isAccProperties) || isReference;
	}

	public boolean isAccProperties() {
		return isAccProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#hasChild(boolean)
	 */
	public boolean hasChild(boolean visual) {
		return hasChild(visual, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#hasChild(boolean, boolean)
	 */
	public boolean hasChild(boolean visual, boolean debugMode) {
		if (level >= 50) {
			throw new Error(
					MessageFormat
							.format(
									Messages.flash_error_target_length, new Object[] { new Integer(level) }) + "\n" + strTarget); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return player.hasChild(this, visual, debugMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getChildren(boolean)
	 */
	public IASNode[] getChildren(boolean visual) {
		return getChildren(visual, false);
	}

	public IASNode[] getEntireChildren() {
		return getChildren(false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getChildren(boolean, boolean,
	 *      boolean)
	 */
	private IASNode[] getChildren(boolean visual, boolean debugMode) {
		IASNode[] children = player.getChildren(this, visual, debugMode);
		List<ASNodeImplV8> childList = new ArrayList<ASNodeImplV8>();
		for (IASNode child : children) {
			if (child instanceof ASNodeImplV8) {
				ASNodeImplV8 node = (ASNodeImplV8) child;
				if (!debugMode) {
					if (!visual && node.shouldSkip()) {
						continue;
					}
				}
				childList.add(node);
			}
		}
		return childList.toArray(new IASNode[childList.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#setMarker()
	 */
	public boolean setMarker() {
		if (null != asObject) {
			try {
				return player.setMarker(getNumber(ASNODE_X),
						getNumber(ASNODE_Y), getNumber(ASNODE_WIDTH),
						getNumber(ASNODE_HEIGHT));
			} catch (Exception e) {
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getPlayer()
	 */
	public IFlashPlayer getPlayer() {
		return player;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getAccInfo()
	 */
	public ASAccInfo getAccInfo() {
		return accInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getKeys()
	 */
	public Set<String> getKeys() {
		if (null != asObject) {
			return asObject.getKeys();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#hasOnRelease()
	 */
	public boolean hasOnRelease() {
		if (null == hasOnRelease) {
			IASNode onReleaseNode = player.getNodeFromPath(strTarget
					+ PATH_ON_RELEASE); //$NON-NLS-1$
			if (null != onReleaseNode) {
				hasOnRelease = Boolean.TRUE;
			} else {
				hasOnRelease = Boolean.FALSE;
			}
		}
		return hasOnRelease.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getX()
	 */
	public double getX() {
		return getDoubleValue(asObject.get(ASNODE_X));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getY()
	 */
	public double getY() {
		return getDoubleValue(asObject.get(ASNODE_Y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getWidth()
	 */
	public double getWidth() {
		return getDoubleValue(asObject.get(ASNODE_WIDTH));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getHeight()
	 */
	public double getHeight() {
		return getDoubleValue(asObject.get(ASNODE_HEIGHT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getId()
	 */
	public int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getDepth()
	 */
	public int getDepth() {
		Integer target = (Integer) asObject.get(ASNODE_DEPTH);
		if (target != null)
			return target.intValue();
		return INVALID_DEPTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getCurrentFrame()
	 */
	public int getCurrentFrame() {
		Integer target = (Integer) asObject.get(ASNODE_CURRENT_FRAME);
		if (target != null)
			return target.intValue();
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#isInputable()
	 */
	public boolean isInputable() {
		Boolean b = (Boolean) asObject.get(ASNODE_IS_INPUTABLE);
		if (b == null)
			return false;
		return b.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#isOpaqueObject()
	 */
	public boolean isOpaqueObject() {
		Boolean b = (Boolean) asObject.get(ASNODE_IS_OPAQUE_OBJECT);
		if (b == null)
			return false;
		return b.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getTabIndex()
	 */
	public int getTabIndex() {
		Number num = getNumber(ASNODE_TAB_INDEX);
		if (num == null)
			return -1;
		else
			return num.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.flash.IASNode#getIconType()
	 */
	public String getIconType() {
		return strIconType;
	}

}

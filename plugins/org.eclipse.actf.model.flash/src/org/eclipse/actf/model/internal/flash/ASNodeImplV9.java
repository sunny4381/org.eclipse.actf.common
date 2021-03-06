/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and Others
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

public class ASNodeImplV9 implements IFlashConst, IASNode {

	private static final String LEVEL0_FOCUS_MANAGER = "_level0.focusManager"; //$NON-NLS-1$
	private static final String LEVEL0_RESERVED = "_level0.reserved"; //$NON-NLS-1$

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

	public ASNodeImplV9(IASNode parent, IFlashPlayer player, ASObject node) {
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
			if (ACC_PROPS.equals(parentObjectName) || 
					ACC_IMPL.equals(parentObjectName)) { 
				isAccProperties = true;
			}
		}
		if (ASNODE_TYPE_NUMBER.equals(strType) || 
				ASNODE_TYPE_NULL.equals(strType) || 
				ASNODE_TYPE_BOOLEAN.equals(strType) || 
				ASNODE_TYPE_STRING.equals(strType) || 
				ASNODE_TYPE_UNDEFINED.equals(strType)) { 
			skipChildren = true;
		} else if (ASNODE_TYPE_OBJECT.equals(strType)) { 
			if (null == strClassName || ASNODE_CLASS_ARRAY.equals(strClassName)) { 
				skipChildren = true;
			}
		} else if (ASNODE_TYPE_MOVIECLIP.equals(strType)) { 
			if (LEVEL0_RESERVED.equals(strTarget) || 
					LEVEL0_FOCUS_MANAGER.equals(strTarget)) { 
				skipChildren = true;
			}
		}
		this.accInfo = ASAccInfo.create(asObject);

		initIconType();
	}

	@SuppressWarnings("nls")
	private void initIconType() {
		if (ASNODE_TYPE_MOVIECLIP.equals(strType)) { 
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
		} else if (ASNODE_TYPE_OBJECT.equals(strType)) { 
			strIconType = strType;
			if (ASNODE_CLASS_BUTTON.equals(strClassName)) { 
				strIconType = ASNODE_CLASS_BUTTON;
			} else if (strClassName.startsWith(ASNODE_CLASS_TEXTFIELD)) { 
				strIconType = ASNODE_ICON_TEXT;
			} else {
				if (ACC_IMPL.equals(strObjectName) || 
						ACC_PROPS.equals(strObjectName)) { 
					strIconType = ASNODE_ICON_ACCPROPS;
				}
			}
		} else if (ASNODE_TYPE_FUNCTION.equals(strType) || 
				ASNODE_TYPE_STRING.equals(strType)) { 
			strIconType = strType;
		} else if (ASNODE_TYPE_NUMBER.equals(strType) || 
				ASNODE_TYPE_NULL.equals(strType) || 
				ASNODE_TYPE_BOOLEAN.equals(strType) || 
				ASNODE_TYPE_UNDEFINED.equals(strType)) { 
			strIconType = ASNODE_ICON_VARIABLE;
		} else if (ASNODE_TYPE_DISPLAYOBJECT.equals(strType)) { 
			// flash.display::
			if (strClassName.contains(ASNODE_CLASS_TEXTFIELD)) { 
				strIconType = ASNODE_ICON_TEXT;
			} else if (strClassName.contains("mx.controls::RichTextEditor")
					|| strClassName.contains("mx.controls::DateChooser")) {
				strIconType = ASNODE_ICON_ACCROLE + "9";// WINDOW
			} else if (strClassName.contains("mx.controls::"
					+ ASNODE_CLASS_SHAPE)) {
				strIconType = ASNODE_ICON_ACCROLE + "40";// graphics
			} else if (strClassName.contains("AVM1Movie")) {
				strIconType = ASNODE_TYPE_MOVIECLIP;
			} else if (strClassName.contains("Bitmap")) {
				strIconType = "Image";
			} else if (strClassName.contains("SimpleButton")) {
				strIconType = ASNODE_CLASS_BUTTON;
				// mx.controls::
			} else if (strClassName.contains("mx.controls::Text")
					|| strClassName.contains("flash.text::StaticText")) {
				strIconType = ASNODE_ICON_ACCROLE + "41";// STATICTEXT
			} else if (strClassName.contains("CheckBox")) {
				strIconType = "CheckBox";
			} else if (strClassName.contains("ComboBox")
					|| strClassName.contains("mx.controls::DateField")) {
				strIconType = "ComboBox";
			} else if (strClassName.contains("DataGridHeader")) {
				strIconType = ASNODE_ICON_ACCROLE + "26";// ROWHEADER
			} else if (strClassName.contains("mx.controls::DataGrid")) {
				strIconType = "DataGrid";
			} else if (strClassName.contains("ScrollBar")) {
				strIconType = "ScrollBar";
			} else if (strClassName.contains("Slider")) {
				strIconType = "Slider";
			} else if (strClassName.contains("Image")) {
				strIconType = "Image";
			} else if (strClassName.contains("Label")) {
				strIconType = "Label";
			} else if (strClassName.contains("ListItem")) {
				strIconType = ASNODE_ICON_ACCROLE + "34";// LISTITEM
			} else if (strClassName.contains("List")) {
				strIconType = "List";
			} else if (strClassName.contains("ProgressBar")) {
				strIconType = "ProgressBar";
			} else if (strClassName.contains("TreeItem")) {
				strIconType = ASNODE_ICON_ACCROLE + "36";// OUTLINEITEM
			} else if (strClassName.contains("Tree")) {
				strIconType = "Tree";
			} else if (strClassName.contains("SWFLoader")) {
				strIconType = ASNODE_TYPE_MOVIECLIP;
			} else if (strClassName.contains("Loader")) {
				strIconType = ASNODE_TYPE_MOVIECLIP;
			} else if (strClassName.contains("RadioButton")) {
				strIconType = "RadioButton";
			} else if (strClassName.contains("mx.containers::HBox")
					|| strClassName.contains("mx.containers::VBox")
					|| strClassName.contains("mx.controls::Spacer")) {
				strIconType = ASNODE_ICON_ACCROLE + "10";// CLIENT
			} else if (strClassName.contains(ASNODE_CLASS_BUTTON)) {
				strIconType = ASNODE_CLASS_BUTTON;
			} else {
				if (isUIComponent) {
					strIconType = ASNODE_ICON_COMPONENT;
				} else {
					// TODO
					strIconType = ASNODE_ICON_COMPONENT;
				}
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
			}
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
			throw new Error(MessageFormat.format(
					Messages.flash_error_target_length,
					new Object[] { new Integer(level) })
					+ "\n" + strTarget); //$NON-NLS-1$ 
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
		List<ASNodeImplV9> childList = new ArrayList<ASNodeImplV9>();
		for (IASNode child : children) {
			if (child instanceof ASNodeImplV9) {
				ASNodeImplV9 node = (ASNodeImplV9) child;
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
					+ PATH_ON_RELEASE); 
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

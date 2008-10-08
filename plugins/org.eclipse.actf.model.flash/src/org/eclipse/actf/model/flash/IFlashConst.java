/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

/**
 * Interface holding constants used by Flash related classes.
 */
public interface IFlashConst {

	// general
	/**
	 * Class ID value for Flash Player components
	 */
	public static final String CLSID_FLASH = "{D27CDB6E-AE6D-11CF-96B8-444553540000}";
	/**
	 * ActionScript value which means invalid depth
	 */
	public static final int INVALID_DEPTH = -16384;
	/**
	 * Name of the ActionScript variable for Flash Player version.
	 */
	public static final String PLAYER_VERSION = "$version";
	/**
	 * Name of the ActionScript variable for the URL from which the Flash movie
	 * is downloaded.
	 */
	public static final String CONTENT_URL = "_url";

	/**
	 * Name of accImpl object.
	 */
	public static final String ACC_IMPL = "_accImpl";
	/**
	 * Name of accProps object.
	 */
	public static final String ACC_PROPS = "_accProps";

	// Browser Ready State
	/**
	 * Name of the variable for browser's ready state.
	 */
	public static final String READY_STATE = "readyState";
	/**
	 * Value of ready state which means "complete".
	 */
	public static final Integer COMPLETED_READY_STATE = Integer.valueOf(4);

	// initialize
	/**
	 * ActionScript variable name which denotes the "Level 0", into which the
	 * root SWF is loaded.
	 */
	public static final String PATH_ROOTLEVEL = "_level0"; //$NON-NLS-1$
	/**
	 * ActionScript variable name which denotes the "Level 53553", into which
	 * the bridge SWF is loaded. 53553 is choosed so that Flash developers
	 * rarely use the level.
	 */
	public static final String PATH_BRIDGELEVEL = "_level53553"; //$NON-NLS-1$
	/**
	 * ActionScript variable name which denotes the availability of our bridge
	 * mechanism. If the bridge is correctly injected, this variable is
	 * accessible from the external program.
	 */
	public static final String PATH_IS_AVAILABLE = ".Eclipse_ACTF_is_available"; //$NON-NLS-1$
	/**
	 * ActionScript variable name which denotes the content ID of the Flash
	 * movie. The ACTF proxy and boot loader set the ID for authentication
	 * between the program using this class and browser component.
	 */
	public static final String PATH_CONTENT_ID = ".Eclipse_ACTF_SWF_CONTENT_ID";

	// other paths
	/**
	 * ActionScript variable name which denotes the global variables store.
	 */
	public static final String PATH_GLOBAL = "_global"; //$NON-NLS-1$

	/**
	 * ActionScript variable name which denotes the mouse (button pressing)
	 * event handler.
	 */
	public static final String PATH_ON_PRESS = ".onPress";
	/**
	 * ActionScript variable name which denotes the mouse (button releasing)
	 * event handler.
	 */
	public static final String PATH_ON_RELEASE = ".onRelease";

	// V8
	/**
	 * ActionScript variable name to which we put request arguments to the
	 * bridge Flash component.
	 */
	public static final String PROP_REQUEST_ARGS = ".Eclipse_ACTF_request_args";
	/**
	 * ActionScript variable name from which we get response from the bridge
	 * Flash component.
	 */
	public static final String PROP_RESPONSE_VALUE = ".Eclipse_ACTF_response_value";
	/**
	 * Flash Player method name to set a value to a ActionScript variable.
	 */
	public static final String PLAYER_SET_VARIABLE = "SetVariable";
	/**
	 * Flash Player method name to get a value from a ActionScript variable.
	 */
	public static final String PLAYER_GET_VARIABLE = "GetVariable";

	// HTML node attributes
	/**
	 * HTML DOM API method name to set attributes to elements.
	 */
	public static final String PLAYER_SET_ATTRIBUTE = "SetAttribute";
	// public static final String PLAYER_GET_ATTRIBUTE = "GetAttribute";
	/**
	 * HTML attribute name for Flash Player window mode, which belongs to
	 * <code>object</code> and/or <code>embed</code> tags.
	 * 
	 * @see #V_TRANSPARENT
	 * @see #V_OPAQUE
	 */
	public static final String ATTR_WMODE = "WMode";
	/**
	 * HTML attribute value for Flash Player window mode, which denotes
	 * transparent window mode.
	 * 
	 * @see #ATTR_WMODE
	 */
	public static final String V_TRANSPARENT = "Transparent";
	/**
	 * HTML attribute value for Flash Player window mode, which denotes opaque
	 * window mode.
	 * 
	 * @see #ATTR_WMODE
	 */
	public static final String V_OPAQUE = "Opaque";

	// methods
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_ROOT_NODE = "getRootNode"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_NUM_SUCCESSOR_NODES = "getNumSuccessorNodes"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_SUCCESSOR_NODES = "getSuccessorNodes"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_NUM_CHILD_NODES = "getNumChildNodes"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_CHILD_NODES = "getChildNodes"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_INNER_NODES = "getInnerNodes"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_NEW_MARKER = "newMarker"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_SET_MARKER = "setMarker"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_NODE_FROM_PATH = "getNodeFromPath";//$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_CALL_METHOD = "callMethodA"; //$NON-NLS-1$

	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_SET_FOCUS = "setFocus"; //$NON-NLS-1$
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_UPDATE_TARGET = "updateTarget";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_REPAIR_FLASH = "repairFlash";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_SET_PROPERTY = "setProperty";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_PROPERTY = "getProperty";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_TRANSLATE = "translate";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_NODE_AT_DEPTH = "getNodeAtDepth";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_UNSET_MARKER = "unsetMarker";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_CLEAR_ALL_MARKERS = "clearAllMarkers";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_ON_ROLL_OUT = "onRollOut";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_ON_ROLL_OVER = "onRollOver";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_ON_PRESS = "onPress";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_ON_RELEASE = "onRelease";

	// methods for media control
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_SEARCH_SOUND = "searchSound";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_SEARCH_VIDEO = "searchVideo";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_CURRENT_POSITION = "getCurrentPosition";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_PAUSE = "pause";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_PLAY = "play";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_STOP = "stop";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_SET_VOLUME = "setVolume";
	/**
	 * Name of an ActionScript method, which is defined and exposed by boot
	 * loader Flash component.
	 */
	public static final String M_GET_VOLUME = "getVolume";

	// V9
	/**
	 * Name of an ActionScript method, which we call to put request to the Flash
	 * bridge component.
	 */
	public static final String DISPATCH_METHOD = "dispatchMethod";
	/**
	 * Name of an ActionScript method to get the version of a target SWF. This
	 * method is defined and exposed by Flash boot loader component.
	 */
	public static final String M_GET_SWF_VERSION = "getSwfVersion";
	/**
	 * ActionScript variable name which denotes the content ID of the Flash
	 * movie. The ACTF proxy and boot loader set the ID for authentication
	 * between the program using this class and browser component.
	 */
	public static final String M_GET_CONTENT_ID = "getContentId";
	// V9

	// ASNODE
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the type of the node (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_TYPE = "type"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the class name of the node (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_CLASS_NAME = "className"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the short instance name (for example, <code>baz</code>) of the node
	 * (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_OBJECT_NAME = "objectName"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the full instance name (for example, <code>foo.bar.baz</code>) of the
	 * node (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_TARGET = "target"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the string representation of the node's value (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_VALUE = "value"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the text content of the node (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_TEXT = "text"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the title of the node (of type {@link String}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_TITLE = "title"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the x coordinate of the node (of type {@link Double}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_X = "x"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the y coordinate of the node (of type {@link Double}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_Y = "y"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the width of the node (of type {@link Double}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_WIDTH = "w"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the height of the node (of type {@link Double}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_HEIGHT = "h"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the accessibility information of the node (of type {@link ASAccInfo}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ACCINFO = "accInfo"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the depth (aka z-order) of the node (of type {@link Integer}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_DEPTH = "depth"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the current frame of the node (of type {@link Integer}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_CURRENT_FRAME = "currentFrame"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which is the
	 * flag (of type {@link Boolean}) denotes whether the node is UI Component.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_IS_UI_COMPONENT = "isUIComponent"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which is the
	 * flag (of type {@link Boolean}) denotes whether the node is inputable by
	 * users.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_IS_INPUTABLE = "isInputable"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which is the
	 * flag (of type {@link Boolean}) denotes whether the system should
	 * traverse into its children.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_IS_OPAQUE_OBJECT = "isOpaqueObject"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the id of the node (of type {@link Integer}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ID = "id"; //$NON-NLS-1$
	/**
	 * Property name of instance implementing {@link IASNode}, which denotes
	 * the tab index of the node (of type {@link Integer}).
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_TAB_INDEX = "tabIndex"; //$NON-NLS-1$
	// ASNODE TYPE/CLASS
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_MOVIECLIP = "movieclip";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_OBJECT = "object";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_UNDEFINED = "undefined";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_STRING = "string";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_BOOLEAN = "boolean";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_NULL = "null";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_NUMBER = "number";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_FUNCTION = "function";
	/**
	 * Type name of the ActionScript objects. It is return value of ActionScript
	 * <code>typeof</code> operator.
	 * 
	 * @see IASNode
	 * @see #ASNODE_TYPE
	 */
	public static final String ASNODE_TYPE_DISPLAYOBJECT = "displayobject";
	/**
	 * Class name of the ActionScript objects. Class name is redundant only when
	 * type name of the node is "object".
	 * 
	 * @see IASNode
	 * @see #ASNODE_CLASS_NAME
	 */
	public static final String ASNODE_CLASS_BUTTON = "Button";
	/**
	 * Class name of the ActionScript objects. Class name is redundant only when
	 * type name of the node is "object".
	 * 
	 * @see IASNode
	 * @see #ASNODE_CLASS_NAME
	 */
	public static final String ASNODE_CLASS_ARRAY = "Array";
	/**
	 * Class name of the ActionScript objects. Class name is redundant only when
	 * type name of the node is "object".
	 * 
	 * @see IASNode
	 * @see #ASNODE_CLASS_NAME
	 */
	public static final String ASNODE_CLASS_TEXTFIELD = "TextField";
	/**
	 * Class name of the ActionScript objects. Class name is redundant only when
	 * type name of the node is "object".
	 * 
	 * @see IASNode
	 * @see #ASNODE_CLASS_NAME
	 */
	public static final String ASNODE_CLASS_SHAPE = "Shape";
	// ASNODE ICONTYPE
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_ACCROLE = "accrole_";
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_ACCPROPS = "accprops"; //$NON-NLS-1$
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_FLASH = "flash"; //$NON-NLS-1$
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_OTHERS = "others"; //$NON-NLS-1$
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_TEXT = "text"; //$NON-NLS-1$
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_VARIABLE = "variable"; //$NON-NLS-1$
	/**
	 * Icon type name. It is used to determine an icon of an item in the Flash
	 * Outline View.
	 * 
	 * @see IASNode
	 */
	public static final String ASNODE_ICON_COMPONENT = "component";
	// ASNODE

	// ACCINFO
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means the
	 * ALT text for an ActionScript object.
	 * 
	 */
	public static final String ACCINFO_NAME = "name"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means the
	 * long description for an ActionScript object.
	 * 
	 */
	public static final String ACCINFO_DESCRIPTION = "description"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means the
	 * MSAA widget role of an ActionScript object. It comes from a property of
	 * "._accImpl" object in ActionScrip 2.0, which is not documented. In
	 * ActionScript 3.0, it comes from AccessibilityImplementation class.
	 * 
	 */
	public static final String ACCINFO_ROLE = "role"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means the
	 * MSAA widget state of an ActionScript object. It comes from a property of
	 * "._accImpl" object in ActionScrip 2.0, which is not documented. In
	 * ActionScript 3.0, it comes from AccessibilityImplementation class.
	 * 
	 */
	public static final String ACCINFO_STATE = "state"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means
	 * whether an ActionScript object is ignored by screen readers.
	 * 
	 */
	public static final String ACCINFO_SILENT = "silent"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means
	 * whether children of an ActionScript object is ignored by screen readers.
	 * 
	 */
	public static final String ACCINFO_FORCESIMPLE = "forceSimple"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means
	 * shortcut key for an ActionScript object.
	 * 
	 */
	public static final String ACCINFO_SHORTCUT = "shortcut"; //$NON-NLS-1$
	/**
	 * One of the property names of {@link ASAccInfo} instance, which means the
	 * widget's MSAA default action of an ActionScript object. It comes from a
	 * property of "._accImpl" object in ActionScrip 2.0, which is not
	 * documented. In ActionScript 3.0, it comes from
	 * AccessibilityImplementation class.
	 * 
	 */
	public static final String ACCINFO_DEFAULTACTION = "defaultAction"; //$NON-NLS-1$
	// public static final String ACCINFO_CHILDREN = "children"; //$NON-NLS-1$
	// ACCINFO

}

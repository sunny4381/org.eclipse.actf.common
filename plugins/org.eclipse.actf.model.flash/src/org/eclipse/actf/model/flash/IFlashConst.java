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

public interface IFlashConst {

	// general
	public static final String CLSID_FLASH = "{D27CDB6E-AE6D-11CF-96B8-444553540000}";
	public static final int INVALID_DEPTH = -16384;
	public static final String PLAYER_VERSION = "$version";

	// Browser Ready State
	public static final String READY_STATE = "readyState";
	public static final Integer COMPLETED_READY_STATE = Integer.valueOf(4);

	// initialize
	public static final String PATH_ROOTLEVEL = "_level0"; //$NON-NLS-1$
	public static final String PATH_BRIDGELEVEL = "_level53553"; //$NON-NLS-1$
	public static final String PATH_IS_AVAILABLE = ".Eclipse_ACTF_is_available"; //$NON-NLS-1$
	public static final String PATH_CONTENT_ID = ".Eclipse_ACTF_SWF_CONTENT_ID";

	// other paths
	public static final String PATH_GLOBAL = "_global"; //$NON-NLS-1$
	public static final String PATH_ON_PRESS = ".onPress";
	public static final String PATH_ON_RELEASE = ".onRelease";

	// V8
	public static final String PROP_REQUEST_ARGS = ".Eclipse_ACTF_request_args";
	public static final String PROP_RESPONSE_VALUE = ".Eclipse_ACTF_response_value";
	public static final String PLAYER_SET_VARIABLE = "SetVariable";
	public static final String PLAYER_GET_VARIABLE = "GetVariable";

	// HTML node attributes
	public static final String PLAYER_SET_ATTRIBUTE = "SetAttribute";
	// public static final String PLAYER_GET_ATTRIBUTE = "GetAttribute";
	public static final String ATTR_WMODE = "WMode";
	public static final String V_TRANSPARENT = "Transparent";
	public static final String V_OPAQUE = "Opaque";

	// methods
	public static final String M_GET_ROOT_NODE = "getRootNode"; //$NON-NLS-1$
	public static final String M_GET_NUM_SUCCESSOR_NODES = "getNumSuccessorNodes"; //$NON-NLS-1$
	public static final String M_GET_SUCCESSOR_NODES = "getSuccessorNodes"; //$NON-NLS-1$
	public static final String M_GET_NUM_CHILD_NODES = "getNumChildNodes"; //$NON-NLS-1$
	public static final String M_GET_CHILD_NODES = "getChildNodes"; //$NON-NLS-1$
	public static final String M_GET_INNER_NODES = "getInnerNodes"; //$NON-NLS-1$
	public static final String M_NEW_MARKER = "newMarker"; //$NON-NLS-1$
	public static final String M_SET_MARKER = "setMarker"; //$NON-NLS-1$
	public static final String M_GET_NODE_FROM_PATH = "getNodeFromPath";//$NON-NLS-1$
	public static final String M_CALL_METHOD = "callMethodA"; //$NON-NLS-1$

	public static final String M_SET_FOCUS = "setFocus"; //$NON-NLS-1$
	public static final String M_UPDATE_TARGET = "updateTarget";
	public static final String M_REPAIR_FLASH = "repairFlash";
	public static final String M_SET_PROPERTY = "setProperty";
	public static final String M_GET_PROPERTY = "getProperty";
	public static final String M_TRANSLATE = "translate";
	public static final String M_GET_NODE_AT_DEPTH = "getNodeAtDepth";
	public static final String M_UNSET_MARKER = "unsetMarker";
	public static final String M_CLEAR_ALL_MARKERS = "clearAllMarkers";
	public static final String M_ON_ROLL_OUT = "onRollOut";
	public static final String M_ON_ROLL_OVER = "onRollOver";
	public static final String M_ON_PRESS = "onPress";
	public static final String M_ON_RELEASE = "onRelease";

	// methods for media control
	public static final String M_SEARCH_SOUND = "searchSound";
	public static final String M_SEARCH_VIDEO = "searchVideo";
	public static final String M_GET_CURRENT_POSITION = "getCurrentPosition";
	public static final String M_PAUSE = "pause";
	public static final String M_PLAY = "play";
	public static final String M_STOP = "stop";
	public static final String M_SET_VOLUME = "setVolume";
	public static final String M_GET_VOLUME = "getVolume";

	// V9
	public static final String DISPATCH_METHOD = "dispatchMethod";
	public static final String M_GET_SWF_VERSION = "getSwfVersion";
	public static final String M_GET_CONTENT_ID = "getContentId";
	// V9

	// ASNODE
	public static final String ASNODE_TYPE = "type"; //$NON-NLS-1$
	public static final String ASNODE_CLASS_NAME = "className"; //$NON-NLS-1$
	public static final String ASNODE_OBJECT_NAME = "objectName"; //$NON-NLS-1$
	public static final String ASNODE_TARGET = "target"; //$NON-NLS-1$
	public static final String ASNODE_VALUE = "value"; //$NON-NLS-1$
	public static final String ASNODE_TEXT = "text"; //$NON-NLS-1$
	public static final String ASNODE_TITLE = "title"; //$NON-NLS-1$
	public static final String ASNODE_X = "x"; //$NON-NLS-1$
	public static final String ASNODE_Y = "y"; //$NON-NLS-1$
	public static final String ASNODE_WIDTH = "w"; //$NON-NLS-1$
	public static final String ASNODE_HEIGHT = "h"; //$NON-NLS-1$
	public static final String ASNODE_ACCINFO = "accInfo"; //$NON-NLS-1$
	public static final String ASNODE_DEPTH = "depth"; //$NON-NLS-1$
	public static final String ASNODE_CURRENT_FRAME = "currentFrame"; //$NON-NLS-1$
	public static final String ASNODE_IS_UI_COMPONENT = "isUIComponent"; //$NON-NLS-1$
	public static final String ASNODE_IS_INPUTABLE = "isInputable"; //$NON-NLS-1$
	public static final String ASNODE_IS_OPAQUE_OBJECT = "isOpaqueObject"; //$NON-NLS-1$
	public static final String ASNODE_ID = "id"; //$NON-NLS-1$
	public static final String ASNODE_TAB_INDEX = "tabIndex"; //$NON-NLS-1$
	// ASNODE TYPE/CLASS
	public static final String ASNODE_TYPE_MOVIECLIP = "movieclip";
	public static final String ASNODE_TYPE_OBJECT = "object";
	public static final String ASNODE_TYPE_UNDEFINED = "undefined";
	public static final String ASNODE_TYPE_STRING = "string";
	public static final String ASNODE_TYPE_BOOLEAN = "boolean";
	public static final String ASNODE_TYPE_NULL = "null";
	public static final String ASNODE_TYPE_NUMBER = "number";
	public static final String ASNODE_TYPE_FUNCTION = "function";
	public static final String ASNODE_TYPE_DISPLAYOBJECT = "displayobject";
	public static final String ASNODE_CLASS_BUTTON = "Button";
	public static final String ASNODE_CLASS_ARRAY = "Array";
	public static final String ASNODE_CLASS_TEXTFIELD = "TextField";
	public static final String ASNODE_CLASS_SHAPE = "Shape";
	// ASNODE ICONTYPE
	public static final String ASNODE_ICON_ACCROLE = "accrole_";
	public static final String ASNODE_ICON_ACCPROPS = "accprops"; //$NON-NLS-1$
	public static final String ASNODE_ICON_FLASH = "flash"; //$NON-NLS-1$
	public static final String ASNODE_ICON_OTHERS = "others"; //$NON-NLS-1$
	public static final String ASNODE_ICON_TEXT = "text"; //$NON-NLS-1$
	public static final String ASNODE_ICON_VARIABLE = "variable"; //$NON-NLS-1$
	public static final String ASNODE_ICON_COMPONENT = "component";
	// ASNODE

	// ACCINFO
	public static final String ACCINFO_NAME = "name"; //$NON-NLS-1$
	public static final String ACCINFO_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ACCINFO_ROLE = "role"; //$NON-NLS-1$
	public static final String ACCINFO_STATE = "state"; //$NON-NLS-1$
	public static final String ACCINFO_SILENT = "silent"; //$NON-NLS-1$
	public static final String ACCINFO_FORCESIMPLE = "forceSimple"; //$NON-NLS-1$
	public static final String ACCINFO_SHORTCUT = "shortcut"; //$NON-NLS-1$
	public static final String ACCINFO_DEFAULTACTION = "defaultAction"; //$NON-NLS-1$
	// public static final String ACCINFO_CHILDREN = "children"; //$NON-NLS-1$
	// ACCINFO

}

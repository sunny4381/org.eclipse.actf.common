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

	//general
	public static final String CLSID_FLASH = "{D27CDB6E-AE6D-11CF-96B8-444553540000}";
	public static final String WMODE = "WMode";
	
    public static final Integer COMPLETED_READY_STATE = Integer.valueOf(4);


	//messages
	public static final String ERROR_OK = "OK: "; //$NON-NLS-1$
	public static final String ERROR_NG = "NG: "; //$NON-NLS-1$
	public static final String ERROR_NA = "NA: "; //$NON-NLS-1$
	public static final String ERROR_WAIT = "WAIT: "; //$NON-NLS-1$	

	//initialize
	public static final String PATH_ROOTLEVEL = "_level0"; //$NON-NLS-1$
	public static final String PATH_BRIDGELEVEL = "_level53553"; //$NON-NLS-1$
	public static final String PATH_IS_AVAILABLE = ".Eclipse_ACTF_is_available"; //$NON-NLS-1$
	public static final String PATH_CONTENT_ID = ".Eclipse_ACTF_SWF_CONTENT_ID";

	//V8
	public static final String PROP_REQUEST_ARGS = ".Eclipse_ACTF_request_args";
	public static final String PROP_RESPONSE_VALUE = ".Eclipse_ACTF_response_value";
	public static final String PLAYER_SET_VARIABLE = "SetVariable";
	public static final String PLAYER_GET_VARIABLE = "GetVariable";
	//attributes
	public static final String PLAYER_SET_ATTRIBUTE = "SetAttribute";
	public static final String PLAYER_GET_ATTRIBUTE = "GetAttribute";
	public static final String ATTR_ERROR = "aDesignerError";
	public static final String ATTR_MARKER = "marker";

	
	//methods
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

	//V9
	public static final String DISPATCH_METHOD = "dispatchMethod";
	public static final String M_GET_SWF_VERSION = "getSwfVersion";
	public static final String M_GET_CONTENT_ID = "getContentId";
	//V9

	
}

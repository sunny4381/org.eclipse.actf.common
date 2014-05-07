/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO, Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
// --------------------------------------------------------------------------------
//  Common Bridge Initialization Module
// --------------------------------------------------------------------------------
_root.Eclipse_ACTF_is_available = "true";
_root.Eclipse_ACTF_SWF_Bridge_version = "1.2.0";

// Used by RequestServer.
_root.Eclipse_ACTF_request_args = "";
_root.Eclipse_ACTF_response_value = "";
_root.Eclipse_ACTF_SWF_CONTENT_ID = _global.Eclipse_ACTF_SWF_CONTENT_ID;
// ExternalInterface.addCallback('getSwfVersion', null, Eclipse_ACTF_Controller.getSwfVersion);
Eclipse_ACTF_RequestServer.start(_global.Eclipse_ACTF_SECRET);
delete _global.Eclipse_ACTF_SECRET;

Eclipse_ACTF_Init.initialize();

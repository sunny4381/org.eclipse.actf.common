/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Daisuke SATO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash;

import org.eclipse.actf.model.flash.as.ASObject;

public class FlashAccInfo {

	private ASObject accInfo;

	public FlashAccInfo(ASObject parent) {
		Object result = parent.get(ASObject.ASNODE_ACCINFO);
		if (result instanceof ASObject) {
			accInfo = (ASObject) result;
		}
	}

	public void dispose() {

	}

	public int getAccRole() {
		if (null != accInfo) {
			Object objRole = accInfo.get(ASObject.ACCINFO_ROLE);
			if (objRole instanceof Integer) {
				return (Integer) objRole;
			}
		}
		return -1;
	}

	public boolean isSilent() {
		if (null != accInfo) {
			Object objSilent = accInfo.get(ASObject.ACCINFO_SILENT);
			if (objSilent instanceof Boolean) {
				return (Boolean) objSilent;
			}
		}
		return false;
	}

	public String getAccName() {
		if (null != accInfo) {
			Object objName = accInfo.get(ASObject.ACCINFO_NAME);
			if (objName instanceof String) {
				return (String) objName;
			}
		}
		return null;
	}

	public String getAccDescription() {
		if (null != accInfo) {
			Object objDesc = accInfo.get(ASObject.ACCINFO_DESCRIPTION);
			if (objDesc instanceof String) {
				return (String) objDesc;
			}
		}
		return null;
	}

}

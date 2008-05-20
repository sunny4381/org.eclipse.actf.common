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

import java.util.Set;

import org.eclipse.actf.model.flash.as.ASObject;

public class FlashAccInfo {

	private ASObject accInfo;

	private FlashAccInfo(ASObject accInfo) {
		this.accInfo = accInfo;
	}

	public static FlashAccInfo create(ASObject target) {
		Object result = target.get(ASObject.ASNODE_ACCINFO);
		if (result instanceof ASObject) {
			return new FlashAccInfo((ASObject) result);
		}
		return null;
	}

	public int getRole() {
		Object obj = accInfo.get(ASObject.ACCINFO_ROLE);
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		return -1;
	}

	public int getState() {
		Object obj = accInfo.get(ASObject.ACCINFO_STATE);
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		return -1;
	}

	public boolean isSilent() {
		Object obj = accInfo.get(ASObject.ACCINFO_SILENT);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	public boolean isForceSimple() {
		Object obj = accInfo.get(ASObject.ACCINFO_FORCESIMPLE);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	public String getName() {
		Object obj = accInfo.get(ASObject.ACCINFO_NAME);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	public String getDescription() {
		Object obj = accInfo.get(ASObject.ACCINFO_DESCRIPTION);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	public String getShortcut() {
		Object obj = accInfo.get(ASObject.ACCINFO_SHORTCUT);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	public String getDefaultAction() {
		Object obj = accInfo.get(ASObject.ACCINFO_DEFAULTACTION);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	public Object get(String prop) {
		return accInfo.get(prop);
	}

	public Set<String> getKeys() {
		return accInfo.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (accInfo != null) {
			return accInfo.toString();
		} else {
			return "";
		}
	}

}

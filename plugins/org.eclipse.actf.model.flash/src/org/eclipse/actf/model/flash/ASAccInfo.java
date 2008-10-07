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

/**
 * ASAccInfo stores accessibility information of ActionScript
 * <code>Object</code>.
 * 
 * @see ASObject
 */
public class ASAccInfo {

	private ASObject accInfo;

	private ASAccInfo(ASObject accInfo) {
		this.accInfo = accInfo;
	}

	/**
	 * Create {@link ASAccInfo} from {@link ASObject}
	 * 
	 * @param target
	 *            target {@link ASObject}
	 * @return {@link ASAccInfo} or null if not available
	 */
	public static ASAccInfo create(ASObject target) {
		Object result = target.get(IFlashConst.ASNODE_ACCINFO);
		if (result instanceof ASObject) {
			return new ASAccInfo((ASObject) result);
		}
		return null;
	}

	/**
	 * @return role
	 * @see IFlashConst#ACCINFO_ROLE
	 */
	public int getRole() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_ROLE);
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		return -1;
	}

	/**
	 * @return state
	 * @see IFlashConst#ACCINFO_STATE
	 */
	public int getState() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_STATE);
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		return -1;
	}

	/**
	 * @return isSilent
	 * @see IFlashConst#ACCINFO_SILENT
	 */
	public boolean isSilent() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_SILENT);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	/**
	 * @return isForceSimple
	 * @see IFlashConst#ACCINFO_FORCESIMPLE
	 */
	public boolean isForceSimple() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_FORCESIMPLE);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	/**
	 * @return name
	 * @see IFlashConst#ACCINFO_NAME
	 */
	public String getName() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_NAME);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	/**
	 * @return description
	 * @see IFlashConst#ACCINFO_DESCRIPTION
	 */
	public String getDescription() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_DESCRIPTION);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	/**
	 * @return shortcut
	 * @see IFlashConst#ACCINFO_SHORTCUT
	 */
	public String getShortcut() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_SHORTCUT);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	/**
	 * @return default action
	 * @see IFlashConst#ACCINFO_DEFAULTACTION
	 */
	public String getDefaultAction() {
		Object obj = accInfo.get(IFlashConst.ACCINFO_DEFAULTACTION);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	/**
	 * Get the value for a given key.
	 * 
	 * @param prop
	 *            name of a key
	 * @return the value for the given key
	 */
	public Object get(String prop) {
		return accInfo.get(prop);
	}

	/**
	 * Gets the set of keys of the target AS Object.
	 * 
	 * @return The {@link Set} of the keys.
	 */
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

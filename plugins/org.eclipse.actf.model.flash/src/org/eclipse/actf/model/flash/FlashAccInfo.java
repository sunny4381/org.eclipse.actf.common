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

public class FlashAccInfo {

	private FlashNode parent;
	private Boolean hasOnRelease;
	private Boolean isSilent;
	private Integer accRole;

	public FlashAccInfo(FlashNode parent) {
		this.parent = parent;
	}

	public void dispose() {

	}

	public boolean hasOnRelease() {
		if (null == hasOnRelease) {
			FlashNode onReleaseNode = parent.getNode("onRelease"); //$NON-NLS-1$
			if (null != onReleaseNode) {
				hasOnRelease = Boolean.TRUE;
				onReleaseNode.dispose();
			} else {
				hasOnRelease = Boolean.FALSE;
			}
		}
		return hasOnRelease.booleanValue();
	}

	public int getAccRole() {
		if (null == accRole) {
			Object objRole = parent.getPlayer().callMethod(
					parent.getTarget() + "._accImpl", "get_accRole", 0); //$NON-NLS-1$ //$NON-NLS-2$
			if (objRole != null)
				return (Integer) objRole;
		}
		return -1;
	}

	public boolean isSilent() {
		if (null == isSilent) {
			FlashNode accSilentNode = parent.getNode("_accProps.silent"); //$NON-NLS-1$
			if (null != accSilentNode) {
				isSilent = new Boolean("true".equals(accSilentNode.getValue())); //$NON-NLS-1$
				accSilentNode.dispose();
			} else {
				isSilent = Boolean.FALSE;
			}
		}
		return isSilent.booleanValue();
	}

	public String getAccName() {
		Object objName = parent.getPlayer().callMethod(
				parent.getTarget() + "._accImpl", "get_accName", 0); //$NON-NLS-1$ //$NON-NLS-2$
		if (objName != null)
			return (String) objName;

		FlashNode accNameNode = parent.getNode("_accProps.name"); //$NON-NLS-1$
		if (null != accNameNode) {
			try {
				return accNameNode.getValue();
			} finally {
				accNameNode.dispose();
			}
		}
		return null;
	}

	public String getAccDescription() {
		FlashNode accDescriptionNode = parent.getNode("_accProps.description"); //$NON-NLS-1$
		if (null != accDescriptionNode) {
			try {
				return accDescriptionNode.getValue();
			} finally {
				accDescriptionNode.dispose();
			}
		}
		return null;
	}

}

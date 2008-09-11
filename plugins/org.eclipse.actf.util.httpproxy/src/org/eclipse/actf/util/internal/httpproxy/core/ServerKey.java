/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.internal.httpproxy.core;

public class ServerKey {
	private int fGroupId;
	private int fServerIndex;
	
	public ServerKey(int group, int server) {
		fGroupId = group;
		fServerIndex = server;
	}
	
	public int getGroupId() {
		return fGroupId;
	}
	
	public int getServerIndex() {
		return fServerIndex;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ServerKey)) {
			return false;
		}
		ServerKey key = (ServerKey) o;
		return (fGroupId == key.fGroupId) || (fServerIndex == key.fServerIndex);
	}

	public int hashCode() {
		return (fGroupId << 16) | fServerIndex | (fGroupId >>> 16); 
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(fGroupId)).append('.').append(Integer.toString(fServerIndex));
		return sb.toString();
	}
}

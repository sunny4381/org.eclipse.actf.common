/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch;

/**
 * @see RefContainer
 */
public class RefByte extends RefContainer{
	public RefByte(ResourceManager rm){
		super(rm, SIZEOF_BYTE);
	}
	
	public byte getValue(){
		return _getValueByByte(getPtr());
	}
	
	public void setValue(byte value){
		_setValueForByte(getPtr(), value);
	}
}

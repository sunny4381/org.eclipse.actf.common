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
public class RefShort extends RefContainer{
	public RefShort(ResourceManager rm){
		super(rm, SIZEOF_SHORT);
	}
	
	public short getValue(){
		return _getValueByShort(getPtr());
	}
	
	public void setValue(short value){
		_setValueForShort(getPtr(), value);
	}
}

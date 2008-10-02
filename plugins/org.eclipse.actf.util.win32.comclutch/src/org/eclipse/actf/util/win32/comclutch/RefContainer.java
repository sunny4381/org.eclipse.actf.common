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

import org.eclipse.actf.util.win32.comclutch.impl.IResourceImpl;

/**
 * RefContainer is abstract class of referenced variables.
 * The instance of the RefContainer is used for call by reference.
 * You don't have to care about the resource management, because
 * the ResourceManager treat the resource life cycle.
 * You can release the resource if you don't need to keep it.
 */
public abstract class RefContainer extends IResourceImpl {

	protected static final int SIZEOF_BOOLEAN = 1;
	protected static final int SIZEOF_BYTE = 1;
	protected static final int SIZEOF_SHORT = 2;
	protected static final int SIZEOF_INT = 4;
	protected static final int SIZEOF_LONG = 8;
	protected static final int SIZEOF_FLOAT = 4;
	protected static final int SIZEOF_DOUBLE = 8;
	protected static final int SIZEOF_IUNKOWN = 8;
	protected static final int SIZEOF_OBJECT = 8;
	protected static final int SIZEOF_VOIDPTR = 8;

	private long ptr;

	private native void _free(long ptr);
	private native long _calloc(int size);
	
	protected native boolean _getValueByBoolean(long ptr);
	protected native byte _getValueByByte(long ptr);
	protected native short _getValueByShort(long ptr);
	protected native int _getValueByInt(long ptr);
	protected native long _getValueByLong(long ptr);
	protected native float _getValueByFloat(long ptr);
	protected native double _getValueByDouble(long ptr);
	protected native long _getValueByIUnknown(long ptr);
	protected native Object _getValueByObject(long ptr);
	protected native long _getValueByVoidPtr(long ptr);
	protected native String _getValueByString(long ptr);

	protected native void _setValueForBoolean(long ptr, boolean value);
	protected native void _setValueForByte(long ptr, byte value);
	protected native void _setValueForShort(long ptr, short value);
	protected native void _setValueForInt(long ptr, int value);
	protected native void _setValueForLong(long ptr, long value);
	protected native void _setValueForFloat(long ptr, float value);
	protected native void _setValueForDouble(long ptr, double value);
	protected native void _setValueForIUnknown(long ptr, long value);
	protected native void _setValueForObject(long ptr, Object value);
	protected native void _setValueForVoidPtr(long ptr, long value);
	protected native void _setValueForString(long ptr, String value);
	
	protected RefContainer(ResourceManager rm, int size) {
        super(rm, false);
		ptr = _calloc(size);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.util.win32.comclutch.IResource#getPtr()
	 */
	public long getPtr() {
		return ptr;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.util.win32.comclutch.impl.IResourceImpl#release()
	 */
	public void release() {
		_free(ptr);
	}
}
